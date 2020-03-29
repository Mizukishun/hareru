package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 从artworks接口结果的content内容中解析出来的具体图片信息
 * content的内容可参见"sample/artworks接口返回结果的preload-data的content内容.json";
 */
@Data
public class PixivArtworksInterfaceResultContentBO implements Serializable {

    /** 图片详细信息 **/
    private PixivPictureDetailInfoBO pictureDetailInfoBO;

    /** 作者基本信息 **/
    private PixivPictureAuthorInfoBO authorInfoBO;

    /** tag信息 **/
    private List<PixivPictureTagBO> tagBOList;
}
