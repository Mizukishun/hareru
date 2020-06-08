package org.kiharu.hareru.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 动漫花园资源信息
 *
 * @Author kiharu
 * @Date 2020-06-08
 */
@Data
public class DmhyResource {

    /** 标题 **/
    private String title;
    /** 上传时间，字符串格式表示 **/
    private String uploadTimeStr;
    /** 上传时间，精确到分 **/
    private Date uploadTime;
    /** 分类 **/
    private String sort;
    /** 分类ID **/
    private String sortId;
    /** 字幕组ID，TODO--这是花园定义的，自己这边需要另做统计，因为花园里的没有全部统计收录 **/
    private String teamId;
    /** 字幕组名称，TODO--这只是花园定义的，字幕组这块需要自己另外单独进行统计处理 **/
    private String teamName;
    /** 主题ID，从详情链接中解析出的 **/
    private String topicId;
    /** 详情链接， **/
    private String detailLink;
    /** 外部磁链 **/
    private String externalMagnet;
    /** 文件大小  **/
    private String size;
    /** 发布者名称 **/
    private String uploadUserName;
    /** 种子数量 **/
    private Integer seedCount;
    /** 正在下载的人数 **/
    private Integer downloadingCount;
    /** 已下载完成的人数  **/
    private Integer downloadedCount;
    /** 评论数量（约*条评论） **/
    private Integer commentCount;

    /** 简介 **/
    private String introduction;
    /** 会员专用链接 **/
    private String memberMagnet;
    /** Magnet链接 **/
    private String magnet;
    /** 带资源信息的Magnet链接 **/
    private String moreInfoMagnet;
    /** Magnet链接typeII **/
    private String magnetTypeII;
    /** 弹幕播放链接 **/
    private String ddplayUrl;
    /** 外部搜索链接 **/
    private String googleSearchUrl;
    /** 文件列表 **/
    private List<String> fileList;
    /** 文件列表中每个文件的大小，顺序同上面文件列表的顺序 **/
    private List<String> fileSizeList;
}
