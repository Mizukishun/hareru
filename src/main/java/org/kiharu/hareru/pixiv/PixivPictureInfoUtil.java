package org.kiharu.hareru.pixiv;

import jdk.internal.util.xml.impl.Input;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.util.PixivUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * 这个类用于下载Pixiv图片相关的信息，也即下载的辅助类
 */
@Slf4j
@Service
public class PixivPictureInfoUtil {

    /**
     * 获取Pixiv的artworks接口的返回内容
     * 接口地址样例：https://www.pixiv.net/artworks/76211609
     * @param pixivId
     * @return
     */
    public static String getRespHtmlFromArtworksInterface(String pixivId) {
        String url = PixivConstants.PIXIV_ARTWORKS_PATH + pixivId;
        Headers headers = PixivUtils.getArtworksHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        String respHtml = null;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求pixiv的artworks接口返回结果时出错\nurl={}\nerrorInfo={}", url, response.message());
                return null;
            }

            InputStream inputStream = response.body().byteStream();
            // 注意这里的返回结果是压缩了的，所以这里通过GZIPInputStream进行解压转换
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            respHtml = bufferedReader.lines().collect(Collectors.joining());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return respHtml;
    }

    /**
     * 根据pixivId从artworks中获取的内容中提取出图片的原始大图地址
     * https://www.pixiv.net/artworks/76211609
     * @param pixivId
     * @return https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg
     */
    public static String getUrlFromArtworksByPixivId(String pixivId) {

        String url = PixivConstants.PIXIV_ARTWORKS_PATH + pixivId;

        Headers headers = PixivUtils.getArtworksHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        String result = null;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                //System.out.println("请求获取图片原始大图地址时出错，url=" + url);
                log.error("请求获取图片原始大图地址时出错，url={}", url);
                return null;
            }
            InputStream inputStream = response.body().byteStream();
            // 注意这里是压缩了的，所以这里通过GZIPInputStream进行解压转换
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respHtml = bufferedReader.lines().collect(Collectors.joining());

            // 匹配https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg这样的原图
            String regex = PixivConstants.PIXIV_ARTWORKS_IMG_ORIGINAL_REGEX;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(respHtml);


            if (matcher.find()) {
                result = matcher.group();
                //result = pixivPicOriginalAddress.replace("\"", "");
            }

            StringBuilder sb = new StringBuilder()
                    .append("请求获取图片原始大图信息的返回结果为：\n")
                    .append(result);
            log.info(sb.toString());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

        StringBuilder urlSB = new StringBuilder()
                .append(PixivConstants.PIXIV_ILLUST_RECOMMEND_PREFIX)
                .append(pixivId)
                .append(PixivConstants.PIXIV_ILLUST_RECOMMEND_SUFFIX);

        Headers headers = PixivUtils.getArtworksHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(urlSB.toString())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                //System.out.println("请求推荐图片pixivIds失败,url=" + urlSB.toString());
                log.error("请求推荐图片pixivIds失败，url={}", urlSB.toString());
            }
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respJSONStr = bufferedReader.lines().collect(Collectors.joining());

            //System.out.println("请求推荐图片pixivIds的返回结果为:\n" + respJSONStr);

            // 从返回的结果中解析出其推荐的所有图片pixivIds
            pixivIdList = PixivUtils.getPixivIdsFromRespStr(respJSONStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // 把pixivId本身也下载下载
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
     * 获取该pixivId对应的可能的多张图片的原始大图地址
     * @param pixivId
     * @return
     */
    public static List<String> getUrlsFromArtworksByPixivId(String pixivId) {
        if (StringUtils.isEmpty(pixivId)) {
            return null;
        }
        String respHtml = getRespHtmlFromArtworksInterface(pixivId);
        if (StringUtils.isEmpty(respHtml)) {
            return null;
        }
        String content = PixivInterfaceResultParser.getArtworksResultContent(respHtml);
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivInterfaceResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return null;
        }

        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();
        Integer pageCount = detailInfoBO.getPageCount();
        String originalUrl = detailInfoBO.getOriginalUrl();


        List<String> urls = new ArrayList<>(pageCount + 8);
        // 如果该pixivId只对应一张图片，则只把当前请求到原始大图返回
        if (pageCount == 1 && StringUtils.isNotEmpty(originalUrl)) {
            urls.add(originalUrl);
            return urls;
        }
        // 如果该pixivId对应多张图片，则需再次请求https://www.pixiv.net/ajax/illust/80391469/pages接口进行获取所有图片原始大图地址
        urls = getUrlsFromAjaxIllustPagesByPixivId(pixivId);
        return urls;
    }

    /**
     * 获取pixivId对应的多张图片的原始大图地址
     * https://www.pixiv.net/ajax/illust/80391469/pages
     * @param pixivId
     * @return
     */
    public static List<String> getUrlsFromAjaxIllustPagesByPixivId(String pixivId) {
        StringBuilder urlSB = new StringBuilder()
                .append(PixivConstants.PIXIV_AJAX_ILLUST_PAGES_PREFIX)
                .append(pixivId)
                .append(PixivConstants.PIXIV_AJAX_ILLUST_PAGES_SUFFIX);

        Headers headers = PixivUtils.getHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(urlSB.toString())
                .build();

        List<String> result = null;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求获取pixivId={}的多张图片的原始大图地址失败，url={}", pixivId, urlSB.toString());
                // TODO--这里需要怎么处理才合适？
                response.close();
                return null;
            }
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respJSONStr = bufferedReader.lines().collect(Collectors.joining());
            result = PixivInterfaceResultParser.getUrlsFromAjaxIllustPageInterfaceResult(respJSONStr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 根据pixivId获取其作者的所有作品的pixivId
     * @param pixivId
     * @return
     */
    public static List<String> getPixivIdsFromAuthorAllWorksByPixivId(String pixivId) {
        String respHtml = PixivPictureInfoUtil.getRespHtmlFromArtworksInterface(pixivId);
        String content = PixivInterfaceResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivInterfaceResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return null;
        }

        List<String> authorWorksId = resultContentBO.getPictureDetailInfoBO().getUserIllusts();
        return authorWorksId;
    }

    /**
     * 根据pixivId所关联推荐的图片以及所有这些图片作者的所有作品的pixivId（一层）
     * @param pixivId
     * @return
     */
    public static List<String> getPixivIdsFromRecommendPicAuthorsWorksByPixivId(String pixivId) {
        List<String> result = new ArrayList<>(1024);
        // 获取所有推荐的图片
        List<String> recommendPixivIds = getPixivIdsFromAjaxIllustRecommend(pixivId);
        // 获取所有推荐图片的作者的所有作品图片的pixivId
        for (String recommendPixivId : recommendPixivIds) {
            List<String> authorWorksPixivIdList = getPixivIdsFromAuthorAllWorksByPixivId(pixivId);
            result.addAll(authorWorksPixivIdList);
        }
        return result;
    }
}
