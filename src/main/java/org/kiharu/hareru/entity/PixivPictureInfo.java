package org.kiharu.hareru.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * P站图片详细信息
 * @author kiharu
 * @date 2020-04-09
 */
@Data
public class PixivPictureInfo implements Serializable {
    private static final long serialVersionUID = -4282290374156469323L;

    /** 主键 **/
    private Integer id;
    /** P站图片ID **/
    private String pixivId;
    /** 图片作者ID **/
    private String authorId;
    /** 图片作者名称 **/
    private String authorName;
    /** 原始图片地址 **/
    private String originalUrl;
    /** 图片标题 **/
    private String title;
    /** 图片描述 **/
    private String description;
    /** 图片类型后缀 **/
    private String picSuffix;
    /** 是否R18 **/
    private Integer r18Restrict;
    /** 宽度 **/
    private Integer width;
    /** 高度 **/
    private Integer height;
    /** 同一pixivId对应图片的数量 **/
    private Integer pageCount;
    /** 收藏数 **/
    private Integer bookmarkCount;
    /** 赞数 **/
    private Integer likeCount;
    /** 观看数 **/
    private Integer viewCount;
    /** 评论数 **/
    private Integer commentCount;
    /** 回复数 **/
    private Integer responseCount;
    /** 迷你图地址 **/
    private String miniUrl;
    /** 缩略图地址 **/
    private String thumbUrl;
    /** 小图地址 **/
    private String smallUrl;
    /** 正常图地址 **/
    private String regularUrl;
    /** 图片上传到P站的日期,yyyyMMdd格式 **/
    private String uploadDate;
    /** 图片上传到P站的时间,HHmmss格式 **/
    private String uploadTime;
    /** 图片本地保存路径 **/
    private String localPicPath;
    /** 图片本地保存文件名 **/
    private String localPicName;
    /** 图片大小，以B为单位 **/
    private Integer picSizeB;
    /** 图片大小，以KB为单位 **/
    private Integer picSizeKB;
    /** 图片大小，以MB为单位 **/
    private Integer picSizeMB;
    /** 是否下载成功，0-下载失败，1-下载成功 **/
    private Integer downloadSuccess;
    /** 新增时间 **/
    private Date addTime;
    /** 更新时间 **/
    private Date updateTime;
}
