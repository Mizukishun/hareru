package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片的tag信息
 * 具体样例可参见"sample/artworks接口返回结果的preload-data的content内容.json"
 */
@Data
public class PixivPictureTagBO implements Serializable {

    private String tag;
    private Boolean locked;
    private Boolean deletable;
    /** 创建这个tag的用户ID **/
    private String userId;
    /** 创建这个tag的用户名 **/
    private String userName;
    private String translation;

}
