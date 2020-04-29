package org.kiharu.hareru.mapper;

import org.kiharu.hareru.entity.PixivPictureDetailInfo;

import java.util.List;

/**
 * @Author kiharu
 * @Date 2020-04-29
 */
public interface PixivPictureDetailInfoMapper {

    /**
     * 批量插入记录
     * @param list
     * @return
     */
    Integer batchInsert(List<PixivPictureDetailInfo> list);

    /**
     * 查询对应pixivId的原始图片地址
     * @param pixivIdList
     * @return
     */
    List<String> selectOriginalUrls(List<String> pixivIdList);
}
