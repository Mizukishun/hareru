package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存Pixiv原始图片url中解析出来的信息
 * https://i.pximg.net/img-original/img/2020/03/22/00/52/23/80273312_p0.jpg
 */
@Data
public class PixivPictureUrlInfoBO implements Serializable {

    /** 域名，对应i.pximg.net **/
    private String host;
    /** 图片日期，对应2020/03/22，组装成yyyyMMdd格式的 **/
    private String date;
    /** 图片时间，对应00/52/23，组装成hhmmss格式的 **/
    private String time;
    /** pixivId，对应80273312 **/
    private String pixivId;
    /** 图片类型后缀，对应.jpg **/
    private String suffix;
    /** 原始图片URL **/
    private String url;
}
