package org.kiharu.hareru.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.kiharu.hareru.bo.PixivAjaxIllustPagesUrlInfoBO;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.entity.PixivPictureInfo;
import org.kiharu.hareru.mapper.PixivPictureInfoMapper;
import org.kiharu.hareru.pixiv.PixivPictureUtils;
import org.kiharu.hareru.pixiv.PixivRequestUtils;
import org.kiharu.hareru.pixiv.PixivResultParser;
import org.kiharu.hareru.service.PixivDownloadService;
import org.kiharu.hareru.util.PixivHeadersUtils;
import org.kiharu.hareru.util.PixivUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用于下载Pixiv图片的类
 */
@Slf4j
@Service
public class PixivDownloadServiceImpl implements PixivDownloadService {

    @Autowired
    private PixivPictureInfoMapper pixivPictureInfoMapper;

    /**
     * 下载指定url的图片到本地
     * @param url
     */
    /*public void downloadPixivPicture(String url) {
        // TODO--测试用，之后删除
        long begin = System.currentTimeMillis();
        long stage1 = 0, stage2 = 0, stage3 = 0, stage4 = 0, stage5 = 0;

        // 构建请求头
        Headers headers = PixivHeadersUtils.getSimpleHeaders();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        // TODO--测试用，之后删除
        stage1 = System.currentTimeMillis();
        // 请求获取图片数据
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("请求下载Pixiv图片失败，url={}", url);
                // TODO--待确认这里是否适合放return语句
                return;
            }
            // TODO--测试用，之后删除
            stage2 = System.currentTimeMillis();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body().byteStream());

            File savedPicFile = PixivUtils.getSavedPicFile(url);
            FileOutputStream fileOutputStream = new FileOutputStream(savedPicFile, true);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);


            // TODO--测试用，之后删除
            stage3 = System.currentTimeMillis();
            // TODO--经测试，下面这里太慢了，之后看有没有啥办法提升下这里的处理速度
            // 将网络图片数据保存到本地文件中
            int b = 0;
            while ((b = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(b);
            }
            *//*byte[] bytes = new byte[2048];
            int length = 0;
            int off = 0;
            while((length = bufferedInputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes);
            }*//*

            // TODO--测试用，之后删除
            stage4 = System.currentTimeMillis();

            *//*fileOutputStream.flush();
            fileOutputStream.close();*//*
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("下载图片数据出错，url=")
                    .append(url);
            log.error(errorMsg.toString(), ex);
        }
        // TODO--测试用，之后删除
        stage5 = System.currentTimeMillis();

        log.info("下载图片url={}所用的各个阶段时间为：stage1={}ms, stage2={}ms, stage3={}ms, stage4={}ms, stage5={}ms，总时间total={}ms", url, stage1 - begin, stage2 - stage1, stage3 - stage2, stage4 - stage3, stage5 - stage4, stage5 - begin);
    }*/

    /**
     * 尝试采用异步的方式下载图片，看下载速度是否更好点
     * @param url 下载图片的地址
     */
    /*@Override
    public void asyncDownloadPixivPicture(String url){
        Headers headers = PixivHeadersUtils.getSimpleHeaders();
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().headers(headers).url(url).build();
        File savedPicFile = PixivUtils.getSavedPicFile(url);

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                log.error("异步请求下载图片失败，url=" + url, ex);
            }

            @Override
            public void onResponse(Call call, Response response) {
                //BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body().byteStream());
                try {
                    log.info("进入异步onResponse");

                    byte[] bytes = response.body().bytes();
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(savedPicFile));
                    bufferedOutputStream.write(bytes);


                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    bytes = null;
                    log.info("我送你离开~");
                } catch (IOException ex) {
                    StringBuilder errorMsg = new StringBuilder().append("异步下载图片保存时出错,file=").append(savedPicFile.getAbsolutePath());
                    log.error(errorMsg.toString(), ex);
                }

            }
        });
        log.info("Completed");
    }*/

