package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.util.PixivHeadersUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步请求获取Pixiv的相关信息
 * @Author kiharu
 * @Date 2020-04-28
 */
@Slf4j
public class PixivAsyncRequestUtils {


    /**
     * 获取从artworks接口返回的pixivId对应内容
     * 接口地址样例：https://www.pixiv.net/artworks/76211609
     * @param pixivId
     * @return
     */
    public static void getRespHtmlFromArtworks(String pixivId, Callback callback) {
        String url = PixivConstants.PIXIV_ARTWORKS_PATH + pixivId;
        Headers headers = PixivHeadersUtils.getHeadersWithUserCookieAutoDecompress();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(PixivConstants.REQUEST_SECONDS_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(PixivConstants.REQUEST_SECONDS_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(PixivConstants.REQUEST_SECONDS_TIME_OUT, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        // 异步网络请求
        client.newCall(request).enqueue(callback);
    }

    /**
     * 获取pixivId对应的多张图片的接口返回内容
     * 也即或取ajax/illust/{pixivId}/pages接口的返回结果
     * https://www.pixiv.net/ajax/illust/80391469/pages
     * @param pixivId
     * @param callback
     */
    public static void getResponseFromAjaxIllustPage(String pixivId, Callback callback) {
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_AJAX_ILLUST_PAGES_PREFIX)
                .append(pixivId)
                .append(PixivConstants.PIXIV_AJAX_ILLUST_PAGES_SUFFIX);
        Headers headers = PixivHeadersUtils.getHeadersWithUserCookieAutoDecompress();
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(PixivConstants.REQUEST_SECONDS_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(PixivConstants.REQUEST_SECONDS_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(PixivConstants.REQUEST_SECONDS_TIME_OUT, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url.toString())
                .build();
        // 异步网络请求
        client.newCall(request).enqueue(callback);

    }
}
