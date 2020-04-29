package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 从/ajax/illust/80391469/pages接口返回结果中解析出的图片信息
 * 一个pixivId对应多张图片信息
 * https://www.pixiv.net/ajax/illust/80391469/pages
 * 具体数据来源可参照"pixivId=75919281获取的多张图片接口返回的内容.txt"
 * @Author kiharu
 * @Date 2020-04-29
 */
@Deprecated
@Data
public class PixivAjaxIllustPagePictureBO implements Serializable {

    private static final long serialVersionUID = -317202480559696580L;
    /** 原始大图地址 **/
    private String originalUrl;
    /** 图片宽度 **/
    private Integer width;
    /** 图片高度 **/
    private Integer height;
    /** 缩略图地址 **/
    private String thumbMiniUrl;
    /** 小图地址 **/
    private String smallUrl;
    /** 普通图地址 **/
    private String regularUrl;
}
