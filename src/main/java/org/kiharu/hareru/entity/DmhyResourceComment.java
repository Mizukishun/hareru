package org.kiharu.hareru.entity;

import lombok.Data;

import java.util.Date;

/**
 * 资源的评论信息
 *
 * @Author kiharu
 * @Date 2020-06-08
 */
@Data
public class DmhyResourceComment {

    /** 评论用户名 **/
    private String commentUserName;
    /** 评论用户头像地址 **/
    private String commentUserImage;
    /** 评论发表时间 **/
    private Date commentTime;
    /** 发表人IP **/
    private String commentUserIP;
    /** 评论标题 **/
    private String commentTitle;
    /** 评论内容 **/
    private String commentContent;
}
