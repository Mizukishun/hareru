package org.kiharu.hareru.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import org.apache.commons.collections4.MapUtils;
import org.kiharu.hareru.bo.PixivAjaxIllustPagesUrlInfoBO;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.entity.PixivPictureDetailInfo;
import org.kiharu.hareru.mapper.PixivPictureDetailInfoMapper;
import org.kiharu.hareru.pixiv.PixivAsyncRequestUtils;
import org.kiharu.hareru.pixiv.PixivCallbackBuilder;
import org.kiharu.hareru.service.PixivInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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
    private PixivPictureDetailInfoMapper pixivPictureDetailInfoMapper;


    @Override
    public Integer downloadPictureInfoByRange(Integer startPixivId, Integer endPixivId) {
        log.info("开始请求获取范围为[{}, {}]内的所有图片信息到数据库表pixiv_picture_detail_info表中", startPixivId, endPixivId);
        // TODO--这里没有限制[startPixivId, endPixivId]的范围，但如果太大了，因为全部请求都是异步的，所以有可能会撑爆内存--之后看是不是多加一层限制
        // 如果入参为空，则指定pixivId的取值范围为[0,Integer.MAX_VALUE]
        if (startPixivId == null || startPixivId < 0) {
            startPixivId = 0;
        }
        if (endPixivId == null || endPixivId < 0) {
            endPixivId = Integer.MAX_VALUE;
        }

        Integer count = endPixivId - startPixivId + 1;

        CopyOnWriteArrayList<PixivPictureDetailInfo> detailInfoList = new CopyOnWriteArrayList<>();

        /*for (int i = startPixivId; i <= endPixivId; ++i) {
            String pixivId = String.valueOf(i);
            Callback callback = PixivCallbackBuilder.getArtworksCallback(detailInfoList);
            PixivAsyncRequestUtils.getRespHtmlFromArtworks(pixivId, callback);
        }*/

        for (int i = 0, j = 1; i < count; ++i) {
            // 控制下每次都是请求的数量
            if (i > j * PixivConstants.ASYNC_MAX_REQUEST_COUNT) {
                try {
                    log.info("异步请求图片信息downloadPictureInfoByRange暂时休眠10秒钟,i={}, j={}", i, j);
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    log.error("线程休眠失败", e);
                    continue;
                }
                ++j;
            }
            String pixivId = String.valueOf(startPixivId + i);
            Callback callback = PixivCallbackBuilder.getArtworksCallback(detailInfoList);
            PixivAsyncRequestUtils.getRespHtmlFromArtworks(pixivId, callback);
        }

        // TODO--如何判断已经把全部的都请求处理完了？？
        // 用数量来判断上面的所有异步请求已经处理完成了
        while (detailInfoList.size() < count) {
            try {
                log.info("异步请求artworks接口还未达到{}数量的记录，等待5秒钟", count);
                Thread.sleep(5 * 1000L);
            } catch (InterruptedException e) {
                log.error("等待异步请求图片信息时出错", e);
                break;
            }
        }
        log.info("异步请求artworks接口已获取到{}数量的记录", count);

        /** 针对pageCount > 1的需要再进一步获取其对应的多张图片信息 **/
        // pixivId对应的PixivPictureDetailInfo信息，以便后续处理其对应的多张图片信息
        Map<String, PixivPictureDetailInfo> pageCountMoreThanOnePixivIdDetailInfoMap = detailInfoList.parallelStream()
                .filter(item -> item.getPixivId() != null)
                .filter(item -> !PixivConstants.INVALID_PIXIV_ID.equals(item.getPixivId()))
                // 过滤掉pageCount为null的
                .filter(item -> item.getPageCount() != null)
                // 然后再过滤出pageCount > 1
                .filter(item -> item.getPageCount().compareTo(1) > 0)
                .collect(Collectors.toMap(PixivPictureDetailInfo::getPixivId, item -> item));

        // pixivId只对应一张图片
        List<PixivPictureDetailInfo> pageCountEqualsOneDetailInfoList = detailInfoList.stream()
                .filter(item -> item.getPixivId() != null)
                .filter(item -> !PixivConstants.INVALID_PIXIV_ID.equals(item.getPixivId()))
                .filter(item -> !pageCountMoreThanOnePixivIdDetailInfoMap.keySet().contains(item.getPixivId()))
                .collect(Collectors.toList());


        /*log.info("测试下当前内存主要数据的大小,范围为[{}, {}],detailInfoList={}b, pageCountMoreThanOnePixivIdDetailInfoMap={}b, pageCountEqualsOneDetailInfoList={}b",
                startPixivId, endPixivId, detailInfoList, pageCountMoreThanOnePixivIdDetailInfoMap, pageCountEqualsOneDetailInfoList);*/
        detailInfoList = null;

        // 这里可以先执行一波插入
        Integer insertCount = batchInsertPixivPictureDetailInfo(pageCountEqualsOneDetailInfoList);
        log.info("下载pixivId从[{},{}]范围的图片信息成功，共插入{}条记录(阶段一)", startPixivId, endPixivId, insertCount);

        Integer pageCountInsertCount = downloadPageCountMoreThanOnePictures(pageCountMoreThanOnePixivIdDetailInfoMap);
        log.info("下载pixivId从[{},{}]范围的图片信息成功，共插入{}条记录(阶段二)", startPixivId, endPixivId, pageCountInsertCount);

        Integer totalCount = insertCount + pageCountInsertCount;

        log.info("下载pixivId从[{}, {}]范围的图片信息，其中pageCount=1的有{}个pixivId，而pageCount>1的有{}个pixivId,其中pageCount=1总共插入了{}条记录，pageCount>1的总共插入了{}条记录",
                startPixivId, endPixivId, pageCountEqualsOneDetailInfoList.size(), pageCountMoreThanOnePixivIdDetailInfoMap.size(), insertCount, pageCountInsertCount);

        // TODO--采用异步请求的方式来针对pixivId对应图片信息的获取
        return totalCount;
    }

    /**
     * 下载pixivId对应有多张图片的信息
     * @param pixivIdDetailInfoMap 对应多张图片的pixivId的PixivPictureDetailInfo
     * @return
     */
    @Override
    public Integer downloadPageCountMoreThanOnePictures(Map<String, PixivPictureDetailInfo> pixivIdDetailInfoMap) {
        if (MapUtils.isEmpty(pixivIdDetailInfoMap)) {
            return 0;
        }
        // 所有pageCount > 1的pixivId，也即这些pixivId还需要请求ajax/illust/{pixivId}/pages接口获取多张图片的地址信息
        List<String> pageCountMoreThanOnePixivIdList = pixivIdDetailInfoMap.keySet().stream().collect(Collectors.toList());

        // 所有需要请求ajax/illust/{pixivId}/pages接口的pageCount之和
        Integer pageCountSum = pixivIdDetailInfoMap.entrySet()
                .stream()
                .map(item -> item.getValue().getPageCount() == null ? 0 : item.getValue().getPageCount())
                .reduce((item1, item2) -> item1 + item2)
                .get();

        CopyOnWriteArrayList<PixivAjaxIllustPagesUrlInfoBO> pageCountInfoBOList = new CopyOnWriteArrayList<>();
        Integer moreThanOnePixivIdSize = pageCountMoreThanOnePixivIdList.size();
        /*for (String pixivId : pageCountMoreThanOnePixivIdList) {
            Callback callback = PixivCallbackBuilder.getCallbackFromAjaxIllustPage(pageCountInfoBOList);
            PixivAsyncRequestUtils.getResponseFromAjaxIllustPage(pixivId, callback);
        }*/

        for (int i = 0, j = 1; i < moreThanOnePixivIdSize; ++i) {
            // 控制下每次都是请求的数量
            if (i > j * PixivConstants.ASYNC_MAX_REQUEST_COUNT) {
                try {
                    log.info("异步请求多张图片信息downloadPageCountMoreThanOnePictures暂时休眠10秒钟，i={}, j={}", i, j);
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    log.error("线程休眠失败", e);
                    continue;
                }
                ++j;
            }
            Callback callback = PixivCallbackBuilder.getCallbackFromAjaxIllustPage(pageCountInfoBOList);
            String pixivId = pageCountMoreThanOnePixivIdList.get(i);
            PixivAsyncRequestUtils.getResponseFromAjaxIllustPage(pixivId, callback);
        }

        // 这里等待上面的异步请求全部处理完成
        while (pageCountInfoBOList.size() < pageCountSum) {
            try {
                Thread.sleep(5 * 1000);
                log.info("获取pixivId对应多张图片的请求还未达到{}数量，等待5秒钟", pageCountSum);
            } catch (InterruptedException e) {
                log.error("等待异步请求pixivId对应多张图片信息时出错", e);
                break;
            }
        }
        log.info("异步请求pixivId对应的多张图片信息成功，总共请求了{}个的pixivId对应的多张图片，总共有{}张图片", pixivIdDetailInfoMap.size(), pageCountSum);

        List<PixivPictureDetailInfo> detailInfoList = new ArrayList<>();
        for (PixivAjaxIllustPagesUrlInfoBO infoBO : pageCountInfoBOList) {
            // 所有pixivId为null的都是出错了的，跳过
            if (infoBO.getPixivId() == null) {
                continue;
            }
            String pixivId = infoBO.getPixivId();
            if (!pixivIdDetailInfoMap.keySet().contains(pixivId)) {
                continue;
            }

            PixivPictureDetailInfo pixivPictureDetailInfo = new PixivPictureDetailInfo();
            BeanUtils.copyProperties(pixivIdDetailInfoMap.get(pixivId), pixivPictureDetailInfo);
            pixivPictureDetailInfo.setOriginalUrl(infoBO.getOriginal());
            pixivPictureDetailInfo.setRegularUrl(infoBO.getRegular());
            pixivPictureDetailInfo.setSmallUrl(infoBO.getSmall());
            pixivPictureDetailInfo.setThumbUrl(infoBO.getThumbMini());
            pixivPictureDetailInfo.setWidth(infoBO.getWidth());
            pixivPictureDetailInfo.setHeight(infoBO.getHeight());

            detailInfoList.add(pixivPictureDetailInfo);
        }

        Integer pageCountInsertCount = batchInsertPixivPictureDetailInfo(detailInfoList);

        return pageCountInsertCount;

    }

    /**
     * 分批插入pixiv_picture_detail_info表记录，防止单条SQL语句长度超过数据库限制
     * @param pixivPictureDetailInfoList
     * @return
     */
    @Override
    public Integer batchInsertPixivPictureDetailInfo(List<PixivPictureDetailInfo> pixivPictureDetailInfoList) {
        Integer insertCount = 0;
        Integer count = pixivPictureDetailInfoList.size();
        // 控制一次性插入数据库中的记录数量，防止单条SQL语句长度超出数据库限制
        Integer fromIndex = 0;
        Integer endIndex = PixivConstants.MAX_INSERT_RECORD_COUNT;
        do {
            if (count.compareTo(endIndex) < 0) {
                endIndex = count;
            }
            List<PixivPictureDetailInfo> tempInsertList = pixivPictureDetailInfoList.subList(fromIndex, endIndex);
            Integer tempInsertCount = pixivPictureDetailInfoMapper.batchInsert(tempInsertList);
            insertCount += tempInsertCount;

            log.info("分批插入pixiv_picture_detail_info表中{}条记录，fromIndex={}, endIndex={}", tempInsertCount, fromIndex, endIndex);

            fromIndex += PixivConstants.MAX_INSERT_RECORD_COUNT;
            endIndex += PixivConstants.MAX_INSERT_RECORD_COUNT;
        } while(fromIndex < count);

        return insertCount;
    }


}
