package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.bo.PixivAjaxIllustPagesUrlInfoBO;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivAuthorProfileBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.entity.PixivPictureDetailInfo;
import org.kiharu.hareru.entity.PixivPictureInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类用于下载Pixiv图片相关的信息，也即下载的辅助类
 */
@Slf4j
@Service
public class PixivPictureUtils {

    /**
     * 根据pixivId从artworks中获取的内容中提取出图片的原始大图地址
     * https://www.pixiv.net/artworks/76211609
     * 注意，这个方法只能获取pixivId对应的第一张图片，如果该 pixivId有多张图片，则无法获取其他图片
     *
     * 用getUrlsFromArtworksByPixivId()来替代当前这个方法
     * @param pixivId
     * @return https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg
     */
    @Deprecated
    public static String getUrlFromArtworksByPixivId(String pixivId) {
        String result = null;
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        // 匹配https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg这样的原图
        String regex = PixivConstants.PIXIV_REGEX_ARTWORKS_IMG_ORIGINAL;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(respHtml);

        if (matcher.find()) {
            result = matcher.group();
        }

        return result;
    }


    /**
     * 获取该pixivId对应的所有图片的原始大图地址
     * @param pixivId
     * @return
     */
    public static List<String> getUrlsFromArtworksByPixivId(String pixivId) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isEmpty(pixivId)) {
            return result;
        }
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        if (StringUtils.isEmpty(content)) {
            return result;
        }
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return result;
        }

        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();
        Integer pageCount = detailInfoBO.getPageCount();
        String originalUrl = detailInfoBO.getOriginalUrl();

        result = new ArrayList<>(pageCount + 8);
        // 如果该pixivId只对应一张图片，则只把当前请求到原始大图返回，不需要再次请求接口ajax/illust/{pixivId}/pages接口
        if (pageCount == 1 && StringUtils.isNotEmpty(originalUrl)) {
            result.add(originalUrl);
            return result;
        }
        // 如果该pixivId对应多张图片，则需再次请求https://www.pixiv.net/ajax/illust/{pixivId}/pages接口进行获取所有图片原始大图地址
        result = getUrlsFromAjaxIllustPagesByPixivId(pixivId);
        return result;
    }

    /**
     * 根据pixivId获取其关联推荐的所有其他图片pixivId
     * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
     * TODO--方法名取错了，或者用另外一个方法来实现下载功能
     * @param pixivId
     * @return
     */
    public static List<String> getPixivIdsFromAjaxIllustRecommend(String pixivId) {
        List<String> pixivIdList = new ArrayList<>();

        // 获取接口返回结果
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxIllustInit(pixivId).orElse("");

        // 解析接口返回结果以便获取所关联推荐的所有图片的pixivId
        pixivIdList = PixivResultParser.getPixivIdsFromRespStr(respJSONStr);
        // 把pixivId本身也添加进去
        pixivIdList.add(pixivId);

        return pixivIdList;
    }

    /**
     * 根据多个pixivId获取这些所关联推荐的所有图片pixivId
     * @param pixivIdList
     * @return
     */
    public static Set<String> getRecommendPixivIdSetByPixivIdList(List<String> pixivIdList) {
        Set<String> result = new HashSet<>(64);
        if (CollectionUtils.isEmpty(pixivIdList)) {
            log.warn("getRecommendPixivIdSetByPixivIdList方法的入参pixivIdList为空");
            return result;
        }

        // 获取关联推荐图片出错的pixivId集合
        List<String> errorPixivIdList = new ArrayList<>(16);


        for (String pixivId : pixivIdList) {
            try {
                List<String> singleRecommendPixivIdList = getPixivIdsFromAjaxIllustRecommend(pixivId);
                result.addAll(singleRecommendPixivIdList);
            } catch (Exception ex) {
                // 如果获取其中一个pixivId的关联推荐图片时出错，则暂时跳过
                ex.printStackTrace();
                log.error("获取pixivId={}的关联推荐图片时出错", pixivId);
                errorPixivIdList.add(pixivId);
                continue;
            }
        }

        return result;
    }

    /**
     * 获取pixivId对应的多张(或一张）图片的原始大图地址
     * https://www.pixiv.net/ajax/illust/80391469/pages
     * @param pixivId
     * @return
     */
    public static List<String> getUrlsFromAjaxIllustPagesByPixivId(String pixivId) {
        // 获取ajax/illust/{pixivId}/pages接口的返回结果
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxIllustPage(pixivId).orElse("");
        // 从接口返回结果中解析出多张图片的原始大图地址
        List<String> result = PixivResultParser.getUrlsFromAjaxIllustPageResult(respJSONStr);

        return result;
    }

    /**
     * 获取pixivId对应的多张（或一张）图片的信息
     * @param pixivId
     * @return
     */
    public static List<PixivAjaxIllustPagesUrlInfoBO> getUrlsInfoFromAjaxIllustPagesByPixivId(String pixivId) {
        // 获取ajax/illust/{pixivId}/pages接口的返回结果
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxIllustPage(pixivId).orElse("");
        // 从接口返回结果中解析出多张图片的信息
        List<PixivAjaxIllustPagesUrlInfoBO> result = PixivResultParser.getUrlsInfoFromAjaxIllustPageResult(respJSONStr);
        return result;
    }

    /**
     * 根据pixivId获取其作者的所有作品的pixivId
     * @param pixivId
     * @return
     */
    public static List<String> getPixivIdsFromAuthorAllWorksByPixivId(String pixivId) {
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return null;
        }

        List<String> authorWorksId = resultContentBO.getPictureDetailInfoBO().getUserIllusts();
        return authorWorksId;
    }

    /**
     * 获取根据pixivId所关联推荐的图片以及所有这些图片作者的所有作品的pixivId（一层）
     * @param pixivId
     * @return
     */
    public static Set<String> getPixivIdsFromRecommendPicAuthorsWorksByPixivId(String pixivId) {
        //List<String> result = new ArrayList<>(1024);
        Set<String> result = new HashSet<>(1024);
        // 获取所有推荐的图片
        List<String> recommendPixivIds = getPixivIdsFromAjaxIllustRecommend(pixivId);
        // 获取所有推荐图片的作者的所有作品图片的pixivId
        for (String recommendPixivId : recommendPixivIds) {
            List<String> authorWorksPixivIdList = getPixivIdsFromAuthorAllWorksByPixivId(recommendPixivId);
            result.addAll(authorWorksPixivIdList);
        }
        return result;
    }

    /**
     * 获取作者其所有的插画及漫画作品ID
     * @param pixivUserId 作者的P站ID
     * @return
     */
    public static Set<String> getAuthorIllustAndMangaId(String pixivUserId) {
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxUserProfileAll(pixivUserId).orElse("");
        PixivAuthorProfileBO pixivAuthorProfileBO = PixivResultParser.getAuthorIllustAndMangaInfo(respJSONStr);
        Set<String> result = new HashSet<>(16);
        result.addAll(pixivAuthorProfileBO.getIllustIdList());
        result.addAll(pixivAuthorProfileBO.getMangaIdList());
        return result;
    }

    /**
     * 获取指定日期的综合R18每日排行榜图片ID
     * 从下面这两个接口返回结果中解析获取
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330&p=2&format=json
     * @param date 20200330格式的日期字符串
     * @return
     */
    public static Set<String> getPixivIdsFromRankingDailyR18(String date) {
        Set<String> result = new HashSet<>(128);
        // 获取https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330接口中返回的图片pixivId
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_PREFIX)
                .append(date);
        String respHtml = PixivRequestUtils.getCommonRespFromGZipReqWithCookie(url.toString()).orElse("");

        Set<String> pixivIdSet1 = PixivResultParser.getPixivIdsFromRankingDailyR18(respHtml);
        result.addAll(pixivIdSet1);

        // 获取https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330&p=2&format=json接口返回的图片pixivId
        StringBuilder urlP2 = new StringBuilder()
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_PREFIX)
                .append(date)
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_SUFFIX);
        String respJSONStr = PixivRequestUtils.getCommonRespFromGZipReqWithCookie(urlP2.toString()).orElse("");

        Set<String> pixivIdSet2 = PixivResultParser.getPixivIdsFromRankingDailyR18P2(respJSONStr);
        result.addAll(pixivIdSet2);

        return result;
    }

    public static PixivPictureInfo convertDetailInfoBO2Entity(PixivPictureDetailInfoBO detailInfoBO) {
        PixivPictureInfo pixivPictureInfo = new PixivPictureInfo();

        pixivPictureInfo.setPixivId(detailInfoBO.getId());
        pixivPictureInfo.setAuthorId(detailInfoBO.getUserId());
        pixivPictureInfo.setOriginalUrl(detailInfoBO.getOriginalUrl());
        pixivPictureInfo.setAuthorName(detailInfoBO.getUserName());
        pixivPictureInfo.setTitle(detailInfoBO.getTitle());
        pixivPictureInfo.setMiniUrl(detailInfoBO.getMiniUrl());
        pixivPictureInfo.setThumbUrl(detailInfoBO.getThumbUrl());
        pixivPictureInfo.setSmallUrl(detailInfoBO.getSmallUrl());
        pixivPictureInfo.setRegularUrl(detailInfoBO.getRegularUrl());
        pixivPictureInfo.setPageCount(detailInfoBO.getPageCount());
        pixivPictureInfo.setWidth(detailInfoBO.getWidth());
        pixivPictureInfo.setHeight(detailInfoBO.getHeight());
        pixivPictureInfo.setBookmarkCount(detailInfoBO.getBookmarkCount());
        pixivPictureInfo.setLikeCount(detailInfoBO.getLikeCount());
        pixivPictureInfo.setCommentCount(detailInfoBO.getCommentCount());
        pixivPictureInfo.setResponseCount(detailInfoBO.getResponseCount());
        pixivPictureInfo.setViewCount(detailInfoBO.getViewCount());

        // 为了防止description字段超出数据库长度，这里进行了裁剪
        /*String description = detailInfoBO.getDescription();
        if (description.length() > 1024) {
            description = description.substring(0, 1000);
        }
        pixivPictureInfo.setDescription(description);*/

        return pixivPictureInfo;
    }

    /**
     * 将PixivPictureDetailInfoBO转换为PixivPictureDetailInfo
     * @param bo
     * @return
     */
    public static PixivPictureDetailInfo convert2DetailInfo(PixivPictureDetailInfoBO bo) {
        PixivPictureDetailInfo entity = new PixivPictureDetailInfo();
        entity.setPixivId(bo.getId());
        entity.setOriginalUrl(bo.getOriginalUrl());
        entity.setAuthorId(bo.getUserId());
        entity.setAuthorName(bo.getUserName());
        entity.setPageCount(bo.getPageCount());
        entity.setTitle(bo.getTitle());
        entity.setWidth(bo.getWidth());
        entity.setHeight(bo.getHeight());
        entity.setIllustType(bo.getIllustType());
        entity.setR18Restrict(bo.getXRestrict() == null ? null : Integer.valueOf(bo.getXRestrict()));
        entity.setBookmarkCount(bo.getBookmarkCount());
        entity.setLikeCount(bo.getLikeCount());
        entity.setCommentCount(bo.getCommentCount());
        entity.setViewCount(bo.getViewCount());
        entity.setResponseCount(bo.getResponseCount());
        entity.setCreateDate(bo.getCreateDate());
        entity.setUploadDate(bo.getUploadDate());
        entity.setMiniUrl(bo.getMiniUrl());
        entity.setThumbUrl(bo.getThumbUrl());
        entity.setSmallUrl(bo.getSmallUrl());
        entity.setRegularUrl(bo.getRegularUrl());
        entity.setPixivIdInt(bo.getId() == null ? null : Integer.valueOf(bo.getId()));
        entity.setAuthorIdInt(bo.getUserId() == null ? null : Integer.valueOf(bo.getUserId()));

        String originalUrl = bo.getOriginalUrl();
        if (StringUtils.isNotBlank(originalUrl)) {
            String picSuffix = originalUrl.substring(originalUrl.lastIndexOf(".") + 1);
            entity.setPicSuffix(picSuffix);
        }
        // tags字段的值应该要从PixivArtworksInterfaceResultContentBO类的tagBOList字段拿，不是从这里获取

        return entity;
    }

}
