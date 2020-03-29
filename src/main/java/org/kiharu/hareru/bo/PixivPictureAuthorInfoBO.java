package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Pixiv图片的作者信息，基于artworks接口返回数据来的
 * 可参见"sample/artworks接口返回结果的preload-data的content内容.json"
 */
@Data
public class PixivPictureAuthorInfoBO implements Serializable {

    private String userId;
    private String userName;
    private String image;
    private String imageBig;
    private Boolean premium;
    private Boolean isFollowed;
    private Boolean isMypixiv;
    private Boolean isBlocking;
    private String background;
    private Integer partial;

    /** 该作者的所有图片ID **/
    private List<String> userIllusts;
}
