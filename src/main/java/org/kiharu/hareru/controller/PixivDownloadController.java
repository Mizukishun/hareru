package org.kiharu.hareru.controller;

import lombok.extern.slf4j.Slf4j;
import org.kiharu.hareru.pixiv.PixivPictureDownloader;
import org.kiharu.hareru.pixiv.PixivPictureInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/pixiv")
public class PixivDownloadController {

    @Autowired
    private PixivPictureDownloader pixivPictureDownloader;

    /**
     * 根据pixivId下载该图片
     * @param pixivId
     */
    @GetMapping("/simpleDownloadByPixivId")
    public String simpleDownloadByPixivId(@RequestParam("pixivId") String pixivId) {
        pixivPictureDownloader.downloadPictureByPixivId(pixivId);
        return "成功";
    }

    /**
     * 根据pixivId下载其所关联推荐的所有图片（一层）
     * @param pixivId
     */
    @GetMapping("/downloadRecommendPictureByPixivId")
    public String downloadRecommendPictureByPixivId(@RequestParam("pixivId") String pixivId) {
        pixivPictureDownloader.downloadRecommendPictureByPixivId(pixivId);
        return "SUCCESS";
    }

    /**
     * 根据给定的多个pixivId，下载它们所关联推荐的所有图片（一层）
     * @param pixivIdList
     */
    @GetMapping("/downloadRecommendPicturesByPixivIdList")
    public String downloadRecommendPicturesByPixivIdList(@RequestParam("pixivIdList") List<String> pixivIdList) {
        pixivPictureDownloader.downloadRecommendPictureByPixivIdList(pixivIdList);
        return "SUCCESS";
    }

    /**
     * 根据pixivId下载其所关联推荐的所有图片(两层）
     * @param pixivId
     * @return
     */
    @PostMapping("/downloadRecommendPicByPixivIdWithTwoDepth")
    public String downloadRecommendPicByPixivIdWithTwoDepth(@RequestParam("pixivId") String pixivId) {
        pixivPictureDownloader.downloadRecommendPicByPixivIdWithTwoDepth(pixivId);
        return "SUCCESS";
    }

    /**
     * 根据pixivId下载其关联推荐的所有图片的作者的所有作品图片（一层）
     * @param pixivId
     * @return
     */
    @PostMapping("/downloadRecommendPicAndAuthorWorksByPixivId")
    public String downloadRecommendPicAndAuthorWorksByPixivId(@RequestParam("pixivId") String pixivId) {
        pixivPictureDownloader.downloadRecommendPicAndAuthorWorksByPixivId(pixivId);
        return "SUCCESS";
    }

    /**
     * 下载作者的所有插画及漫画作品
     * @param pixivUserId 该作者在P站的ID
     * @return
     */
    @GetMapping("/downloadAuthorWorksByPixivUserId")
    public String downloadAuthorWorksByPixivUserId(@RequestParam("pixivUserId")String pixivUserId) {
        pixivPictureDownloader.downloadAuthorIllustAndManga(pixivUserId);
        return "SUCCESS";
    }

    /**
     * 根据pixivId获取所有关联推荐作品的作者，然后把所有这些作者的插画及漫画下载下来
     * 采用异步下载图片
     * @param pixivId
     * @return
     */
    @GetMapping("/downloadRecommendAuthorWorksByPixivId")
    public String downloadRecommendAuthorWorksByPixivId(@RequestParam("pixivId")String pixivId) {
        Set<String> pixivIds = PixivPictureInfoUtils.getPixivIdsFromRecommendPicAuthorsWorksByPixivId(pixivId);
        log.info("根据pixivId={}获取到的关联推荐图片的所有作者的所有图片数量为：{}", pixivId, pixivIds.size());
        for (String downloadPixivId : pixivIds) {
            pixivPictureDownloader.asyncDownloadPictureByPixivId(downloadPixivId);
        }

        return "SUCCESS";
    }


    /**
     * 测试异步调用，因为异步调用单元测试好像执行完代码项目就结束了，没有后续的异步处理，所以暂时放在这里进行测试
     */
    @GetMapping("/testAsyncDownload")
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
        PixivPictureDownloader pixivPictureDownloader = new PixivPictureDownloader();
        //pixivPictureDownloader.asyncDownloadPixivPicture(url, file);
        pixivPictureDownloader.asyncDownloadPixivPicture(url);

        log.info("SUCCESS");
    }

    /**
     * 测试Controller
     */
    @GetMapping("/test")
    public String test() {
        log.info("测试Controller接口");
        return "SUCCESS";
    }
}
