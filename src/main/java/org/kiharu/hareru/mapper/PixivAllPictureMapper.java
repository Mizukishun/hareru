package org.kiharu.hareru.mapper;

import org.kiharu.hareru.entity.PixivAllPicture;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author kiharu
 * @Date 2020-04-28
 */
public interface PixivAllPictureMapper {

    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer batchInsert(List<PixivAllPicture> list);

    /**
     * 查询原始图片地址
     * @param pixivIdList
     * @return
     */
    List<String> select(List<String> pixivIdList);
}
