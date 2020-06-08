package org.kiharu.hareru.dmhy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;
import org.kiharu.hareru.constant.DmhyConstants;
import org.kiharu.hareru.entity.DmhyResource;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 解析接口的返回结果
 *
 * @Author kiharu
 * @Date 2020-06-08
 */
@Slf4j
public class DmhyResultParser {

    /**
     * 解析资源分页查询结果
     * @param htmlResp
     */
    public static List<DmhyResource> parseTopicPageResult(String htmlResp) {
        List<DmhyResource> result = new ArrayList<>();
        if (StringUtils.isBlank(htmlResp)) {
            return result;
        }

        Document document = Jsoup.parse(htmlResp);
        Elements tbody = document.getElementsByTag("tbody");

        Element tbodyContent = tbody.get(0);

        Elements trList = tbodyContent.getElementsByTag("tr");
        for (int i = 0; i < trList.size(); ++i) {
            Element tr = trList.get(i);
            DmhyResource dmhyResource = new DmhyResource();
            // tr标签下是多个td标签，每个td标签内容对应不同的值
            Elements tdList = tr.getElementsByTag("td");
            for (int j = 0; j < tdList.size(); ++j) {
                Element td = tdList.get(j);

                // 对tr下的每个td标签做对应的处理
                switch(j) {
                    case 0:
                        // 获取资源上传时间
                        Element uploadTimeSpan = td.getElementsByTag("span").first();
                        String uploadTimeStr = uploadTimeSpan.html();
                        dmhyResource.setUploadTimeStr(uploadTimeStr);
                        // 转换成Date类型
                        if (StringUtils.isNotBlank(uploadTimeStr)) {
                            try {
                                Date uploadTime = DateUtils.parseDate(uploadTimeStr, DmhyConstants.UPLOAD_TIME_FORMAT);
                                dmhyResource.setUploadTime(uploadTime);
                            } catch (ParseException pex) {
                                StringBuilder errorMsg = new StringBuilder()
                                        .append("解析资源上传时间uploadTimeStr=")
                                        .append(uploadTimeStr)
                                        .append("出错");
                                log.error(errorMsg.toString(), pex);
                            }
                        }
                        break;
                    case 1:
                        // 获取分类
                        Element a = td.getElementsByTag("a").first();
                        String href = a.attr("href");
                        if (StringUtils.isNotBlank(href) && href.contains("/")) {
                            String sortId = href.substring(href.lastIndexOf("/") + 1);
                            dmhyResource.setSortId(sortId);
                        }
                        Element font = td.getElementsByTag("font").first();
                        String sort = font.html();
                        dmhyResource.setSort(sort);
                        break;
                    case 2:
                        // 获取字幕组和资源名称信息--TODO--这里需要确定如果没有明确字幕组是怎样的
                        // 这里可能包含三个部分，分别是字幕组信息<span class="tag">、资源链接及标题信息<a>、评论数量信息<span>

                        // <span>是字幕组信息或者评论数信息，需具体进行判断区别
                        Elements spanList = td.getElementsByTag("span");
                        for (int k = 0; k < spanList.size(); ++k) {
                            Element span = spanList.get(k);
                            if (span.hasAttr("class")) {
                                // 拥有class="tag"的span是字幕组信息
                                Element teamElement = span.getElementsByTag("a").first();
                                String teamName = teamElement.html();
                                dmhyResource.setTeamName(teamName);
                                String teamIdHref = teamElement.attr("href");
                                if (StringUtils.isNotBlank(teamIdHref) && teamIdHref.contains("/")) {
                                    String teamId = teamIdHref.substring(teamIdHref.lastIndexOf("/") + 1);
                                    dmhyResource.setTeamId(teamId);
                                }
                            } else {
                                // 否则，就是评论数量信息，也即：”約2條評論“
                                String commentCountStr = span.html();
                                String commentCount = StringUtils.getDigits(commentCountStr);
                                if (StringUtils.isNotEmpty(commentCount)) {
                                    dmhyResource.setCommentCount(Integer.valueOf(commentCount));
                                }
                            }
                        }

                        // 这个td下面直属的<a>标签是资源链接及标题信息
                        Element linkAndTitleInfoA = td.getElementsByTag("a").last();
                        String title = linkAndTitleInfoA.html();
                        dmhyResource.setTitle(title);
                        String linkHref = linkAndTitleInfoA.attr("href");
                        String detailLink = DmhyConstants.URL_HOME_PAGE + linkHref;
                        dmhyResource.setDetailLink(detailLink);
                        // 从/topics/view/542148_04_10_1080P_MP4.html获取topicId，也即542148
                        if (StringUtils.isNotBlank(linkHref) && linkHref.contains("/")) {
                            String topicIdHtml = linkHref.substring(linkHref.lastIndexOf("/") + 1);
                            if (StringUtils.isNotBlank(topicIdHtml) && topicIdHtml.contains("_")) {
                                String topicId = topicIdHtml.substring(0, topicIdHtml.indexOf("_"));
                                dmhyResource.setTopicId(topicId);
                            }
                        }
                        break;
                    case 3:
                        // 外部磁力链接
                        Element externalMagnetElement = td.getElementsByTag("a").first();
                        String externalMagnet = externalMagnetElement.attr("href");
                        dmhyResource.setExternalMagnet(externalMagnet);
                        break;
                    case 4:
                        // 文件大小--TODO--以后这里需要再更具体处理，目前只是带单位的字符串格式的
                        String sizeStr = td.html();
                        dmhyResource.setSize(sizeStr);
                        break;
                    case 5:
                        // 种子数量
                        String seedCountStr = td.html();
                        if (StringUtils.isNotBlank(seedCountStr) && StringUtils.isNumeric(seedCountStr)) {
                            dmhyResource.setSeedCount(Integer.valueOf(seedCountStr));
                        }
                        break;
                    case 6:
                        // 正在下载的数量
                        String downloadingCountStr = td.html();
                        if (StringUtils.isNotBlank(downloadingCountStr) && StringUtils.isNumeric(downloadingCountStr)) {
                            dmhyResource.setDownloadingCount(Integer.valueOf(downloadingCountStr));
                        }
                        break;
                    case 7:
                        // 已经完成的数量
                        String downloadedCountStr = td.html();
                        if (StringUtils.isNotBlank(downloadedCountStr) && StringUtils.isNumeric(downloadedCountStr)) {
                            dmhyResource.setDownloadedCount(Integer.valueOf(downloadedCountStr));
                        }
                        break;
                    case 8:
                        // 发布人姓名
                        Element uploadUserNameA = td.getElementsByTag("a").first();
                        String uploadUserName = uploadUserNameA.html();
                        dmhyResource.setUploadUserName(uploadUserName);
                        break;
                    default:
                        break;
                }
            }

            result.add(dmhyResource);
        }

        return result;
    }

