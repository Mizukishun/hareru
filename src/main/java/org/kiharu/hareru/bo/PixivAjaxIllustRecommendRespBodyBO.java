package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PixivAjaxIllustRecommendRespBodyBO implements Serializable {

    private List<PixivAjaxIllustRecommendRespIllustBO> illusts;

    private List<String> nextIds;

    //private PixivAjaxIllustRecommendRespMethodsBO recommendMethods;
}
