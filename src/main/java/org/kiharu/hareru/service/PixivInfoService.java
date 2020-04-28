package org.kiharu.hareru.service;

/**
 * @Author kiharu
 * @Date 2020-04-28
 */
public interface PixivInfoService {

    /**
     * 下载P站所有图片原始大图地址
     * @param startPixivId
     * @param endPixivId
     * @return
     */
    Integer downloadPixivPictureUrl(Integer startPixivId, Integer endPixivId);


}
