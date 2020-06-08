package org.kiharu.hareru.dmhy;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.util.PixivHeadersUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 请求获取动漫花园相关信息
 *
 * @Author kiharu
 * @Date 2020-06-08
 */
@Slf4j
public class DmhyRequestUtils {

    /**
     * 动漫花园接口测试demo
     * @return
     */
    public static boolean downloadDemo(String url) {
        Headers headers = PixivHeadersUtils.getDmhyCommonHeaders();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求获取{}返回结果是出错，出错信息为：{}", url, response.message());
                return false;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream(), "UTF-8"));
            String resp = bufferedReader.lines().collect(Collectors.joining());

            log.info("动漫花园获取{}的返回结果为：\n{}", url, resp);
        } catch (IOException ex) {
            log.error("请求获取" + url + "出错", ex);
        }
        return true;
    }

    /**
     * 获取动漫花园返回的接口结果信息，为HTML格式的字符串
     * @param url
     * @return
     */
    public static Optional<String> getDmhyRequestResult(String url) {
        Optional<String> result = Optional.empty();

        Headers headers = PixivHeadersUtils.getDmhyCommonHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求获取{}的返回结果出错，出错信息为：{}", url, response.message());
                return result;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream(), "UTF-8"));
            String htmlResp = bufferedReader.lines().collect(Collectors.joining());
            if (StringUtils.isNotBlank(htmlResp)) {
                result = Optional.of(htmlResp);
            }
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("请求获取")
                    .append(url)
                    .append("的执行结果出错，出错信息为");
            log.error(errorMsg.toString(), ex);
        }


        return result;
    }
}
