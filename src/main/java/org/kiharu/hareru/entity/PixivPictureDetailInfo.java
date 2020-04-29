package org.kiharu.hareru.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * P站图片详情信息
 * @Author kiharu
 * @Date 2020-04-29
 */
@Data
public class PixivPictureDetailInfo implements Serializable {

    private static final long serialVersionUID = -5499090822833594895L;

    private Long id;
    /** P站图片ID **/
    private String pixivId;
    /** 在P站是否存在该图片，0-不存在，1-存在 **/
    private Integer existed;
    /** 是否已下载到本地，0-未下载，1-已下载 **/
    private Integer downloaded;
    /** 原始图片地址 **/
    private String originalUrl;
    /** 图片作者ID **/
    private String authorId;
    /** 图片作者名称 **/
    private String authorName;
    /** 该pixivId对应的图片数量 **/
    private Integer pageCount;
    /** 图片标题 **/
    private String title;
    /** 图片宽度 **/
    private Integer width;
    /** 图片高度 **/
    private Integer height;
    /** 图片类型，0-插画，1-漫画，2-动图 **/
    private Integer illustType;
    /** 图片后缀名称 **/
    private String picSuffix;
    /** 是否18限图片，0-否，1-是 **/
    private Integer r18Restrict;
    /** 收藏数 **/
    private Integer bookmarkCount;
    /** 赞数 **/
    private Integer likeCount;
    /** 评论数 **/
    private Integer commentCount;
    /** 观看数 **/
    private Integer viewCount;
    /** 回复数 **/
    private Integer responseCount;
    /** 图片在P站的创建时间，注意中国和日本的时区差 **/
    private Date createDate;
    /** 图片在P站的上传时间，注意中国和日本的时区差 **/
    private Date uploadDate;
    /** 图片的标签，以分号:分割 **/
    private String tags;
    /** 迷你图地址 **/
    private String miniUrl;
    /** 缩略图地址 **/
    private String thumbUrl;
    /** 小图地址 **/
    private String smallUrl;
    /** 正常图地址 **/
    private String regularUrl;
    /** 图片大小，以B为单位 **/
    private Integer sizeB;
    /** 图片大小，以KB为单位 **/
    private Integer sizeKB;
    /** 图片大小，以MB为单位 **/
    private Integer sizeMB;
    /** INT类型的图片pixiv_id，便于进行排序等处理 **/
    private Integer pixivIdInt;
    /** INT类型的作者author_id，便于进行排序等处理 **/
    private Integer authorIdInt;
    /** 记录新增时间 **/
    private Date addTime;
    /** 记录更新时间 **/
    private Date updateTime;
    /** 请求状态，0-初始，1-成功，2-待重试，3-失败，见RequestStatusEnum **/
    private Integer requestStatus;
}