    /**
     * 根据pixivId下载其对应的所有图片
     * @param pixivId
     */
    /*@Override
    public void asyncDownloadPictureByPixivId(String pixivId) {
        if (StringUtils.isEmpty(pixivId)) {
            log.error("downloadPictureByPixivId方法的入参pxivId不能为空");
            return;
        }

        // 先根据pixivId获取原始大图的地址
        List<String> urls = PixivPictureUtils.getUrlsFromArtworksByPixivId(pixivId);
        if (CollectionUtils.isEmpty(urls)) {
            log.error("获取pixivId={}对应的所有原始大图地址出错：", pixivId);
        }
        for (String url : urls) {
            try {
                asyncDownloadPixivPicture(url);
            } catch (Exception ex) {
                // 下载pixivId对应的所有图片之一出错时，记录日志，跳过--TODO--之后再补充针对出错的处理
                StringBuilder errorMsg = new StringBuilder().append("下载图片出错，图片地址url=").append(url);
                log.error(errorMsg.toString(), ex);
                continue;
            }
        }
    }*/

    /**
     * 根据pixivId下载其对应的所有图片，这里使用异步下载图片
     * @param pixivId
     */
    /*@Override
    public void downloadPictureByPixivId(String pixivId) {
        if (StringUtils.isEmpty(pixivId)) {
            log.error("downloadPictureByPixivId方法的入参pxivId不能为空");
        }

        List<String> urls = PixivPictureUtils.getUrlsFromArtworksByPixivId(pixivId);
        if (CollectionUtils.isEmpty(urls)) {
            log.error("获取pixivId={}对应的所有原始大图地址出错：", pixivId);
        }
        for (String url : urls) {
            try {
                asyncDownloadPixivPicture(url);
            } catch (Exception ex) {
                // 下载pixivId对应的所有图片之一出错时，记录日志，跳过--TODO--之后再补充针对出错的处理
                StringBuilder errorMsg = new StringBuilder().append("下载图片出错，图片地址url=").append(url);
                log.error(errorMsg.toString(), ex);
                continue;
            }
        }
    }*/

    /**
     * 根据pixivId下载其对应的可能的多张图片
     * TODO--需要测试下如果pixivId本来就只对应一张图片，下面这里能否请求获取到该唯一一张图片的原始大图地址
     * @param pixivId
     */
    /*@Override
    public void asyncDownloadMultiPicturesByPixivId(String pixivId) {
        List<String> urls = PixivPictureUtils.getUrlsFromAjaxIllustPagesByPixivId(pixivId);

        for (String url : urls) {
            try {
                asyncDownloadPixivPicture(url);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }*/

