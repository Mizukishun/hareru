package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;

import java.util.List;

@Slf4j
public class PixivResultParserTest {

    @Test
    public void testGetArtworksResultContentJSONStr() {
        String pixivId = "77944010";
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);

        log.info("解析artworks接口的结果content内容如下：======================content\n{}", content);
    }

    @Test
    public void testParseArtworksResult() {
        String pixivId = "77944010";
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO result = PixivResultParser.parseArtworksResult(content);
        log.info("从artworks接口解析出来的结果具体信息为：\n{}", JSON.toJSONString(result));
    }

    @Test
    public void testGetUrlsFromAjaxIllustPageResult() {
        String pixivId = "80420349";
        String resp = PixivRequestUtils.getResponseFromAjaxIllustPage(pixivId).orElse("");
        List<String> result = PixivResultParser.getUrlsFromAjaxIllustPageResult(resp);
        log.info("获取pixivId={}对应的所有图片的原始大图地址：\n{}", pixivId, JSONObject.toJSONString(result));
    }
}
