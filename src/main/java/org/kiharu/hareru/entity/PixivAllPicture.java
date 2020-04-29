package org.kiharu.hareru.entity;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author kiharu
 * @Date 2020-04-2020-04-28
 */
@Data
public class PixivAllPicture implements Serializable {

    private static final long serialVersionUID = 6555136425277895588L;
    /** 主键 **/
    private Long id;
    /** P站图片ID **/
    private String pixivId;
    /** 图片作者ID **/
    private String authorId;
    /** 原始图片地址 **/
    private String originalUrl;
    /** 图片上传到P站的日期，yyyyMMdd格式 **/
    private String uploadDate;
    /** 图片上传到P站的时间，HHmmss格式 **/
    private String uploadTime;
    /** 图片后缀，也即图片类型 **/
    private String picSuffix;
    /** 是否R18,0-否，1-是 **/
    private Integer r18Restrict;
    /** 图片在pixiv是否存在，0-不存在，无法找到，1-存在 **/
    private Integer existed;
    /** 是否已下载到本地，0-未下载，1-已下载 **/
    private Integer downloaded;
    /** 新增时间 **/
    private Date addTime;
    /** 更新时间 **/
    private Date updateTime;
}
