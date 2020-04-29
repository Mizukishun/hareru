package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.entity.PixivAllPicture;
import org.kiharu.hareru.entity.PixivPictureDetailInfo;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author kiharu
 * @Date 2020-04-28
 */
@Slf4j
public class PixivAsyncRequestUtilsTest {

    /**
     * 保证异步请求能够在单元测试中继续后续处理的循环
     */
    @AfterEach
    public void whileTrue() {
        int i = 0;
        // 如果不主动关闭单元测试的话，则默认让他运行个1小时
        int time = 5 * 60 * 60;
        while(i < time) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++i;
            log.info("单元测试线程还在运行着");
        }
        log.info("单元测试已停止");
    }

    @Test
    public void testGetRespHtmlFromArtworks() {
        String pixivId = "80385020";
        CopyOnWriteArrayList<PixivPictureDetailInfo> result = new CopyOnWriteArrayList<>();
        Callback callback = PixivCallbackBuilder.getArtworksCallback(result);
        PixivAsyncRequestUtils.getRespHtmlFromArtworks(pixivId, callback);

        /*try {
            Thread.sleep(5 * 60 * 1000);
            log.info("休眠5分钟之后，获取到的信息为{}", JSON.toJSONString(result));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }



}
