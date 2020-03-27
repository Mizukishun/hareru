package org.kiharu.hareru.pixiv;

import org.junit.jupiter.api.Test;

public class DownloadPixivPictureTest {

    @Test
    public void testDownloadPixivPicture() {
        String url = "https://i.pximg.net/img-original/img/2015/01/11/18/25/43/48119465_p0.jpg";
        DownloadPixivPicture downloadPixivPicture = new DownloadPixivPicture();
        downloadPixivPicture.downloadPixivPicture(url);
    }


    @Test
    public void testDownloadPictureByPixivId() {
        String pixivId = "70932201";
        DownloadPixivPicture downloadPixivPicture = new DownloadPixivPicture();
        downloadPixivPicture.downloadPictureByPixivId(pixivId);
    }

    @Test
    public void testGetPixivIdsFromAjaxIllustRecommend() {
        String pixivId = "79759981";
        DownloadPixivPicture downloadPixivPicture = new DownloadPixivPicture();
        downloadPixivPicture.downloadRecommendPictureByPixivId(pixivId);
    }
}
