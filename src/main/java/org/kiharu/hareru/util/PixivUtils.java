package org.kiharu.hareru.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Headers;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.bo.PixivAjaxIllustRecommendRespBO;
import org.kiharu.hareru.bo.PixivAjaxIllustRecommendRespBodyBO;
import org.kiharu.hareru.constant.PixivConstants;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 用于处理Pixiv图片的相关工具类
 */
public class PixivUtils {

    /**
     * 获取下载图片时所需的请求头信息Headers
     * @return
     */
    public static Headers getHeaders() {
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
     * 获取下载artworks/pixivId是所需的请求头信息Heaaders
     * https://www.pixiv.net/artworks/76211609
     * @return
     */
    public static Headers getArtworksHeaders() {
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

    /**
     * 保存图片的完整路径名，指定到具体文件
     * 获取到的文件路径名格式为：
     * D:/pixivDemo/{date}/{pictureName}
     * TODO--总感觉目前这样不大灵活，看情况之后修改吧！
     * @param url
     * @return
     */
    public static String getCompletedFilePath(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (!url.contains("/")) {
            return null;
        }

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String todayStr = format.format(today);


        int endIndex = url.length();
        if (url.contains("_p0")) {
            // 如果图片路径最后包含_p0，则去掉这_p0，只保留pixiv的图片ID
            endIndex = url.lastIndexOf("_p0");
        }
        String pixivId = url.substring(url.lastIndexOf("/") + 1, endIndex);
        // 图片文件后缀名，也即图片类型
        String picSuffix = url.substring(url.lastIndexOf("."), url.length());

        StringBuilder result = new StringBuilder()
                .append(PixivConstants.PICTURE_SAVE_DIR)
                .append(todayStr)
                .append("/")
                .append(pixivId)
                .append(picSuffix);

        return result.toString();
    }

    /**
     * 从Pixiv的关联图片推荐接口返回的结果字符串中解析出其中所有推荐的图片pixivId
     * https://www.pixiv.net/ajax/illust/79759981/recommend/init?limit=18
     * @param respJSONStr
     * @return
     */
    public static List<String> getPixivIdsFromRespStr(String respJSONStr) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isEmpty(respJSONStr)) {
            return result;
        }


        JSONObject resp = JSON.parseObject(respJSONStr);
        JSONObject body = resp.getJSONObject("body");
        JSONArray illusts = body.getJSONArray("illusts");
        JSONArray nextIds = body.getJSONArray("nextIds");

        List<String> nextIdList = new ArrayList<>();
        for (int i = 0; i < nextIds.size(); ++i) {
            String nextId = nextIds.getString(i);
            nextIdList.add(nextId);
        }

        List<String> illustsIdList = new ArrayList<>();
        for (int j = 0; j < illusts.size(); ++j) {
            JSONObject illustObject = illusts.getJSONObject(j);
            String id = illustObject.getString("id");
            illustsIdList.add(id);
        }

        result.addAll(illustsIdList);
        result.addAll(nextIdList);

        StringBuilder resultInfo = new StringBuilder()
                .append("关联图片推荐接口返回的图片pixivIds总共有:").append(nextIdList.size()).append("个\n")
                .append("其中nextIds有：").append(nextIds.size()).append("个\n")
                .append("其中illustIds有：").append(illustsIdList.size()).append("个\n")
                .append("所有的pixivIds如下：\n")
                .append(JSONObject.toJSONString(result));



        /*
        PixivAjaxIllustRecommendRespBO respBO = (PixivAjaxIllustRecommendRespBO)JSON.parse(respJSONStr);

        if (respBO == null) {
            System.out.println("解析关联图片推荐接口返回结果出错：respJSONStr=" + respJSONStr);
        }
        PixivAjaxIllustRecommendRespBodyBO bodyBO = respBO.getBody();

        List<String> nextIds = bodyBO.getNextIds();
        result.addAll(nextIds);

        List<String> illustIds = bodyBO.getIllusts().stream().map(item -> item.getId()).collect(Collectors.toList());
        result.addAll(illustIds);*/

       /* StringBuilder resultInfo = new StringBuilder()
                .append("关联图片推荐接口返回的图片pixivIds总共有:").append(result.size()).append("个\n")
                .append("其中nextIds有：").append(nextIds.size()).append("个\n")
                .append("其中illustIds有：").append(illustIds.size()).append("个\n")
                .append("所有的pixivIds如下：\n")
                .append(JSONObject.toJSONString(result));*/


        System.out.println(resultInfo);

        return result;
    }
}
