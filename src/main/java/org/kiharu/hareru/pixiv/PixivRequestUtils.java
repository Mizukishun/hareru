package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.util.PixivHeadersUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * 专门用于向pixiv的接口发起请求，获取返回结果
 */
@Slf4j
public class PixivRequestUtils {

    /**
     * 获取ajax/illust/{pixivId}/pages接口的返回结果
     * https://www.pixiv.net/ajax/illust/80391469/pages
     * @param pixivId
     * @return
     */
    public static Optional<String> getResponseFromAjaxIllustPage(String pixivId) {
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_AJAX_ILLUST_PAGES_PREFIX)
                .append(pixivId)
                .append(PixivConstants.PIXIV_AJAX_ILLUST_PAGES_SUFFIX);

        Headers headers = PixivHeadersUtils.getHeadersWithUserCookie();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url.toString())
                .build();

        Optional<String> result = Optional.empty();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求获取pixivId={}的多张图片的原始大图地址失败，url={}", pixivId, url.toString());
                // TODO--这里放return语句合适吗？？
                return result;
            }
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respJSONStr = bufferedReader.lines().collect(Collectors.joining());
            result = Optional.of(respJSONStr);
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("请求获取ajax/illust/../pages接口出错，具体的请求接口地址为：")
                    .append(url.toString());
            log.error(errorMsg.toString(), ex);
        }
        return result;
    }

    /**
     * 获取Pixiv的artworks接口的返回内容
     * 接口地址样例：https://www.pixiv.net/artworks/76211609
     * @param pixivId
     * @return
     */
    public static Optional<String> getRespHtmlFromArtworksInterface(String pixivId) {
        String url = PixivConstants.PIXIV_ARTWORKS_PATH + pixivId;
        Headers headers = PixivHeadersUtils.getHeadersWithUserCookie();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        Optional<String> result = Optional.empty();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求pixiv的artworks接口返回结果时出错\nurl={}\nerrorInfo={}", url, response.message());
                return result;
            }

            InputStream inputStream = response.body().byteStream();
            // 注意这里的返回结果是压缩了的，所以这里通过GZIPInputStream进行解压转换
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respHtml = bufferedReader.lines().collect(Collectors.joining());
            result = Optional.of(respHtml);
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("获取artworks接口返回结果出错，请求的接口地址为：")
                    .append(url);
            log.error(errorMsg.toString(), ex);
        }

        return result;
    }


    /**
     * 根据pixivId获取所关联推荐的所有图片的接口返回结果
     * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
     * @param pixivId
     * @return
     */
    public static Optional<String> getResponseFromAjaxIllustInit(String pixivId) {
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_ILLUST_RECOMMEND_PREFIX)
                .append(pixivId)
                .append(PixivConstants.PIXIV_ILLUST_RECOMMEND_SUFFIX);

        Headers headers = PixivHeadersUtils.getHeadersWithUserCookie();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url.toString())
                .build();

        Optional<String> result = Optional.empty();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求推荐图片pixivIds失败，url={}", url.toString());
                return result;
            }
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respJSONStr = bufferedReader.lines().collect(Collectors.joining());

            result = Optional.of(respJSONStr);
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("根据pixivId获取所关联推荐的所有图片的接口返回结果，其中pixivId=")
                    .append(pixivId)
                    .append("请求的具体地址为：")
                    .append(url.toString());
            log.error(errorMsg.toString(), ex);
        }
        return result;
    }

    /**
     * 根据作者pixivUserId获取该作者所有作品基本信息的接口返回结果
     * https://www.pixiv.net/ajax/user/1277076/profile/all
     * @param pixivUserId
     * @return
     */
    public static Optional<String> getResponseFromAjaxUserProfileAll(String pixivUserId) {
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_AJAX_USER_PROFILE_ALL_PREFIX)
                .append(pixivUserId)
                .append(PixivConstants.PIXIV_AJAX_USER_PROFILE_ALL_SUFFIX);
        Headers headers = PixivHeadersUtils.getHeadersWithUserCookie();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url.toString())
                .build();
        Optional<String> result = Optional.empty();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求作者所有作品基本信息接口失败，url={}", url.toString());
                return result;
            }
            GZIPInputStream gZipInputStream = new GZIPInputStream(response.body().byteStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gZipInputStream, "UTF-8"));
            String respJSONStr = bufferedReader.lines().collect(Collectors.joining());
            result = Optional.of(respJSONStr);
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("请求根据作者pixivUserId获取该作者所有作品基本信息的接口返回结果出错,url=")
                    .append(url.toString());
            log.error(errorMsg.toString(), ex);
        }
        return result;
    }

    /**
     * 获取综合R18每日排行榜接口的返回结果
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200331
     * @param date 20200331格式的日期字符串，指定具体哪一天的综合R18排行榜
     * @return
     */
    public static Optional<String> getResponseFromRankingDailyR18(String date) {
        Optional<String> respHtml = Optional.empty();
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_PREFIX)
                .append(date);
        respHtml = getCommonRespFromGZipReqWithCookie(url.toString());

        return respHtml;
    }

    /**
     * 通用的用于获取返回结果的方法，带有用户Cookies信息的请求，同时返回结果是压缩类型的
     * @param url
     * @return 字符串
     */
    public static Optional<String> getCommonRespFromGZipReqWithCookie(String url) {
        Headers headers = PixivHeadersUtils.getHeadersWithUserCookieAutoDecompress();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();
        Optional<String> result = Optional.empty();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求获取url={}的返回结果失败", url);
                return result;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream(), "UTF-8"));
            String resp = bufferedReader.lines().collect(Collectors.joining());
            result = Optional.of(resp);
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("请求获取url=")
                    .append(url)
                    .append("出错");
            log.error(errorMsg.toString(), ex);
        }
        return result;
    }
}
