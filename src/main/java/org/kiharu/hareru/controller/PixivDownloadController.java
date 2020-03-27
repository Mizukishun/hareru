package org.kiharu.hareru.controller;

import org.kiharu.hareru.pixiv.DownloadPixivPicture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pixiv")
public class PixivDownloadController {

    @Autowired
    private DownloadPixivPicture downloadPixivPicture;

    @GetMapping("/simpleDownloadByPixivId")
    public void simpleDownloadByPixivId(@RequestParam("pixivId") String pixivId) {
        downloadPixivPicture.downloadRecommendPictureByPixivId(pixivId);
    }

    @GetMapping("/test")
    public void test() {
        System.out.println("测试Controller接口");
    }

    @GetMapping("/simpleDownloadByPixivIdList")
    public void simpleDownloadByPixivIdList(@RequestParam("pixivIdList") List<String> pixivIdList) {

    }
}
