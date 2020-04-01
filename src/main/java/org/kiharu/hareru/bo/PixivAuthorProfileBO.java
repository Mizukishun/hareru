package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 从/ajax/user/1277076/profile/all获取到作者的所有插画和漫画作品ID
 */
@Data
public class PixivAuthorProfileBO implements Serializable {

    /**
     * 作者的P站ID
     */
    private String pixivUserId;

    /**
     * 插画的pixivId
     */
    private Set<String> illustIdList;

    /**
     * 漫画的pixivId
     */
    private Set<String> mangaIdList;
}
