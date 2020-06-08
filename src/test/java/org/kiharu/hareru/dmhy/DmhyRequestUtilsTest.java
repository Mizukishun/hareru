package org.kiharu.hareru.dmhy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.constant.DmhyConstants;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 动漫花园请求接口单元测试类
 *
 * @Author kiharu
 * @Date 2020-06-08
 */
@Slf4j
public class DmhyRequestUtilsTest {

    // 首页
    public static final String homePage = DmhyConstants.URL_HOME_PAGE;;
    // 分页
    public static final String listPage = DmhyConstants.URL_TOPICS_LIST_PAGE + 3;
    // 详情页
    //public static final String detailPage = "https://share.dmhy.org/topics/view/542149_LoliHouse_Yesterday_wo_Utatte_-_09_WebRip_1080p_HEVC-10bit_AAC.html";
    public static final String detailPage = "https://share.dmhy.org/topics/view/541951_Snow-Raws_Machine-Doll_wa_Kizutsukanai_BD_1920x1080_HEVC-YUV420P10_FLAC.html";
    // 评论
    public static final String commentPage = "https://share.dmhy.org/comment/recent/topic_id/541931"; //?stamp=1591602790";

    @Test
    public void testDownloadDemo() {
        // 首页
        String url = DmhyConstants.URL_HOME_PAGE;
        boolean result = DmhyRequestUtils.downloadDemo(url);
        Assert.isTrue(result, "url接口单元测试不通过");
//
//        // 分页
//        String url2 = DmhyConstants.URL_TOPICS_LIST_PAGE + 3;
//        boolean result2 = DmhyRequestUtils.downloadDemo(url2);
//        Assert.isTrue(result2, "url2接口单元测试不通过");

        // 详情
//        String url3 = "https://share.dmhy.org/topics/view/542149_LoliHouse_Yesterday_wo_Utatte_-_09_WebRip_1080p_HEVC-10bit_AAC.html";
//        boolean result3 = DmhyRequestUtils.downloadDemo(url3);
//        Assert.isTrue(result3, "url2接口单元测试不通过");

        // 评论
//        String url4 = "https://share.dmhy.org/comment/recent/topic_id/541931"; //?stamp=1591602790";
//        boolean result4 = DmhyRequestUtils.downloadDemo(url4);
//        Assert.isTrue(result4, "url接口单元测试不通过");
    }

    @Test
    public void testGetDmhyRequestResult() {
        // 模式选择
        int mode = 2;

        String url;
        switch(mode) {
            case 0:
                url = homePage;
                break;
            case 1:
                url = listPage;
                break;
            case 2:
                url = detailPage;
                break;
            case 3:
                url = commentPage;
                break;
            default:
                url = homePage;
        }

        Optional<String> result = DmhyRequestUtils.getDmhyRequestResult(url);
        Assert.hasLength(result.get(), "获取" + url + "返回结果出错");

        log.debug("请求url={}返回的结果为：\n{}", url, result.get());
    }
}
