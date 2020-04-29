package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 从/ajax/illust/80391469/pages接口返回结果中解析出的图片信息
 * 具体数据来源可参照"pixivId=75919281获取的多张图片接口返回的内容.txt"
 * @Author kiharu
 * @Date
 */
@Data
public class PixivAjaxIllustPagesUrlInfoBO implements Serializable {

    private static final long serialVersionUID = 8122060267214675366L;
    /** 缩略图地址 **/
    private String thumbMini;
    /** 小图地址 **/
    private String small;
    /** 普通图地址 **/
    private String regular;
    /** 原始大图地址 **/
    private String original;
    /** 图片宽度 **/
    private Integer width;
    /** 图片高度 **/
    private Integer height;
    /** 图片P站ID **/
    private String pixivId;
}
