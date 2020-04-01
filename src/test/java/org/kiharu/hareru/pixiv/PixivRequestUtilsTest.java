package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PixivRequestUtilsTest {



    @Test
    public void testGetResponseFromAjaxIllustPage() {
        String pixivId = "80385020";
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxIllustPage(pixivId).orElse("出错");
        log.info("获取ajax/illust/{}/pages接口的返回结果为：\n{}", pixivId, respJSONStr);
    }

    @Test
    public void testGetRespHtmlFromArtworksInterface() {
        String pixivId = "80385020";
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        log.info("获取artworks接口返回结果为：\n{}", respHtml);
    }

    @Test
    public void testGetResponseFromAjaxIllustInit() {
        String pixivId = "80385020";
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxIllustInit(pixivId).orElse("Error");
        log.info("获取/ajax/illust/{}}/recommend/init?limit=18接口的返回结果为：\n{}", pixivId, respJSONStr);
    }

    @Test
    public void testGetResponseFromAjaxUserProfileAll() {
        String pixivUserId = "7038833";
        String respJSONStr = PixivRequestUtils.getResponseFromAjaxUserProfileAll(pixivUserId).orElse("Error");
        log.info("获取/ajax/user/{}/profile/all接口的返回结果为：\n{}", pixivUserId, respJSONStr);
    }
}
