package org.kiharu.hareru.demo;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 初始的Pixiv图片下载
 */
public class PixivDownload {

    /**
     * 下载Pixiv图片
     */
    public void downloadDemo() {
        String url = "https://i.pximg.net/img-original/img/2020/03/22/00/52/23/80273312_p0.jpg";

        String fileName = getFileNameFromUrl(url);
        String fileDir = "F:/PixivDemo/";
        String filePath = fileDir  + fileName;

        // 创建文件
        File picFile = new File(filePath);
        FileOutputStream fileOutputStream = null;
        try {
            if (!picFile.exists()) {
                picFile.createNewFile();
            }
            fileOutputStream = new FileOutputStream(picFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Host", "i.pximg.net");
        headersMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        headersMap.put("Accept", "image/webp,*/*");
        headersMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        headersMap.put("Accept-Encoding", "gzip, deflate, br");
        headersMap.put("DNT", "1");
        headersMap.put("Connection", "keep-alive");
        headersMap.put("TE", "Trailers");
        headersMap.put("Referer", "https://www.pixiv.net");
        Headers headers = Headers.of(headersMap);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("失败");
            }
            InputStream respInputStream = response.body().byteStream();
            int b = 0;
            while ((b = respInputStream.read()) != -1) {
                fileOutputStream.write(b);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从图片 url中获取文件名
     * @param url
     * @return
     */
    private String getFileNameFromUrl(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        return fileName;
    }

    public static void main(String[] args) {
        PixivDownload pixivDownload = new PixivDownload();
        pixivDownload.downloadDemo();
    }
}
