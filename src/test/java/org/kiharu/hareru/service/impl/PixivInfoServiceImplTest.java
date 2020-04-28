package org.kiharu.hareru.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.service.PixivInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * @Author kiharu
 * @Date 2020-04-28
 */
@Slf4j
@SpringBootTest
public class PixivInfoServiceImplTest {
    @Autowired
    private PixivInfoService pixivInfoService;

    /**
     * 对于异步下载的单元测试，需要下面的无限循环保证能继续进行下去
     */
    @AfterEach
    public void whileTrue() {
        // 用下面这个死循环来让这个单元测试能够进行！
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("醒了");
        }
    }

    @Test
    public void testDownloadPixivPictureUrl() {
        Integer startPixivId = 1;
        Integer endPixivId = 20;
        Integer result = pixivInfoService.downloadPixivPictureUrl(startPixivId, endPixivId);
        log.info("下载pixivId为[{}, {}]返回的图片信息，其返回的结果数量为{}", startPixivId, endPixivId, result);
    }
}
