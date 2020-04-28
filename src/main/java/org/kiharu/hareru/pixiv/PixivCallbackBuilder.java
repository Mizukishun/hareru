package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.entity.PixivAllPicture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 异步请求的回调函数Callback生成器
 * @Author kiharu
 * @Date 2020-04-28
 */
@Slf4j
public class PixivCallbackBuilder {

    /**
     * 获取针对artworks接口的回调函数
     * @return
     */
    public static Callback getArtworksCallback(CopyOnWriteArrayList<PixivAllPicture> result) {
        // 回调接口，用于处理异步 网络 请求之后返回的信息
        Callback callback = new Callback(){
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    // 如果请求不成功，则根据不同情况进行处理
                    Integer code = response.code();
                    String message = response.message();
                    PixivAllPicture pixivAllPicture = new PixivAllPicture();
                    pixivAllPicture.setExisted(0);
                    // TODO--怎么获取之前的pixivId
                    String url = response.request().url().toString();
                    String pixivId = url.substring(url.lastIndexOf("/") + 1);
                    pixivAllPicture.setPixivId(pixivId);
                    result.add(pixivAllPicture);
                    log.info("请求获取url={}失败，返回code={}, message={}", url, code, message);
                } else {
                    // 如果请求成功
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream(), "UTF-8"))) {
                        String respHtml = bufferedReader.lines().collect(Collectors.joining());
                        // 解析返回内容，获取pixivId对应图片的相关信息
                        String content = PixivResultParser.getArtworksResultContent(respHtml);
                        PixivArtworksInterfaceResultContentBO resultContentBO = PixivResultParser.parseArtworksResult(content);
                        if (resultContentBO == null || resultContentBO.getPictureDetailInfoBO() == null) {
                            return;
                        }
                        PixivPictureDetailInfoBO detailInfoBO = resultContentBO.getPictureDetailInfoBO();
                        // TODO--之后这里是获取图片详细信息的唯一地方，看是否需要将详细的图片信息在这里就进行获取保存
                        PixivAllPicture pixivAllPicture = new PixivAllPicture();
                        pixivAllPicture.setPixivId(detailInfoBO.getId());
                        pixivAllPicture.setAuthorId(detailInfoBO.getUserId());
                        Date uploadDate = detailInfoBO.getUploadDate();
                        if (uploadDate != null) {
                            String uploadDateStr = DateFormatUtils.format(uploadDate, "yyyyMMdd");
                            String uploadTimeStr = DateFormatUtils.format(uploadDate, "HHmmss");
                            pixivAllPicture.setUploadDate(uploadDateStr);
                            pixivAllPicture.setUploadTime(uploadTimeStr);
                        }
                        String originalUrl = detailInfoBO.getOriginalUrl();
                        pixivAllPicture.setOriginalUrl(originalUrl);
                        if (StringUtils.isNotBlank(originalUrl)) {
                            String picSuffix = originalUrl.substring(originalUrl.lastIndexOf(".") + 1);
                            pixivAllPicture.setPicSuffix(picSuffix);
                        }
                        pixivAllPicture.setDownloaded(0);
                        pixivAllPicture.setExisted(1);
                        result.add(pixivAllPicture);
                        log.info("添加图片信息成功");
                    } catch (IOException ioe) {
                        // 从网络流中获取数据失败
                        log.error("处理返回artworks接口返回内容失败", ioe);
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException ex) {
                log.error("请求artworks网络接口失败", ex);
                PixivAllPicture pixivAllPicture = new PixivAllPicture();
                pixivAllPicture.setPixivId("-1");
                pixivAllPicture.setExisted(0);
                result.add(pixivAllPicture);
            }
        };
        return callback;
    }
}
