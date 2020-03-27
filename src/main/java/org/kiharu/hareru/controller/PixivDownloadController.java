package org.kiharu.hareru.controller;

import lombok.extern.slf4j.Slf4j;
import org.kiharu.hareru.pixiv.DownloadPixivPicture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pixiv")
public class PixivDownloadController {

    @Autowired
    private DownloadPixivPicture downloadPixivPicture;

    /**
     * 根据pixivId下载该图片
     * @param pixivId
     */
    @GetMapping("/simpleDownloadByPixivId")
    public String simpleDownloadByPixivId(@RequestParam("pixivId") String pixivId) {
        downloadPixivPicture.downloadPictureByPixivId(pixivId);
        return "成功";
    }

    /**
     * 根据pixivId下载其所关联推荐的所有图片（一层）
     * @param pixivId
     */
    @GetMapping("/downloadRecommendPictureByPixivId")
    public String downloadRecommendPictureByPixivId(@RequestParam("pixivId") String pixivId) {
        downloadPixivPicture.downloadRecommendPictureByPixivId(pixivId);
        return "SUCCESS";
    }

    /**
     * 根据给定的多个pixivId，下载它们所关联推荐的所有图片（一层）
     * @param pixivIdList
     */
    @GetMapping("/downloadRecommendPicturesByPixivIdList")
    public String downloadRecommendPicturesByPixivIdList(@RequestParam("pixivIdList") List<String> pixivIdList) {
        downloadPixivPicture.downloadRecommendPictureByPixivIdList(pixivIdList);
        return "SUCCESS";
    }

    /**
     * 根据pixivId下载其所关联推荐的所有图片(两层）
     * @param pixivId
     * @return
     */
    @PostMapping("/downloadRecommendPicByPixivIdWithTwoDepth")
    public String downloadRecommendPicByPixivIdWithTwoDepth(@RequestParam("pixivId") String pixivId) {
        downloadPixivPicture.downloadRecommendPicByPixivIdWithTwoDepth(pixivId);
        return "SUCCESS";
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
