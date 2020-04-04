package org.kiharu.hareru.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.bo.PixivPictureUrlInfoBO;
import org.springframework.util.Assert;

import java.io.File;

@Slf4j
public class PixivUtilsTest {

    @Test
    public void testGetPixivPictureUrlInfoBO() {
        String url = "https://i.pximg.net/img-original/img/2020/03/22/00/52/23/80273312_p0.jpg";
        PixivPictureUrlInfoBO bo = PixivUtils.getPixivPictureUrlInfoBO(url);
        log.info("测试解析URL的结果BO:\n{}", JSON.toJSONString(bo));
        Assert.isTrue(bo.getPixivId().equals("80273312"), "解析URL失败");
    }

    @Test
    public void testGetSavedPicFolder() {
        PixivUtils.getSavedPicFolder();
    }

    @Test
    public void testGetSavedPicFolderBySubject() {
        String subject = "p2233";
        File file = PixivUtils.getSavedPicFolderBySubject(subject);
        log.info("创建的文件夹路径为：{}", file.getAbsolutePath());
        Assert.isTrue(file.exists(), "文件已创建成功");
    }

    @Test
    public void testGetLocalSavedPicFile() {
        String url = "https://i.pximg.net/img-original/img/2020/03/16/21/41/05/80165011_p1.jpg";
        String subject = "pixivId80165011";
        String pixivUserId = "1480420";
        PixivUtils.getLocalSavedPicFile(url, subject, pixivUserId);
    }
}
