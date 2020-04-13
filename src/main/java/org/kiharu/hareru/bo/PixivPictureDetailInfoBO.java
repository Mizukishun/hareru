package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 从artworks接口返回的关于图片的具体详细信息
 * 具体样例可参见"sample/artworks接口返回结果的preload-data的content内容.json"
 */
@Data
public class PixivPictureDetailInfoBO implements Serializable {

    private String illustId;
    private String illustTitle;
    private String illustComment;

    private String id;
    private String title;
    private String description;
    /**
     * 图片类型
     * 0-插画，1-漫画，2-动图
     */
    private Integer illustType;
    private Date createDate;
    private Date uploadDate;
    private String restrict;
    private String xRestrict;
    private String sl;

    private String miniUrl;
    private String thumbUrl;
    private String smallUrl;
    private String regularUrl;
    private String originalUrl;

    // 图片作者信息
    private String userId;
    private String userName;
    private String userAccount;
    /** 图片作者的所有图片pixivId **/
    private List<String> userIllusts;

    /** pixivId可能对应多张图片的数量 **/
    private Integer pageCount;

    /** 图片属性统计信息 **/
    private Integer width;
    private Integer height;
    private Integer bookmarkCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer responseCount;
    private Integer viewCount;
}
