package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.util.PixivUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用于下载Pixiv图片的类
 */
@Slf4j
@Service
public class DownloadPixivPicture {

    /**
     * 下载指定url的图片到本地
     * @param url
     */
    public void downloadPixivPicture(String url) {

        // 首先构建保存图片的文件
        //String filePath = PixivUtils.getCompletedFilePath(url);
        //File savedPicFile = new File(filePath);

        // 构建请求头
        Headers headers = PixivUtils.getHeaders();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                //System.out.println("请求下载Pixiv图片失败，url=" + url);
                log.error("请求下载Pixiv图片失败，url={}", url);
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body().byteStream());

            /*if (!savedPicFile.exists()) {
                // 防止父路径不存在
                if (!savedPicFile.getParentFile().exists()) {
                    savedPicFile.getParentFile().mkdirs();
                }
                savedPicFile.createNewFile();
            }*/
            File savedPicFile = PixivUtils.getSavedPicFile(url);
            FileOutputStream fileOutputStream = new FileOutputStream(savedPicFile);

            // 将网络图片保存到本地文件中
            int b = 0;
            while ((b = bufferedInputStream.read()) != -1) {
                fileOutputStream.write(b);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    /**
     * 根据pixivId下载图片
     * @param pixivId
     */
    public void downloadPictureByPixivId(String pixivId) {
        if (StringUtils.isEmpty(pixivId)) {
            //System.out.println("pixivId不能为空");
            log.error("downloadPictureByPixivId方法的入参pxivId不能为空");
        }

        // 先根据pixivId获取原始大图的地址
        String url = PixivPictureInfoUtil.getUrlFromArtworksByPixivId(pixivId);
        if (StringUtils.isEmpty(url)) {
            //System.out.println("获取原始大图地址出错：pixivId=" + pixivId + "\nurl=url");
            log.error("获取原始大图地址出错：\npixivId={}\nurl={}", pixivId, url);
        }
        // 根据原始大图地址将图片下载到本地
        downloadPixivPicture(url);
    }

    /**
     * 下载所有根据pixivId而推荐的图片（一层）
     * @param pixivId
     */
    public void downloadRecommendPictureByPixivId(String pixivId) {
        List<String> pixivIdList = PixivPictureInfoUtil.getPixivIdsFromAjaxIllustRecommend(pixivId);


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
        Set<String> recommendPixivIdSet = PixivPictureInfoUtil.getRecommendPixivIdSetByPixivIdList(pixivIdList);

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
                ex.printStackTrace();
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
        List<String> initRecommendPixivIdList = PixivPictureInfoUtil.getPixivIdsFromAjaxIllustRecommend(pixivId);

        Set<String> allRecommendPixivIdSet = new HashSet<>(1024);

        // 首先把第一层关联推荐的保存下
        allRecommendPixivIdSet.addAll(initRecommendPixivIdList);

        // 把第二层关联推荐的都保存下
        for (String recommendPixivId : initRecommendPixivIdList) {
            try {
                List<String> secondRecommendPixivIdList = PixivPictureInfoUtil.getPixivIdsFromAjaxIllustRecommend(recommendPixivId);

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
}
