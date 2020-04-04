package org.kiharu.hareru.service;

import java.io.File;
import java.util.List;

/**
 * Pixiv下载接口
 */
public interface PixivDownloadService {

    /**
     * 下载指定url的图片到本地
     * @param url
     */
    //void downloadPixivPicture(String url);

    /**
     * 尝试采用异步的方式下载图片，看下载速度是否更好点
     * @param url 下载图片的地址
     */
    //void asyncDownloadPixivPicture(String url);

    /**
     * 异步下载图片，同时由上层指定本地保存的文件
     * @param url 图片下载地址
     * @param file 需由外层保证此文件已创建了
     */
    void asyncDownloadPixivPicture(String url, File file);

    /**
     * 根据pixivId下载其对应的所有图片
     * @param pixivId
     */
    //void downloadPictureByPixivId(String pixivId);

    /**
     * 根据pixivId下载其对应的所有图片，这里使用异步下载图片
     * @param pixivId
     */
    //void asyncDownloadPictureByPixivId(String pixivId);

    /**
     * 根据pixivId下载其对应的可能的多张图片
     * TODO--需要测试下如果pixivId本来就只对应一张图片，下面这里能否请求获取到该唯一一张图片的原始大图地址
     * @param pixivId
     */
    //void asyncDownloadMultiPicturesByPixivId(String pixivId);

    /**
     * 下载pixivId对应的所有图片，可能只有一张，也可能有多张
     * 综合了上面downloadPictureByPixivId和downloadMultiPicturesByPixivId方法的
     * @param pixivId
     */
    void downloadPicturesByPixivId(String pixivId, String subject);

    /**
     * 下载所有根据pixivId而推荐的图片（一层）
     * @param pixivId
     */
    void downloadRecommendPictureByPixivId(String pixivId);

    /**
     * 下载所有这些多个pixivId所关联推荐的图片
     * @param pixivIdList
     */
    void downloadRecommendPictureByPixivIdList(List<String> pixivIdList);

    /**
     * 根据pixivId下载其所关联推荐的所有图片，同时还要把所有关联推荐的图片它们所又关联推荐的图片也下载下来
     * @param pixivId
     */
    void downloadRecommendPicByPixivIdWithTwoDepth(String pixivId);

    /**
     * 根据pixivId下载其关联推荐的图片，以及所有推荐图片的作者的所有作品
     * @param pixivId
     */
    void downloadRecommendPicAndAuthorWorksByPixivId(String pixivId);

    /**
     * 下载作者的插画及漫画
     * 存放在以作者pixivUserId的文件夹中
     * @param pixivUserId
     */
    void downloadAuthorIllustAndManga(String pixivUserId);

    /**
     * 下载综合R18每日排行榜图片
     * @param date 20200402这样的日期字符串
     */
    void downloadRankingDailyR18(String date);

    /**
     * 下载指定日期之前指定天数的所有综合R18每日推荐图片
     * @param endDate yyyyMMdd格式的日期字符串
     * @param dayNums 天数，如果为正，则是endDate之前的天数；如果是负，则是endDate之后的天数，但最多知道今天
     */
    void downloadRankingDailyR18MultiDays(String endDate, Integer dayNums);
}
