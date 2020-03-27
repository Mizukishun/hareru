package org.kiharu.hareru.pixiv;

import org.junit.jupiter.api.Test;

public class PixivPictureInfoUtilTest {

    @Test
    public void testGetUrlFromArtworksByPixivId() {
        String pixivId = "72497087";
        PixivPictureInfoUtil.getUrlFromArtworksByPixivId(pixivId);
    }
}
