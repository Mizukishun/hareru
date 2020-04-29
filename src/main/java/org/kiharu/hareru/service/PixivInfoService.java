package org.kiharu.hareru.service;

import org.kiharu.hareru.entity.PixivPictureDetailInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author kiharu
 * @Date 2020-04-28
 */
public interface PixivInfoService {

    /**
     *
     * 下载pixivId范围为[startPixivId, endPixivId]内的所有P站图片信息到pixiv_picture_detail_info表中
     * @param startPixivId
     * @param endPixivId
     * @return
     */
    Integer downloadPictureInfoByRange(Integer startPixivId, Integer endPixivId);

    /**
     * 下载pixivId对应有多张图片的信息
     * @param pixivIdDetailInfoMap 对应多张图片的pixivId的PixivPictureDetailInfo
     * @return
     */
    Integer downloadPageCountMoreThanOnePictures(Map<String, PixivPictureDetailInfo> pixivIdDetailInfoMap);

    /**
     * 分批插入pixiv_picture_detail_info表记录，防止单条SQL语句长度超过数据库限制
     * @param pixivPictureDetailInfoList
     * @return
     */
    Integer batchInsertPixivPictureDetailInfo(List<PixivPictureDetailInfo> pixivPictureDetailInfoList);

}
