package org.kiharu.hareru.pixiv;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kiharu.hareru.service.impl.PixivDownloadServiceImpl;

@Slf4j
public class PixivDownloadServiceImplTest {

    /*@Test
    public void testDownloadPixivPicture() {
        String url = "https://i.pximg.net/img-original/img/2020/03/29/07/31/42/80420349_p2.jpg";
        PixivDownloadServiceImpl pixivDownloadServiceImpl = new PixivDownloadServiceImpl();
        long begin = System.currentTimeMillis();
        pixivDownloadServiceImpl.downloadPixivPicture(url);
        long end = System.currentTimeMillis();
        log.info("下载一张图片所用的时间为：{}秒", (end - begin) / 1000);
    }*/


    @Test
    public void testDownloadPictureByPixivId() {
        String pixivId = "80420349";
        PixivDownloadServiceImpl pixivDownloadServiceImpl = new PixivDownloadServiceImpl();
        pixivDownloadServiceImpl.downloadPictureByPixivId(pixivId);
    }
    @Test
    public void testAsyncDownloadPictureByPixivId() {
        String pixivId = "80420349";
        PixivDownloadServiceImpl pixivDownloadServiceImpl = new PixivDownloadServiceImpl();
        pixivDownloadServiceImpl.asyncDownloadPictureByPixivId(pixivId);

        log.info("SUCCESS");
        // 用下面这个死循环来让这个单元测试能够进行！
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("醒了");
        }
    }


    @Test
    public void testGetPixivIdsFromAjaxIllustRecommend() {
        String pixivId = "79759981";
        PixivDownloadServiceImpl pixivDownloadServiceImpl = new PixivDownloadServiceImpl();
        pixivDownloadServiceImpl.downloadRecommendPictureByPixivId(pixivId);
    }

    /**
     * 测试异步下载，但由于异步下载时，当执行完所有代码之后，单元测试也停掉了，所以后续的异步写入没法进行，导致这里是没法成功执行的
     * 需要在整个项目启动之后才能进行异步下载的测试，可以参见/pixiv/testAsyncDownload接口
     * -- 加入后面的死循环后能够正常执行异步操作了
     */
    @Test
    public void testAsyncDownloadPixivPicture() {
        String url = "https://i.pximg.net/img-original/img/2020/03/29/07/31/42/80420349_p2.jpg";
        /*File file = new File("L:/PixivDownload/2020040110/unitTest.jpg");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        PixivDownloadServiceImpl pixivDownloadServiceImpl = new PixivDownloadServiceImpl();
        pixivDownloadServiceImpl.asyncDownloadPixivPicture(url);

        log.info("SUCCESS");
        // 用下面这个死循环来让这个单元测试能够进行！
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("醒了");
        }
    }

    @Test
    public void testDownloadAuthorIllustAndManga() {
        String pixivUserId = "7038833";
        PixivDownloadServiceImpl downloader = new PixivDownloadServiceImpl();
        downloader.downloadAuthorIllustAndManga(pixivUserId);
        log.info("SUCCESS");

        // 用下面这个死循环来让这个单元测试能够进行！
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("醒了");
        }
    }

    @Test
    public void testdownloadRankingDailyR18MultiDays() {
        String endDate = "20200305";
        Integer dayNums = 11;
        PixivDownloadServiceImpl downloader = new PixivDownloadServiceImpl();
        downloader.downloadRankingDailyR18MultiDays(endDate, dayNums);
    }
}