    /**
     * 异步下载图片，同时由上层指定本地保存的文件
     * @param url 图片下载地址
     * @param file 需由外层保证此文件已创建了
     */
    @Override
    public void asyncDownloadPixivPicture(String url, File file) {
        if (file == null || !file.exists()) {
            log.error("本地要保存的图片文件不存在，请先新建文件");
            return ;
        }

        Headers headers = PixivHeadersUtils.getSimpleHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                // 下面这里new FileOutputStream(file)默认是按覆盖的方式进行写入的，也即如果之前已经有了同名文件，则会被新下载的覆盖掉之前的
                try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));){
                    log.info("进入异步onResponse，url={}", url);
                    byte[] bytes = response.body().bytes();
                    bufferedOutputStream.write(bytes);

                    // 这里主动进行关闭释放资源
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    bytes = null;
                    log.info("我送你离开~");
                } catch (IOException ex) {
                    StringBuilder errorMsg = new StringBuilder()
                            .append("异步下载图片保存时出错，filePath=")
                            .append(file.getAbsolutePath())
                            .append("\nurl=")
                            .append(url);
                    log.error(errorMsg.toString(), ex);
                }
            }

            @Override
            public void onFailure(Call call, IOException ex) {
                StringBuilder errorMsg = new StringBuilder()
                        .append("异步请求图片出错，url=")
                        .append(url);
                log.error(errorMsg.toString(), ex);
            }
        });
    }

    /**
     * 下载pixivId对应的所有图片，可能只有一张，也可能有多张
     * 综合了上面downloadPictureByPixivId和downloadMultiPicturesByPixivId方法的
     * @param pixivId
     */
    @Override
    public void downloadPicturesByPixivId(String pixivId, String subject) {
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return;
        }
        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();
        String pixivUserId = detailInfoBO.getUserId();
        Integer pageCount = detailInfoBO.getPageCount();

        Map<String, File> urlFileMap = new HashMap<>(8);

        if (pageCount.equals(1)) {
            // 如果pixivId只对应一张图片，则不需要再另外请求接口取查询额外的多张图片原始地址
            String url = detailInfoBO.getOriginalUrl();
            File file = PixivUtils.getLocalSavedPicFile(url, subject, pixivUserId);
            urlFileMap.put(url, file);
        } else {
            // 如果pixivId对应多张图片，则还需要另外请求接口查询额外的多张图片的原始地址
            List<String> urls = PixivPictureUtils.getUrlsFromAjaxIllustPagesByPixivId(pixivId);
            for (String url : urls) {
                File file = PixivUtils.getLocalSavedPicFile(url, subject, pixivUserId);
                urlFileMap.put(url, file);
            }
        }

        for (Map.Entry<String, File> entry : urlFileMap.entrySet()) {
            String url = entry.getKey();
            File file = entry.getValue();
            asyncDownloadPixivPicture(url, file);
        }
    }


    /**
     * 下载所有根据pixivId而推荐的图片（一层）
     * @param pixivId
     */
    @Override
    public void downloadRecommendPictureByPixivId(String pixivId) {
        List<String> pixivIdList = PixivPictureUtils.getPixivIdsFromAjaxIllustRecommend(pixivId);
        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_RECOMMEND).append(pixivId);

        /*for (String recommendPixivId : pixivIdList) {
            try {
                downloadPicturesByPixivId(recommendPixivId, subject.toString());
            } catch (Exception ex) {
                // 如果在下载一张图片的时候出错，则暂时跳过，同时记录下
                StringBuilder errorMsg = new StringBuilder()
                        .append("下载pixivId=")
                        .append(recommendPixivId)
                        .append("对应的图片发生错误");
                log.error(errorMsg.toString(), ex);
                continue;
            }
        }*/
        saveAndDownloadPictures(subject.toString(), pixivIdList);
    }

    /**
     * 保存并下载图片
     * @param subject
     * @param pixivIdList
     */
    @Override
    public void saveAndDownloadPictures(String subject, List<String> pixivIdList) {

        List<PixivPictureInfo> allPictureInfoList = new ArrayList<>(16);
        List<String> errorPixivIdList = new ArrayList<>(16);

        for (String pixivId : pixivIdList) {
            try {
                List<PixivPictureInfo> pixivIdPicturesInfo = downloadPixivIdPictures(pixivId, subject);

                if (CollectionUtils.isEmpty(pixivIdPicturesInfo)) {
                    errorPixivIdList.add(pixivId);
                }
                allPictureInfoList.addAll(pixivIdPicturesInfo);
            } catch (Exception ex) {
                // 在下载一个pixivId对应的图片时出错，则暂时跳过，同时进行记录保存
                StringBuilder errorMsg = new StringBuilder()
                        .append("下载pixivId=").append(pixivId)
                        .append("对应的图片时发生错误");
                log.error(errorMsg.toString(), ex);
                errorPixivIdList.add(pixivId);
            }
        }

        // 设置是否下载成功的标志
        allPictureInfoList.forEach(item -> {
            if (errorPixivIdList.contains(item.getPixivId())) {
                item.setDownloadSuccess(0);
            } else {
                item.setDownloadSuccess(1);
            }
        });

        // 保存到数据库
        // TODO--这里一次可能会插入几万甚至十万以上的记录，对于MySQL单条SQL语句长度限制max_allowed_packet值可能会超过，导致报PacketTooBigException异常
        // 虽然可以手动修改my.ini的该值，但感觉还是需要在这里做适应处理
        Integer insertRecordCount = allPictureInfoList.size();
        if (PixivConstants.MAX_INSERT_RECORD_COUNT.compareTo(insertRecordCount) < 0) {
            int fromIndex = 0;
            int endIndex = PixivConstants.MAX_INSERT_RECORD_COUNT;
            do {
                if (insertRecordCount.compareTo(endIndex) < 0) {
                    endIndex = insertRecordCount;
                }

                List<PixivPictureInfo> tempInsertList = allPictureInfoList.subList(fromIndex,endIndex);
                pixivPictureInfoMapper.batchInsert(tempInsertList);
                // TODO--测试用，之后删除
                log.info("{}主题总共有{}条记录，此次插入了[{},{}]范围之间的数据", subject, insertRecordCount, fromIndex, endIndex);

                fromIndex += PixivConstants.MAX_INSERT_RECORD_COUNT;
                endIndex += PixivConstants.MAX_INSERT_RECORD_COUNT;
            } while (fromIndex < insertRecordCount);
        }
        // TODO--测试用，之后删除
        log.info("{}主题总共插入了{}条记录", subject, insertRecordCount);


        //pixivPictureInfoMapper.batchInsert(allPictureInfoList);

    }

    /**
     * 下载pixivId指定的所有图片
     * TODO--暂时只下载插图，不下载漫画和动图这些，以后如果需要下载所有的，则可以选择注释掉方法里的TODO部分内容
     * @param pixivId
     * @param subject
     * @return 返回包含所有图片详细信息的结果
     */
    @Override
    public List<PixivPictureInfo> downloadPixivIdPictures(String pixivId, String subject) {
        List<PixivPictureInfo> result = new ArrayList<>();
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return result;
        }
        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();
        String pixivUserId = detailInfoBO.getUserId();
        Integer pageCount = detailInfoBO.getPageCount();
        Integer illustType = detailInfoBO.getIllustType();

        // TODO--这个以后看是否下载漫画及动图等所有类型的图片，现在暂时只下载插图
        // 如果不是插图，则跳过
        if (!illustType.equals(0)) {
            return result;
        }


        PixivPictureInfo pixivPictureInfo = PixivPictureUtils.convertDetailInfoBO2Entity(detailInfoBO);

        Map<String, File> urlFileMap = new HashMap<>(8);

        if (pageCount.equals(1)) {
            // 如果pixivId只对应一张图片，则不需要再另外请求接口取查询额外的多张图片原始地址
            String url = detailInfoBO.getOriginalUrl();
            File file = PixivUtils.getLocalSavedPicFile(url, subject, pixivUserId);
            urlFileMap.put(url, file);

            String localFileSavePath = file.getAbsolutePath();
            pixivPictureInfo.setLocalPicPath(localFileSavePath);
            pixivPictureInfo.setLocalPicName(localFileSavePath.substring(localFileSavePath.lastIndexOf(File.separator) + 1));

            result.add(pixivPictureInfo);
        } else {
            // 如果pixivId对应多张图片，则还需要另外请求接口查询额外的多张图片的原始地址--TODO，这个接口其实可以获取到更多的地址信息
            //List<String> urls = PixivPictureUtils.getUrlsFromAjaxIllustPagesByPixivId(pixivId);
            List<PixivAjaxIllustPagesUrlInfoBO> pixivAjaxIllustPagesUrlInfoBOList = PixivPictureUtils.getUrlsInfoFromAjaxIllustPagesByPixivId(pixivId);
            for (PixivAjaxIllustPagesUrlInfoBO urlInfoBO : pixivAjaxIllustPagesUrlInfoBOList) {
                String url = urlInfoBO.getOriginal();
                File file = PixivUtils.getLocalSavedPicFile(url, subject, pixivUserId);
                urlFileMap.put(url, file);

                PixivPictureInfo tempPixivPictureInfo = new PixivPictureInfo();
                BeanUtils.copyProperties(pixivPictureInfo, tempPixivPictureInfo);
                tempPixivPictureInfo.setOriginalUrl(url);
                tempPixivPictureInfo.setThumbUrl(urlInfoBO.getThumbMini());
                tempPixivPictureInfo.setSmallUrl(urlInfoBO.getSmall());
                tempPixivPictureInfo.setRegularUrl(urlInfoBO.getRegular());
                tempPixivPictureInfo.setWidth(urlInfoBO.getWidth());
                tempPixivPictureInfo.setHeight(urlInfoBO.getHeight());

                String localFileSavePath = file.getAbsolutePath();
                tempPixivPictureInfo.setLocalPicPath(localFileSavePath);
                tempPixivPictureInfo.setLocalPicName(localFileSavePath.substring(localFileSavePath.lastIndexOf(File.separator) + 1));

                result.add(tempPixivPictureInfo);
            }
        }

        for (Map.Entry<String, File> entry : urlFileMap.entrySet()) {
            String url = entry.getKey();
            File file = entry.getValue();
            asyncDownloadPixivPicture(url, file);
        }
        return result;
    }

    /*public  List<PixivPictureInfo> getPixivPictureInfoListFromDetailInfoBO(PixivPictureDetailInfoBO detailInfoBO) {
        List<PixivPictureInfo> result = new ArrayList<>();
        PixivPictureInfo pixivPictureInfo = PixivPictureUtils.convertDetailInfoBO2Entity(detailInfoBO);
        result.add(pixivPictureInfo);

        Integer pageCount = detailInfoBO.getPageCount();
        if (pageCount.equals(1)) {
            return result;
        }
        for (int i = 1; i < pageCount; ++i) {
            PixivPictureInfo tempPixivPictureInfo = new PixivPictureInfo();
            BeanUtils.copyProperties(pixivPictureInfo, tempPixivPictureInfo);

            // 替换原始图片地址，但不确定是否都是这样的--TODO--还需进一步确认
            String originalUrl = pixivPictureInfo.getOriginalUrl();
            String pi = "_p" + i;
            String tempOriginalUrl = originalUrl.replace("_p0", pi);

            tempPixivPictureInfo.setOriginalUrl(tempOriginalUrl);
            result.add(tempPixivPictureInfo);
        }
        return result;
    }*/

    /**
     * 下载所有这些多个pixivId所关联推荐的图片
     * @param pixivIdList
     */
    @Override
    public void downloadRecommendPictureByPixivIdList(List<String> pixivIdList) {
        // 先获取由这些pixivId所关联推荐的图片pixivId，这里通过Set保证没有重复的，
        Set<String> recommendPixivIdSet = PixivPictureUtils.getRecommendPixivIdSetByPixivIdList(pixivIdList);

        if (CollectionUtils.isEmpty(recommendPixivIdSet)) {
            log.warn("获取pixivIdList所关联推荐的所有图片的recommendPixivIdSet为空，其中pixivIdList为：\n{}", JSONObject.toJSONString(pixivIdList));
            return;
        }


        // 下载时发生错误的图片pixivId
        //Set<String> errorPixivIdSet = new HashSet<>();
        // 保存所有图片的文件夹名称
        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_RECOMMEND_MULTI).append(pixivIdList.get(0));

        // 循环下载所有图片
        /*for (String pixivId : recommendPixivIdSet) {
            try {

                downloadPicturesByPixivId(pixivId, subject.toString());
            } catch (Exception ex) {
                // 如果在下载一张图片的时候出错，则暂时跳过，同时记录下
                StringBuilder errorMsg = new StringBuilder()
                        .append("下载pixivId=")
                        .append(pixivId)
                        .append("对应的图片发生错误");
                log.error(errorMsg.toString(), ex);
                errorPixivIdSet.add(pixivId);
                continue;
            }
        }*/
        saveAndDownloadPictures(subject.toString(), recommendPixivIdSet.stream().collect(Collectors.toList()));

        //log.info("根据pixivIdList下载所关联推荐的所有图片的总数为：{},下载失败的图片数为：{}", recommendPixivIdSet.size(), errorPixivIdSet.size());
        log.info("根据pixivIdList下载所关联推荐的所有图片的总数为：{}", recommendPixivIdSet.size());

        // TODO--针对上面出错的再次尝试处理吗？

        return;
    }

    /**
     * 根据pixivId下载其所关联推荐的所有图片，同时还要把所有关联推荐的图片它们所又关联推荐的图片也下载下来
     * @param pixivId
     */
    @Override
    public void downloadRecommendPicByPixivIdWithTwoDepth(String pixivId) {
        List<String> initRecommendPixivIdList = PixivPictureUtils.getPixivIdsFromAjaxIllustRecommend(pixivId);

        Set<String> allRecommendPixivIdSet = new HashSet<>(1024);

        // 首先把第一层关联推荐的保存下
        allRecommendPixivIdSet.addAll(initRecommendPixivIdList);

        // 把第二层关联推荐的都保存下
        for (String recommendPixivId : initRecommendPixivIdList) {
            try {
                List<String> secondRecommendPixivIdList = PixivPictureUtils.getPixivIdsFromAjaxIllustRecommend(recommendPixivId);

                allRecommendPixivIdSet.addAll(secondRecommendPixivIdList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        log.info("下载关联推荐图片(两层)的图片总数为：{}", allRecommendPixivIdSet.size());

        // 保存图片的文件夹名称
        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_RECOMMEND_TWO_DEPTH).append(pixivId);

        // 下载错误的图片pixivId
        /*Set<String> errorPixivSet = new HashSet<>(16);
        // 下载所有关联推荐的图片
        for (String pid : allRecommendPixivIdSet) {
            try {
                downloadPicturesByPixivId(pid, subject.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                errorPixivSet.add(pid);
                continue;
            }
        }*/
        saveAndDownloadPictures(subject.toString(), allRecommendPixivIdSet.stream().collect(Collectors.toList()));
        log.info("下载关联推荐图片(两层)的图片总数为：{}", allRecommendPixivIdSet.size());

        //log.info("下载关联推荐图片(两层)的图片总数为：{}，其中下载错误的数量有：{}", allRecommendPixivIdSet.size(), errorPixivSet.size());
    }

    /**
     * 根据pixivId下载其关联推荐的图片，以及所有推荐图片的作者的所有作品
     * @param pixivId
     */
    @Override
    public void downloadRecommendPicAndAuthorWorksByPixivId(String pixivId) {
        // 获取所有推荐图片作者的所有作品图片pixiId
        Set<String> pixivIds = PixivPictureUtils.getPixivIdsFromRecommendPicAuthorsWorksByPixivId(pixivId);

        //log.info("请求获取到的所有图片pixivId数量为：{}", pixivIds.size());

        List<String> errorPixivIds = new ArrayList<>();

        log.info("根据pixivId={}获取到的推荐图片的所有作者的所有作品数量总数为：{}", pixivId, pixivIds.size());

        // 图片保存的文件夹名称
        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_RECOMMEND_AUTHOR).append(pixivId);

        /*for (String tempPixivId : pixivIds) {
            try {
                downloadPicturesByPixivId(tempPixivId, subject.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                errorPixivIds.add(tempPixivId);
                continue;
            }
        }
        log.info("请求下载的图片数量为：{}\n下载出错的图片数量为：{}\n下载成功的图片数量：{}",pixivIds.size(), errorPixivIds.size(), pixivIds.size() - errorPixivIds.size());
        */
        saveAndDownloadPictures(subject.toString(), pixivIds.stream().collect(Collectors.toList()));
    }

    /**
     * 下载作者的插画及漫画
     * 存放在以作者pixivUserId的文件夹中
     * @param pixivUserId
     */
    @Override
    public void downloadAuthorIllustAndManga(String pixivUserId) {
        Set<String> authorWorksId = PixivPictureUtils.getAuthorIllustAndMangaId(pixivUserId);
        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_AUTHOR).append(pixivUserId);
        /*for (String pixivId : authorWorksId) {
            try {
                downloadPicturesByPixivId(pixivId, subject.toString());
            } catch (Exception ex) {
                StringBuilder errorMsg = new StringBuilder("下载图片pixivId=")
                        .append(pixivId)
                        .append("出错");
                log.error(errorMsg.toString(), ex);
            }
        }*/
        saveAndDownloadPictures(subject.toString(), authorWorksId.stream().collect(Collectors.toList()));
    }

    /**
     * 下载综合R18每日排行榜图片
     * @param date 20200402这样的日期字符串
     */
    @Override
    public void downloadRankingDailyR18(String date) {
        Set<String> pixivIdSet = PixivPictureUtils.getPixivIdsFromRankingDailyR18(date);

        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_DAILY_R18).append(date);

        /*Set<String> errorPixivIdSet = new HashSet<>(16);
        for (String pixivId : pixivIdSet) {
            try {
                downloadPicturesByPixivId(pixivId, subject.toString());
            } catch(Exception ex) {
                errorPixivIdSet.add(pixivId);
                continue;
            }
        }

        log.info("下载{}的综合R18每日排行榜图片总数量为{}，其中失败的数量为{}", date, pixivIdSet.size(), errorPixivIdSet.size());*/
        saveAndDownloadPictures(subject.toString(), pixivIdSet.stream().collect(Collectors.toList()));
    }

    /**
     * 下载指定日期之前指定天数的所有综合R18每日推荐图片
     * @param endDate yyyyMMdd格式的日期字符串
     * @param dayNums 天数，如果为正，则是endDate之前的天数；如果是负，则是endDate之后的天数，但最多知道今天
     */
    @Override
    public void downloadRankingDailyR18MultiDays(String endDate, Integer dayNums) {
        Set<String> pixivIdSet = new HashSet<>(1024);
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        // 获取所有的日期（字符串格式）
        try {
            Date end = format.parse(endDate);
            for (int i = 0; i < dayNums; ++i) {
                Date iDay = DateUtils.addDays(end, -i);

                if (iDay.after(new Date())) {
                    // 如果是今天之后，则跳过
                    continue;
                }
                String day = DateFormatUtils.format(iDay, "yyyyMMdd");
                dateList.add(day);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        log.info("获取到的从{}往前的{}天的所有日期为：\n{}", endDate, dayNums, JSON.toJSONString(dateList));

        StringBuilder subject = new StringBuilder(PixivConstants.SUBJECT_PREFIX_DAILY_R18_MULTI).append(endDate).append("_").append(dayNums);

        for (String date : dateList) {
            Set<String> datePixivIdSet = PixivPictureUtils.getPixivIdsFromRankingDailyR18(date);
            pixivIdSet.addAll(datePixivIdSet);
        }

        /*Set<String> errorPixivIdSet = new HashSet<>(16);
        for (String pixivId : pixivIdSet) {
            try {
                downloadPicturesByPixivId(pixivId, subject.toString());
            } catch(Exception ex) {
                errorPixivIdSet.add(pixivId);
                continue;
            }
        }

        log.info("下载{}往前的{}天的综合R18每日排行榜图片总数量为{}，其中失败的数量为{}", endDate, dayNums, pixivIdSet.size(), errorPixivIdSet.size());*/
        saveAndDownloadPictures(subject.toString(), pixivIdSet.stream().collect(Collectors.toList()));
        log.info("下载{}往前的{}天的综合R18每日排行榜图片总数量为{}", endDate, dayNums, pixivIdSet.size());
    }

    /**
     * 测试保存图片信息
     * @param pixivId
     */
    @Override
    public void savePicInfoTest(String pixivId) {
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return;
        }
        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();

        PixivPictureInfo pixivPictureInfo = new PixivPictureInfo();
        pixivPictureInfo.setPixivId(pixivId);
        pixivPictureInfo.setAuthorId(detailInfoBO.getUserId());
        pixivPictureInfo.setOriginalUrl(detailInfoBO.getOriginalUrl());
        pixivPictureInfo.setAuthorName(detailInfoBO.getUserName());
        pixivPictureInfo.setTitle(detailInfoBO.getTitle());
        pixivPictureInfo.setDescription(detailInfoBO.getDescription());
        pixivPictureInfo.setMiniUrl(detailInfoBO.getMiniUrl());
        pixivPictureInfo.setThumbUrl(detailInfoBO.getThumbUrl());
        pixivPictureInfo.setSmallUrl(detailInfoBO.getSmallUrl());
        pixivPictureInfo.setRegularUrl(detailInfoBO.getRegularUrl());
        pixivPictureInfo.setPageCount(detailInfoBO.getPageCount());
        pixivPictureInfo.setWidth(detailInfoBO.getWidth());
        pixivPictureInfo.setHeight(detailInfoBO.getHeight());
        pixivPictureInfo.setBookmarkCount(detailInfoBO.getBookmarkCount());
        pixivPictureInfo.setLikeCount(detailInfoBO.getLikeCount());
        pixivPictureInfo.setCommentCount(detailInfoBO.getCommentCount());
        pixivPictureInfo.setResponseCount(detailInfoBO.getResponseCount());
        pixivPictureInfo.setViewCount(detailInfoBO.getViewCount());

        List<PixivPictureInfo> list = new ArrayList<>(16);
        list.add(pixivPictureInfo);

        pixivPictureInfoMapper.batchInsert(list);

    }
}
