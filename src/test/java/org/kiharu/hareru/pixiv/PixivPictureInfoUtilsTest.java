package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Slf4j
public class PixivPictureInfoUtilsTest {

    @Test
    public void testGetUrlFromArtworksByPixivId() {
        String pixivId = "73059317";
        String url = PixivPictureInfoUtils.getUrlFromArtworksByPixivId(pixivId);
        log.info("获取到的pixivId={}图片地址为：\n{}", pixivId, url);
    }

    @Test
    public void testGetUrlsFromAjaxIllustPagesByPixivId() {
        String pixivId = "80265023";
        List<String> result = PixivPictureInfoUtils.getUrlsFromAjaxIllustPagesByPixivId(pixivId);
        log.info("获取pixivId={}对应的多张图片的原始大图地址的结果为：\n{}", pixivId, JSONObject.toJSONString(result));
    }

    @Test
    public void testGetUrlsFromArtworksByPixivId() {
        String pixivId = "80265023";
        List<String> urls = PixivPictureInfoUtils.getUrlsFromArtworksByPixivId(pixivId);
        log.info("pixivId对应的所有图片的原始大图地址有(一张）：\n{}", JSON.toJSONString(urls));
        Assert.isTrue(urls.size() == 1, pixivId + "对应的是一张图片");

        String multiPixivId = "80420349";
        List<String> multiUrls = PixivPictureInfoUtils.getUrlsFromArtworksByPixivId(multiPixivId);
        log.info("pixivId对应的所有图片的原始大图地址有(多张）：\n{}", JSON.toJSONString(multiUrls));
        Assert.isTrue(multiUrls.size() > 1, multiPixivId + "对应的是多张图片");
    }

    @Test
    public void testGetAuthorIllustAndMangaId() {
        String pixivUserId = "7038833";
        Set<String> result = PixivPictureInfoUtils.getAuthorIllustAndMangaId(pixivUserId);
        log.info("获取到{}作者的所有插画和漫画的ID：\n{}", pixivUserId, JSON.toJSONString(result));
    }
}
