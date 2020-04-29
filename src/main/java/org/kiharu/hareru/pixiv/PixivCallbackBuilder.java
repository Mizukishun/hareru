package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.kiharu.hareru.bo.PixivAjaxIllustPagesUrlInfoBO;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.entity.PixivAllPicture;
import org.kiharu.hareru.entity.PixivPictureDetailInfo;
import org.kiharu.hareru.enums.PixivPictureRequestStatusEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
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
    public static Callback getArtworksCallback(CopyOnWriteArrayList<PixivPictureDetailInfo> result) {
        // 回调接口，用于处理异步 网络 请求之后返回的信息
        Callback callback = new Callback(){
            @Override
            public void onResponse(Call call, Response response) {
                // TODO--怎么获取之前的pixivId
                String url = response.request().url().toString();
                String pixivId = url.substring(url.lastIndexOf('/') + 1);

                if (!response.isSuccessful()) {
                    // 如果请求不成功，则根据不同情况进行处理
                    Integer code = response.code();
                    String message = response.message();
                    PixivPictureDetailInfo detailInfo = new PixivPictureDetailInfo();
                    detailInfo.setExisted(PixivConstants.FALSE);
                    detailInfo.setPixivId(pixivId);
                    detailInfo.setDownloaded(PixivConstants.FALSE);
                    detailInfo.setRequestStatus(PixivPictureRequestStatusEnum.FAIL.getValue());
                    result.add(detailInfo);
                    log.info("异步请求获取url={}失败，返回code={}, message={}", url, code, message);
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
                        // TODO--注意，这里只获取了pixivId的一张图片，如果它对应多张图片，还需要另外进行请求处理！
                        PixivPictureDetailInfo detailInfo = new PixivPictureDetailInfo();
                        detailInfo = PixivPictureUtils.convert2DetailInfo(detailInfoBO);
                        detailInfo.setDownloaded(PixivConstants.FALSE);
                        detailInfo.setExisted(PixivConstants.TRUE);
                        detailInfo.setRequestStatus(PixivPictureRequestStatusEnum.SUCCESS.getValue());
                        result.add(detailInfo);
                        log.info("异步请求artworks接口获取图片信息成功，url={}", url);

                        bufferedReader.close();
                    } catch (IOException ioe) {
                        // 从网络流中获取数据失败
                        log.error("处理返回artworks接口返回内容失败", ioe);
                        PixivPictureDetailInfo detailInfo = new PixivPictureDetailInfo();
                        detailInfo.setPixivId(pixivId);
                        detailInfo.setDownloaded(PixivConstants.FALSE);
                        detailInfo.setRequestStatus(PixivPictureRequestStatusEnum.RETRY.getValue());
                        result.add(detailInfo);
                    }
                }
                // 关闭response body
                response.body().close();
            }


            @Override
            public void onFailure(Call call, IOException ex) {
                log.error("请求artworks网络接口失败，请检查", ex);
                // TODO--这里还需要具体看下怎么获取到它对应的pixivId值，看是不是在异常中
                PixivPictureDetailInfo detailInfo = new PixivPictureDetailInfo();
                detailInfo.setPixivId(PixivConstants.INVALID_PIXIV_ID);
                result.add(detailInfo);
            }
        };
        return callback;
    }

    /**
     * 获取处理ajax/illust/{pixivId}/pages接口的Callback
     * 也即针对pixivId获取其对应多张图片信息的接口
     * @return
     */
    public static Callback getCallbackFromAjaxIllustPage(CopyOnWriteArrayList<PixivAjaxIllustPagesUrlInfoBO> result) {

        Callback callback = new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                String url = response.request().url().toString();
                if (!response.isSuccessful()) {
                    // 如果请求获取结果失败
                    Integer code = response.code();
                    String message = response.message();
                    log.error("请求获取多张图片地址信息{}接口失败，code={},message={}", url, code, message);
                    PixivAjaxIllustPagesUrlInfoBO emptyBO = new PixivAjaxIllustPagesUrlInfoBO();
                    result.add(emptyBO);
                } else {
                    // 如果请求获取成功，则需要进一步解析出多张图片地址信息
                    try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.body().byteStream(), "UTF-8"))) {
                        String respJSONStr = bufferedReader.lines().collect(Collectors.joining());
                        List<PixivAjaxIllustPagesUrlInfoBO> boList = PixivResultParser.getUrlsInfoFromAjaxIllustPageResult(respJSONStr);
                        result.addAll(boList);

                        log.info("异步请求获取多张图片地址信息ajax/illust/.../pages接口的结果成功，url=", url);
                        bufferedReader.close();
                    } catch (IOException ioe) {
                        PixivAjaxIllustPagesUrlInfoBO emptyBO = new PixivAjaxIllustPagesUrlInfoBO();
                        result.add(emptyBO);
                        StringBuilder msg = new  StringBuilder()
                                .append("获取多张图片url=")
                                .append(url)
                                .append("成功之后，处理返回内容失败");
                        log.error(msg.toString(), ioe);
                    }
                }
                // 关闭response body
                response.body().close();
            }
            @Override
            public void onFailure(Call call, IOException ex) {
                PixivAjaxIllustPagesUrlInfoBO emptyBO = new PixivAjaxIllustPagesUrlInfoBO();
                result.add(emptyBO);
                log.error("请求获取多张图片地址失败，进入onFailure方法", ex);
            }
        };

        return callback;
    }
}
