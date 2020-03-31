package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.util.PixivHeadersUtils;
import org.kiharu.hareru.util.PixivUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用于下载Pixiv图片的类
 */
@Slf4j
@Service
public class PixivPictureDownloader {

    /**
     * 下载指定url的图片到本地
     * @param url
     */
    public void downloadPixivPicture(String url) {
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
            /*byte[] bytes = new byte[2048];
            int length = 0;
            int off = 0;
            while((length = bufferedInputStream.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes);
            }*/

            // TODO--测试用，之后删除
            stage4 = System.currentTimeMillis();

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException ex) {
            StringBuilder errorMsg = new StringBuilder()
                    .append("下载图片数据出错，url=")
                    .append(url);
            log.error(errorMsg.toString(), ex);
        }
        // TODO--测试用，之后删除
        stage5 = System.currentTimeMillis();

        log.info("下载图片url={}所用的各个阶段时间为：stage1={}ms, stage2={}ms, stage3={}ms, stage4={}ms, stage5={}ms，总时间total={}ms", url, stage1 - begin, stage2 - stage1, stage3 - stage2, stage4 - stage3, stage5 - stage4, stage5 - begin);
    }



    /**
     * 根据pixivId下载其对应的所有图片
     * @param pixivId
     */
    public void downloadPictureByPixivId(String pixivId) {
        if (StringUtils.isEmpty(pixivId)) {
            log.error("downloadPictureByPixivId方法的入参pxivId不能为空");
        }
        /*
        // 先根据pixivId获取原始大图的地址
        String url = PixivPictureInfoUtils.getUrlFromArtworksByPixivId(pixivId);
        if (StringUtils.isEmpty(url)) {
            //System.out.println("获取原始大图地址出错：pixivId=" + pixivId + "\nurl=url");
            log.error("获取原始大图地址出错：\npixivId={}\nurl={}", pixivId, url);
        }
        // 根据原始大图地址将图片下载到本地
        downloadPixivPicture(url);*/

        List<String> urls = PixivPictureInfoUtils.getUrlsFromArtworksByPixivId(pixivId);
        if (CollectionUtils.isEmpty(urls)) {
            log.error("获取pixivId={}对应的所有原始大图地址出错：", pixivId);
        }
        for (String url : urls) {
            try {
                downloadPixivPicture(url);
            } catch (Exception ex) {
                // 下载pixivId对应的所有图片之一出错时，记录日志，跳过--TODO--之后再补充针对出错的处理
                StringBuilder errorMsg = new StringBuilder().append("下载图片出错，图片地址url=").append(url);
                log.error(errorMsg.toString(), ex);
                continue;
            }
        }
    }

    /**
     * 根据pixivId下载其对应的可能的多张图片
     * TODO--需要测试下如果pixivId本来就只对应一张图片，下面这里能否请求获取到该唯一一张图片的原始大图地址
     * @param pixivId
     */
    public void downloadMultiPicturesByPixivId(String pixivId) {
        List<String> urls = PixivPictureInfoUtils.getUrlsFromAjaxIllustPagesByPixivId(pixivId);

        for (String url : urls) {
            try {
                downloadPixivPicture(url);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 下载pixivId对应的所有图片，可能只有一张，也可能有多张
     * 综合了上面downloadPictureByPixivId和downloadMultiPicturesByPixivId方法的
     * @param pixivId
     */
    public void downloadAllPicturesByPixivId(String pixivId) {
        String respHtml = PixivRequestUtils.getRespHtmlFromArtworksInterface(pixivId).orElse("");
        String content = PixivResultParser.getArtworksResultContent(respHtml);
        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
            return;
        }
        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();
        Integer pageCount = detailInfoBO.getPageCount();
        if (pageCount.equals(1)) {
            // TODO--需要测试看能否不用这个方法，也即如果下面的downloadMultiPicturesByPixivId也能下载只有一张图片的话，这里就不需要了
            downloadPictureByPixivId(pixivId);
        } else {
            downloadMultiPicturesByPixivId(pixivId);
        }
    }


    /**
     * 下载所有根据pixivId而推荐的图片（一层）
     * @param pixivId
     */
    public void downloadRecommendPictureByPixivId(String pixivId) {
        List<String> pixivIdList = PixivPictureInfoUtils.getPixivIdsFromAjaxIllustRecommend(pixivId);

        pixivIdList.forEach(recommendPixivId -> {
            downloadPictureByPixivId(recommendPixivId);
        });
    }

    /**
     * 下载所有这些多个pixivId所关联推荐的图片
     * @param pixivIdList
     */
    public void downloadRecommendPictureByPixivIdList(List<String> pixivIdList) {
        // 先获取由这些pixivId所关联推荐的图片pixivId，这里通过Set保证没有重复的，
        Set<String> recommendPixivIdSet = PixivPictureInfoUtils.getRecommendPixivIdSetByPixivIdList(pixivIdList);

        if (CollectionUtils.isEmpty(recommendPixivIdSet)) {
            log.warn("获取pixivIdList所关联推荐的所有图片的recommendPixivIdSet为空，其中pixivIdList为：\n{}", JSONObject.toJSONString(pixivIdList));
            return;
        }

        // 下载时发生错误的图片pixivId
        Set<String> errorPixivIdSet = new HashSet<>();

        // 循环下载所有图片
        for (String pixivId : recommendPixivIdSet) {
            try {
                downloadPictureByPixivId(pixivId);
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
        }

        log.info("根据pixivIdList下载所关联推荐的所有图片的总数为：{},下载失败的图片数为：{}", recommendPixivIdSet.size(), errorPixivIdSet.size());

        // TODO--针对上面出错的再次尝试处理吗？

        return;
    }

    /**
     * 根据pixivId下载其所关联推荐的所有图片，同时还要把所有关联推荐的图片它们所又关联推荐的图片也下载下来
     * @param pixivId
     */
    public void downloadRecommendPicByPixivIdWithTwoDepth(String pixivId) {
        List<String> initRecommendPixivIdList = PixivPictureInfoUtils.getPixivIdsFromAjaxIllustRecommend(pixivId);

        Set<String> allRecommendPixivIdSet = new HashSet<>(1024);

        // 首先把第一层关联推荐的保存下
        allRecommendPixivIdSet.addAll(initRecommendPixivIdList);

        // 把第二层关联推荐的都保存下
        for (String recommendPixivId : initRecommendPixivIdList) {
            try {
                List<String> secondRecommendPixivIdList = PixivPictureInfoUtils.getPixivIdsFromAjaxIllustRecommend(recommendPixivId);

                allRecommendPixivIdSet.addAll(secondRecommendPixivIdList);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        log.info("下载关联推荐图片(两层)的图片总数为：{}", allRecommendPixivIdSet.size());

        // 下载错误的图片pixivId
        Set<String> errorPixivSet = new HashSet<>(16);
        // 下载所有关联推荐的图片
        for (String pid : allRecommendPixivIdSet) {
            try {
                downloadPictureByPixivId(pid);
            } catch (Exception ex) {
                ex.printStackTrace();
                errorPixivSet.add(pid);
                continue;
            }
        }

        log.info("下载关联推荐图片(两层)的图片总数为：{}，其中下载错误的数量有：{}", allRecommendPixivIdSet.size(), errorPixivSet.size());
    }

    /**
     * 根据pixivId下载其关联推荐的图片，以及所有推荐图片的作者的所有作品
     * @param pixivId
     */
    public void downloadRecommendPicAndAuthorWorksByPixivId(String pixivId) {
        // 获取所有推荐图片作者的所有作品图片pixiId
        Set<String> pixivIds = PixivPictureInfoUtils.getPixivIdsFromRecommendPicAuthorsWorksByPixivId(pixivId);

        log.info("请求获取到的所有图片pixivId数量为：{}", pixivIds.size());

        List<String> errorPixivIds = new ArrayList<>();

        for (String tempPixivId : pixivIds) {
            try {
                downloadPictureByPixivId(tempPixivId);
            } catch (Exception ex) {
                ex.printStackTrace();
                errorPixivIds.add(tempPixivId);
                continue;
            }
        }
        log.info("请求下载的图片数量为：{}\n下载出错的图片数量为：{}\n下载成功的图片数量：{}",pixivIds.size(), errorPixivIds.size(), pixivIds.size() - errorPixivIds.size());
    }
}
