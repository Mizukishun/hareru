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
                    .append("根据出错，其中pixivId=")
                    .append(pixivId)
                    .append("请求的具体地址为：")
                    .append(url.toString());
            log.error(errorMsg.toString(), ex);
        }
        return result;
    }
}
