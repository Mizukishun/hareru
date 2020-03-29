package org.kiharu.hareru.pixiv;

import org.junit.jupiter.api.Test;

public class PixivPictureDownloaderTest {

    @Test
    public void testDownloadPixivPicture() {
        String url = "https://i.pximg.net/img-original/img/2015/01/11/18/25/43/48119465_p0.jpg";
        PixivPictureDownloader pixivPictureDownloader = new PixivPictureDownloader();
        pixivPictureDownloader.downloadPixivPicture(url);
    }


    @Test
    public void testDownloadPictureByPixivId() {
        String pixivId = "70932201";
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
