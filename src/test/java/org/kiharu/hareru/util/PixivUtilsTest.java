package org.kiharu.hareru.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.bo.PixivPictureUrlInfoBO;
import org.springframework.util.Assert;

@Slf4j
public class PixivUtilsTest {

    @Test
    public void testGetPixivPictureUrlInfoBO() {
        String url = "https://i.pximg.net/img-original/img/2020/03/22/00/52/23/80273312_p0.jpg";
        PixivPictureUrlInfoBO bo = PixivUtils.getPixivPictureUrlInfoBO(url);
        log.info("测试解析URL的结果BO:\n{}", JSON.toJSONString(bo));
        Assert.isTrue(bo.getPixivId().equals("80273312"), "解析URL失败");
    }
}
