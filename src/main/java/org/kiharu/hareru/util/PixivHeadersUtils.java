package org.kiharu.hareru.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置Pixiv请求接口所需的各种请求头Headers
 */
@Slf4j
public class PixivHeadersUtils {

    /**
     * 获取下载图片时所需的请求头信息Headers
     * 没有带有用户Cookie等信息
     * @return
     */
    public static Headers getSimpleHeaders() {
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Host", "i.pximg.net");
        headersMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        headersMap.put("Accept", "image/webp,*/*");
        headersMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        headersMap.put("Accept-Encoding", "gzip, deflate, br");
        headersMap.put("DNT", "1");
        headersMap.put("Connection", "keep-alive");
        headersMap.put("TE", "Trailers");
        headersMap.put("Referer", "https://www.pixiv.net");
        Headers headers = Headers.of(headersMap);

        return headers;
    }

    /**
     * 获取带有用户Cookies信息的请求头
     * 主要用在需要带有 用户信息的接口请求中，如：
     * 获取下载artworks/pixivId是所需的请求头信息Heaaders
     * https://www.pixiv.net/artworks/76211609
     * @return
     */
    public static Headers getHeadersWithUserCookie() {
        Map<String, String> map = new HashMap<>();
        map.put("Host", "www.pixiv.net");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        map.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("DNT", "1");
        map.put("Upgrade-Insecure-Requests", "1");
        map.put("Connection", "keep-alive");
        map.put("Cookie", "PHPSESSID=22834429_W44oSwMbDasKma4DsUWd1JLCcpcA7NPk");

        Headers headers = Headers.of(map);
        return headers;
    }
}
