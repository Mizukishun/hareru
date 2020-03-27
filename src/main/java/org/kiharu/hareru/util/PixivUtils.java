package org.kiharu.hareru.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.kiharu.hareru.bo.PixivPictureUrlInfoBO;
import org.kiharu.hareru.constant.PixivConstants;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 用于处理Pixiv图片的相关工具类
 */
@Slf4j
public class PixivUtils {

    /**
     * 获取下载图片时所需的请求头信息Headers
     * @return
     */
    public static Headers getHeaders() {
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

        return headers;
    }

    /**
     * 获取下载artworks/pixivId是所需的请求头信息Heaaders
     * https://www.pixiv.net/artworks/76211609
     * @return
     */
    public static Headers getArtworksHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put("Host", "www.pixiv.net");
        map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        map.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        map.put("Accept-Encoding", "gzip, deflate, br");
        map.put("DNT", "1");
        map.put("Upgrade-Insecure-Requests", "1");
        map.put("Connection", "keep-alive");
        map.put("Cookie", "PHPSESSID=22834429_W44oSwMbDasKma4DsUWd1JLCcpcA7NPk");

        Headers headers = Headers.of(map);
        return headers;
    }

    /**
     * 保存图片的完整路径名，指定到具体文件
     * 获取到的文件路径名格式为：
     * D:/pixivDemo/{date}/{pixivId}
     * TODO--总感觉目前这样不大灵活，看情况之后修改吧！
     * @param url
     * @return
     */
    public static String getCompletedFilePath(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (!url.contains("/")) {
            return null;
        }

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String todayStr = format.format(today);

        int endIndex = url.length();
        if (url.contains("_p0")) {
            // 如果图片路径最后包含_p0，则去掉这_p0，只保留pixiv的图片ID
            endIndex = url.lastIndexOf("_p0");
        }
        String pixivId = url.substring(url.lastIndexOf("/") + 1, endIndex);
        // 图片文件后缀名，也即图片类型
        String picSuffix = url.substring(url.lastIndexOf("."), url.length());

        StringBuilder result = new StringBuilder()
                .append(PixivConstants.PICTURE_SAVE_DIR)
                .append(todayStr)
                .append("/")
                .append(pixivId)
                .append(picSuffix);

        return result.toString();
    }

    /**
     * 解析pixiv原始图片的url，获取对应的信息
     * https://i.pximg.net/img-original/img/2020/03/22/00/52/23/80273312_p0.jpg
     * @param url
     * @return
     */
    public static PixivPictureUrlInfoBO getPixivPictureUrlInfoBO(String url) {

        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (!url.contains("/")) {
            return null;
        }
        if (!url.startsWith("https://")) {
            return null;
        }

        String[] splitUrl = url.split("/");

        String pixivPicName = splitUrl[11];
        // 获取图片类型后缀
        int length = pixivPicName.length();
        String suffix = pixivPicName.substring(pixivPicName.lastIndexOf("."), length);

        // 获取pixivId
        int endIndex;
        if (pixivPicName.contains("_p0")) {
            endIndex = pixivPicName.lastIndexOf("_p0");
        } else {
            endIndex = pixivPicName.lastIndexOf(".");
        }
        String pixivId = pixivPicName.substring(0, endIndex);

        if (splitUrl.length != 12) {
            log.error("解析原始图片url={}格式发现不是标准格式，请检查", url);
            return null;
        }

        // 原始图片域名
        String host = splitUrl[3];

        // 图片日期，组装成yyyyMMdd格式
        StringBuilder date = new StringBuilder().append(splitUrl[5]).append(splitUrl[6]).append(splitUrl[7]);
        // 图片时间，组装成hhmmss格式
        StringBuilder time = new StringBuilder().append(splitUrl[8]).append(splitUrl[9]).append(splitUrl[10]);

        PixivPictureUrlInfoBO bo = new PixivPictureUrlInfoBO();
        bo.setUrl(url);
        bo.setPixivId(pixivId);
        bo.setSuffix(suffix);
        bo.setDate(date.toString());
        bo.setTime(time.toString());

        return bo;
    }

    /**
     * 获取保存图片的文件夹
     * @return
     */
    public static File getSavedPicFolder() {
        // 获取拥有最大剩余可用空间的磁盘
        File maxUsableSpaceDisk = HareruUtils.getMaxUsableSpaceDiskFile();


        // 将路径中的反斜杠\替换成正斜杠/
        StringBuilder filePathSB = new StringBuilder()
                .append(maxUsableSpaceDisk.getPath().replace("\\", "/"))
                .append(PixivConstants.PICTURE_SAVE_TOP_FOLDER_NAME).append("/");

        // 创建保存Pixiv图片的最顶层文件夹
        File topFolder = new File(filePathSB.toString());
        if (!topFolder.exists()) {
            topFolder.mkdirs();
        }

        // 获取yyyyMMdd格式的当前日期
        String currentDate = DateFormatUtils.format(new Date(), "yyyyMMdd");

        File savedPicFolder;

        if (Arrays.stream(topFolder.listFiles()).noneMatch(innerFile -> innerFile.getName().equals(currentDate))) {
            // 如果顶层文件夹中没有跟当前日期同名的文件夹，则创建一个以当前日期为名的文件夹
            filePathSB.append(currentDate).append("/");
        } else {
            // 如果就是已存在当前日期名称的文件夹，则新建一个文件夹

            // 如果最顶层文件夹中已经有跟当前日期同名的文件夹了，则获取跟当前日期同名类似中最大的那个文件夹名称
            File maxFileNameFile = Arrays.stream(topFolder.listFiles())
                    .filter(innerFile -> innerFile.isDirectory())
                    .filter(innerFile -> innerFile.getName().contains(currentDate))
                    .max((item1, item2) -> item1.getName().compareTo(item2.getName()))
                    .get();
            String maxFileName = maxFileNameFile.getName();

            // 跟当前日期同名类似的最大的文件夹名称，要么在后面拼装个1，要么对其文件夹名称加1
            if (maxFileName.length() == 8) {
                // 如果就是已存在当前日期名称的文件夹，则新建的文件夹名称是在当前日期的后面添加个1
                filePathSB.append(currentDate).append("1").append("/");
            } else {
                // 如果已存在的多个跟当前日期类似名称的文件夹，取到最大的那个，在它的基础上以算数加1的方式进行新文件夹的命名
                Integer maxFileNameInteger = Integer.valueOf(maxFileName);
                String modifiedCurrentDate = String.valueOf(maxFileNameInteger + 1);
                filePathSB.append(modifiedCurrentDate).append("/");
            }
        }

        savedPicFolder = new File(filePathSB.toString());
        savedPicFolder.mkdir();

        return savedPicFolder;
    }

    /**
     * 获取保存图片的文件，包含完整的路径名及文件
     * @param url
     * @return
     */
    public static File getSavedPicFile(String url) {
        PixivPictureUrlInfoBO urlInfoBO = getPixivPictureUrlInfoBO(url);
        if (urlInfoBO == null) {
            return null;
        }

        //File savedPicFolder = getSavedPicFolder();
        File savedPicFolder = PixivConstants.savedPicFolder;

        StringBuilder picFilePath = new StringBuilder()
                .append(savedPicFolder.getAbsolutePath())
                .append("/")
                .append(urlInfoBO.getPixivId())
                .append(urlInfoBO.getSuffix());

        File savedPicFile = new File(picFilePath.toString());

        // 重复图片文件怎么处理？--TODO--需要具体测试看下会怎么保存吧
        if (savedPicFile.exists()) {
            // 如果之前已有同名图片，则新建一个以{pixivId_timestamp}为名的图片
            StringBuilder newPicFilePath = new StringBuilder()
                    .append(savedPicFolder.getAbsolutePath())
                    .append("/").append(urlInfoBO.getPixivId())
                    .append("_").append(System.currentTimeMillis())
                    .append(urlInfoBO.getSuffix());
            savedPicFile = new File(newPicFilePath.toString());
        }

        try {
            savedPicFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savedPicFile;

    }

    /**
     * 从Pixiv的关联图片推荐接口返回的结果字符串中解析出其中所有推荐的图片pixivId
     * https://www.pixiv.net/ajax/illust/79759981/recommend/init?limit=18
     * @param respJSONStr
     * @return
     */
    public static List<String> getPixivIdsFromRespStr(String respJSONStr) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isEmpty(respJSONStr)) {
            return result;
        }


        // 解析返回结果
        JSONObject resp = JSON.parseObject(respJSONStr);
        JSONObject body = resp.getJSONObject("body");
        JSONArray illusts = body.getJSONArray("illusts");
        JSONArray nextIds = body.getJSONArray("nextIds");

        List<String> nextIdList = new ArrayList<>();
        for (int i = 0; i < nextIds.size(); ++i) {
            String nextId = nextIds.getString(i);
            nextIdList.add(nextId);
        }

        List<String> illustsIdList = new ArrayList<>();
        for (int j = 0; j < illusts.size(); ++j) {
            JSONObject illustObject = illusts.getJSONObject(j);
            String id = illustObject.getString("id");
            illustsIdList.add(id);
        }

        result.addAll(illustsIdList);
        result.addAll(nextIdList);

        StringBuilder resultInfo = new StringBuilder()
                .append("关联图片推荐接口返回的图片pixivIds总共有:").append(result.size()).append("个\n")
                .append("其中nextIds有：").append(nextIds.size()).append("个\n")
                .append("其中illustIds有：").append(illustsIdList.size()).append("个\n")
                .append("所有的pixivIds如下：\n")
                .append(JSONObject.toJSONString(result));

        log.info(resultInfo.toString());

        return result;
    }
}
