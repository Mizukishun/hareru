package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;

@Slf4j
public class PixivInterfaceResultParserTest {

    @Test
    public void testGetArtworksResultContentJSONStr() {
        String pixivId = "77944010";
        String respHtml = PixivPictureInfoUtil.getRespHtmlFromArtworksInterface(pixivId);
        String content = PixivInterfaceResultParser.getArtworksResultContent(respHtml);

        log.info("解析artworks接口的结果content内容如下：======================content\n{}", content);
    }

    @Test
    public void testParseArtworksResult() {
        String pixivId = "77944010";
        String respHtml = PixivPictureInfoUtil.getRespHtmlFromArtworksInterface(pixivId);
        String content = PixivInterfaceResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO result = PixivInterfaceResultParser.parseArtworksResult(content);
        log.info("从artworks接口解析出来的结果具体信息为：\n{}", JSON.toJSONString(result));
    }
}
