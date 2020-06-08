package org.kiharu.hareru.dmhy;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.constant.DmhyConstants;
import org.kiharu.hareru.entity.DmhyResource;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * 动漫花园接口返回结果解析类的单元测试类
 *
 * @Author kiharu
 * @Date 2020-06-08
 */
@Slf4j
public class DmhyResultParserTest {

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
    public void testParseTopicPageResult() {
        // 模式选择
        int mode = 1;

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

        Optional<String> htmlResp = DmhyRequestUtils.getDmhyRequestResult(url);
        List<DmhyResource> result = DmhyResultParser.parseTopicPageResult(htmlResp.get());
        Assert.isTrue(!result.isEmpty(), "解析返回结果内容出错");
        log.info("解析url={}的返回结果，其中result.size={},result=\n{}", url, result.size(), JSON.toJSONString(result));
    }

    @Test
    public void testParseDetailInfo() {
        String url = detailPage;
        Optional<String> htmlResp = DmhyRequestUtils.getDmhyRequestResult(url);
        DmhyResource result = DmhyResultParser.parseDetailInfo(htmlResp.get());

        Assert.isTrue(StringUtils.isNotEmpty(result.getMagnet()), "解析详情页面返回结果失败");
        Assert.isTrue(result.getFileList().size() == result.getFileSizeList().size(), "返回的文件数量与大小数量不相等");
        log.info("解析url={}详情页面所得结果为：\n{}", url, JSON.toJSONString(result));
    }
}
