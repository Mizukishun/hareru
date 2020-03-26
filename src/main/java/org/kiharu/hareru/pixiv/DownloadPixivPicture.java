package org.kiharu.hareru.pixiv;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.kiharu.hareru.constant.PixivConstants;
import org.kiharu.hareru.util.PixivUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * 用于下载Pixiv图片的类
 */
public class DownloadPixivPicture {

    /**
     * 下载指定url的图片到本地
     * @param url
     */
    public void downloadPixivPicture(String url) {

        // 首先构建保存图片的文件
        String filePath = PixivUtils.getCompletedFilePath(url);
        File savedPicFile = new File(filePath);

        // 构建请求头
        Headers headers = PixivUtils.getHeaders();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("请求下载Pixiv图片失败，url=" + url);
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body().byteStream());

            if (!savedPicFile.exists()) {
                // 防止父路径不存在
                if (!savedPicFile.getParentFile().exists()) {
                    savedPicFile.getParentFile().mkdirs();
                }
                savedPicFile.createNewFile();
            }
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
     * 根据pixivId从artworks中获取的内容中提取出图片的原始大图地址
     * https://www.pixiv.net/artworks/76211609
     * @param pixivId
     * @return https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg
     */
    public String getUrlFromArtworksByPixivId(String pixivId) {

        String url = PixivConstants.PIXIV_ARTWORKS_PATH + pixivId;

        Headers headers = PixivUtils.getArtworksHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .build();

        String result = "";
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("请求获取图片原始大图地址时出错，url=" + url);
            }
            InputStream inputStream = response.body().byteStream();
            // 注意这里是压缩了的，所以这里通过GZIPInputStream进行解压转换
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respHtml = bufferedReader.lines().collect(Collectors.joining());

            // 匹配https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg这样的原图
            String regex = PixivConstants.PIXIV_ARTWORKS_IMG_ORIGINAL_REGEX;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(respHtml);


            if (matcher.find()) {
                result = matcher.group();
                //result = pixivPicOriginalAddress.replace("\"", "");
            }

            StringBuilder sb = new StringBuilder()
                    .append("请求获取图片原始大图信息的返回结果为：\n")
                    .append(result);
            System.out.println(sb.toString());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 根据pixivId下载图片
     * @param pixivId
     */
    public void downloadPictureByPixivId(String pixivId) {
        if (StringUtils.isEmpty(pixivId)) {
            System.out.println("pixivId不能为空");
        }

        // 先根据pixivId获取原始大图的地址
        String url = getUrlFromArtworksByPixivId(pixivId);
        if (StringUtils.isEmpty(url)) {
            System.out.println("获取原始大图地址出错：pixivId=" + pixivId + "\nurl=url");
        }
        // 根据原始大图地址将图片下载到本地
        downloadPixivPicture(url);
    }

    /**
     * 根据pixivId获取其关联推荐的所有其他图片pixivId
     * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
     * @param pixivId
     * @return
     */
    public List<String> getPixivIdsFromAjaxIllustRecommend(String pixivId) {
        List<String> pixivIdList = new ArrayList<>();

        StringBuilder urlSB = new StringBuilder()
                .append(PixivConstants.PIXIV_ILLUST_RECOMMEND_PREFIX)
                .append(pixivId)
                .append(PixivConstants.PIXIV_ILLUST_RECOMMEND_SUFFIX);

        Headers headers = PixivUtils.getArtworksHeaders();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .headers(headers)
                .url(urlSB.toString())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("请求推荐图片pixivIds失败,url=" + urlSB.toString());
            }
            GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            String respJSONStr = bufferedReader.lines().collect(Collectors.joining());

            //System.out.println("请求推荐图片pixivIds的返回结果为:\n" + respJSONStr);

            // 从返回的结果中解析出其推荐的所有图片pixivIds
            pixivIdList = PixivUtils.getPixivIdsFromRespStr(respJSONStr);

            pixivIdList.forEach(recommendPixivId -> {
                downloadPictureByPixivId(recommendPixivId);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return pixivIdList;
    }
}
