package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class PixivPictureDownloaderTest {

    @Test
    public void testDownloadPixivPicture() {
        String url = "https://i.pximg.net/img-original/img/2019/02/08/00/36/05/73059317_p0.png";
        PixivPictureDownloader pixivPictureDownloader = new PixivPictureDownloader();
        long begin = System.currentTimeMillis();
        pixivPictureDownloader.downloadPixivPicture(url);
        long end = System.currentTimeMillis();
        log.info("下载一张图片所用的时间为：{}秒", (end - begin) / 1000);
    }


    @Test
    public void testDownloadPictureByPixivId() {
        String pixivId = "80420349";
        PixivPictureDownloader pixivPictureDownloader = new PixivPictureDownloader();
        pixivPictureDownloader.downloadPictureByPixivId(pixivId);
    }

    @Test
    public void testGetPixivIdsFromAjaxIllustRecommend() {
        String pixivId = "79759981";
        PixivPictureDownloader pixivPictureDownloader = new PixivPictureDownloader();
        pixivPictureDownloader.downloadRecommendPictureByPixivId(pixivId);
    }
}
