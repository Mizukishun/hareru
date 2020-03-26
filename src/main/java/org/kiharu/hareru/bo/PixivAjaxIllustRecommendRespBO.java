package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 从Pixiv图片推荐接口返回的结果对象
 * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
 */
@Data
public class PixivAjaxIllustRecommendRespBO implements Serializable {

    private Boolean error;

    private String message;

    private PixivAjaxIllustRecommendRespBodyBO body;
}
