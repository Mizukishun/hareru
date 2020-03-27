package org.kiharu.hareru.pixiv;

import org.junit.jupiter.api.Test;

public class DownloadPixivPictureTest {

    @Test
    public void testGetUrlFromArtworksByPixivId() {
        String pixivId = "72497087";
        DownloadPixivPicture downloadPixivPicture = new DownloadPixivPicture();
        downloadPixivPicture.downloadRecommendPictureByPixivId(pixivId);
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
