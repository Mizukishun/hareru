package org.kiharu.hareru.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.kiharu.hareru.entity.PixivAllPicture;
import org.kiharu.hareru.entity.PixivPictureDetailInfo;
import org.kiharu.hareru.mapper.PixivAllPictureMapper;
import org.kiharu.hareru.mapper.PixivPictureDetailInfoMapper;
import org.kiharu.hareru.pixiv.PixivAsyncRequestUtils;
import org.kiharu.hareru.pixiv.PixivCallbackBuilder;
import org.kiharu.hareru.service.PixivInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Author kiharu
 * @Date 2020-04-28
 */
@Slf4j
@Service
public class PixivInfoServiceImpl implements PixivInfoService {

    @Resource
    private PixivAllPictureMapper pixivAllPictureMapper;

    @Resource
    private PixivPictureDetailInfoMapper pixivPictureDetailInfoMapper;


    @Override
    public Integer downloadPixivPictureUrl(Integer startPixivId, Integer endPixivId) {
        // 如果入参为空，则指定pixivId的取值范围为[0,Integer.MAX_VALUE]
        if (startPixivId == null) {
            startPixivId = 0;
        }
        if (endPixivId == null) {
            endPixivId = Integer.MAX_VALUE;
        }

        CopyOnWriteArrayList<PixivPictureDetailInfo> detailInfoList = new CopyOnWriteArrayList<>();

        for (int i = startPixivId; i <= endPixivId; ++i) {
            String pixivId = String.valueOf(i);
            Callback callback = PixivCallbackBuilder.getArtworksCallback(detailInfoList);
            PixivAsyncRequestUtils.getRespHtmlFromArtworks(pixivId, callback);
        }

        // TODO--如何判断已经把全部的都请求处理完了？？
        // 用数量来处理
        Integer count = endPixivId - startPixivId + 1;
        while (detailInfoList.size() < count) {
            try {
                Thread.sleep(5 * 1000);
                log.info("allPictures还未达到{}数量的记录，等待5秒钟", count);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("等待异步请求图片信息时出错", e);
                break;
            }
        }
        //Integer insertCount = pixivAllPictureMapper.batchInsert(allPictureList);
        // 针对pageCount > 1的需要再进一步获取其对应的多张图片信息
        List<String> pageCountMoreThanOnePixivIdList = detailInfoList.parallelStream()
                // TODO--待检测如果pageCount为null时，下面能正常运行吗？
                .filter(item -> item.getPageCount() > 1)
                .map(item -> item.getPixivId())
                .collect(Collectors.toList());



        Integer insertCount = pixivPictureDetailInfoMapper.batchInsert(detailInfoList);


        log.info("下载pixivId从[{},{}]范围的图片信息成功，共插入{}条记录", startPixivId, endPixivId, insertCount);


        // TODO--采用异步请求的方式来针对pixivId对应图片信息的获取
        return insertCount;
    }


}
