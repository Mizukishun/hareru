package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivAuthorProfileBO;
import org.kiharu.hareru.constant.PixivConstants;

import java.util.List;
import java.util.Set;

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

    @Test
    public void testGetAuthorIllustAndMangaInfo() {
        String pixivUserId = "7038833";
        String resp = PixivRequestUtils.getResponseFromAjaxUserProfileAll(pixivUserId).orElse("");
        PixivAuthorProfileBO result = PixivResultParser.getAuthorIllustAndMangaInfo(resp);
        log.info("请求获取到的{}作者插画及漫画ID信息为：\n{}", pixivUserId, JSON.toJSONString(result));
    }

    @Test
    public void testGetPixivIdsFromRankingDailyR18() {
        String date = "20200330";
        StringBuilder url = new StringBuilder()
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_PREFIX)
                .append(date);
        String respHtml = PixivRequestUtils.getCommonRespFromGZipReqWithCookie(url.toString()).orElse("");
        Set<String> result = PixivResultParser.getPixivIdsFromRankingDailyR18(respHtml);
        log.info("解析综合R18每日排行榜的图片pixivId结果数量为{}，具体结果值为：\n{}", result.size(), JSON.toJSONString(result));
    }

    @Test
    public void testGetPixivIdsFromRankingDailyR18P2() {
        String date = "20200330";
        StringBuilder urlP2 = new StringBuilder()
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_PREFIX)
                .append(date)
                .append(PixivConstants.PIXIV_RANKING_DAILY_R18_SUFFIX);
        String respJSONStr = PixivRequestUtils.getCommonRespFromGZipReqWithCookie(urlP2.toString()).orElse("");
        Set<String> result = PixivResultParser.getPixivIdsFromRankingDailyR18P2(respJSONStr);
        log.info("解析综合R18每日排行榜P2的图片pixivId结果数量为{}，具体结果值为：\n{}", result.size(), JSON.toJSONString(result));
    }
}
