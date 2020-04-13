package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 从/ajax/illust/80391469/pages接口返回结果中解析出的图片信息
 * @Author kiharu
 * @Date
 */
@Data
public class PixivAjaxIllustPagesUrlInfoBO implements Serializable {

    private static final long serialVersionUID = 8122060267214675366L;
    private String thumbMini;
    private String small;
    private String regular;
    private String original;
    private Integer width;
    private Integer height;
}
