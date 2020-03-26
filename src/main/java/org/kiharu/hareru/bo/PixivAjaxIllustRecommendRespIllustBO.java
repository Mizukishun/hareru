package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PixivAjaxIllustRecommendRespIllustBO implements Serializable {

    private String illustId;

    private String illustTitle;
    private String id;
    private String title;
    private Integer illustType;
    private Integer xRestrict;
    private Integer restrict;
    private Integer sl;
    private String url;
    private String description;
    private List<String> tags;
    private String userId;
    private String userName;
    private Integer width;
    private Integer height;
    private Integer pageCount;
    private Boolean isBookmarkable;
    private String bookmarkData;
    private String alt;
    private Boolean isAdContainer;
    private String profileImageUrl;
    private String type;

    // TODO--其它属性之后补充完成，可参见从Pixiv图片推荐接口返回的结果.txt
}