    /**
     * 解析详情页面返回结果
     * @param htmlResp
     * @return
     */
    public static DmhyResource parseDetailInfo(String htmlResp) {
        DmhyResource result = new DmhyResource();
        if (StringUtils.isBlank(htmlResp)) {
            return result;
        }

        Document document = Jsoup.parse(htmlResp);
        Element resourceTabs = document.body().getElementById("resource-tabs");
        Element tabs1 = resourceTabs.getElementById("tabs-1");
        Elements pElements = tabs1.getElementsByTag("p");
        // 解析多个不同链接
        for (int i = 0; i < pElements.size(); ++i) {
            Element p = pElements.get(i);

            // 根据顺序进行解析
            switch(i) {
                case 0:
                    // 会员专用链接
                    Element memberLinkA = p.getElementsByTag("a").first();
                    String memberLink = memberLinkA.attr("href");
                    result.setMemberMagnet(DmhyConstants.HTTPS + memberLink);
                    break;
                case 1:
                    // Magnet链接
                    Element magnetLinkA = p.getElementById("a_magnet");
                    String moreInfoMagnet = magnetLinkA.attr("href");
                    String magnet = magnetLinkA.html();
                    result.setMoreInfoMagnet(moreInfoMagnet);
                    result.setMagnet(magnet);
                    break;
                case 2:
                    // Magnet链接type-II
                    Element magnetTypeIIA = p.getElementById("magnet2");
                    String magnetTypeII = magnetTypeIIA.attr("href");
                    result.setMagnetTypeII(magnetTypeII);
                    break;
                case 3:
                    // 弹幕播放链接
                    Element ddplayA = p.getElementById("ddplay");
                    String ddplayUrl = ddplayA.html();
                    result.setDdplayUrl(ddplayUrl);
                    break;
                case 4:
                    // 外部搜索链接
                    Element googleSearchA = p.getElementsByTag("a").first();
                    String googleSearchUrl = googleSearchA.attr("href");
                    result.setGoogleSearchUrl(googleSearchUrl);
                    break;

            }
        }

        List<String> fileList = new ArrayList<>();
        List<String> fileSizeList = new ArrayList<>();

        // 获取该资源中的文件列表及对应文件大小
        Element fileListElement = tabs1.getElementsByClass("file_list").first();
        Elements liList = fileListElement.getElementsByTag("li");
        for (int i = 0; i < liList.size(); ++i) {
            Element li = liList.get(i);
            Element span = li.getElementsByTag("span").first();
            String fileSize = span.html();


            // 注意这里取得的结果是"[BeanSub&FZSD&LoliHouse] Yesterday wo Utatte - 09 [WebRip 1080P HEVC-10bit AAC ASSx2].mkv 302.6MB"这样的，
            // 大小也会包含在其中，所以需要进一步的处理
            String fileName = li.text();
            if (StringUtils.isNotBlank(fileName) && fileName.contains(" ")) {
                fileName = fileName.substring(0, fileName.lastIndexOf(" "));
            }

            // 这里需要注意保证文件数量与文件大小的数量一致
            fileList.add(fileName);
            fileSizeList.add(fileSize);
        }
        result.setFileList(fileList);
        result.setFileSizeList(fileSizeList);

        return result;
    }


}
