package org.kiharu.hareru.pixiv;

import org.junit.jupiter.api.Test;

public class PixivPictureInfoUtilTest {

    @Test
    public void testGetUrlFromArtworksByPixivId() {
        String pixivId = "72497087";
        PixivPictureInfoUtil.getUrlFromArtworksByPixivId(pixivId);
    }

    @Test
    public void testgetRespHtmlFromArtworksInterface() {
        String pixivId = "80385020";
        String respHtml = PixivPictureInfoUtil.getRespHtmlFromArtworksInterface(pixivId);
        System.out.println(respHtml);
    }
}
