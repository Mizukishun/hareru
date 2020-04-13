package org.kiharu.hareru.mapper;

import org.kiharu.hareru.entity.PixivPictureInfo;

import java.util.List;

public interface PixivPictureInfoMapper {

    /**
     * 批量插入P站图片信息
     * @param list
     * @return
     */
    Integer batchInsert(List<PixivPictureInfo> list);
}
