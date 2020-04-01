package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.kiharu.hareru.bo.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 专门用于解析Pixiv接口返回结果
 */
@Slf4j
public class PixivResultParser {

    /**
     * 获取artworks接口返回的结果内容
     * https://www.pixiv.net/artworks/80385020
     * @param respHtml
     * @Return 返回<meta id="meta-preload-data">节点中的content内容，其中的内容是JSON格式的，涉及pixivId图片的关键信息
     */
    public static String getArtworksResultContent(String respHtml) {
        if (StringUtils.isEmpty(respHtml)){
            return null;
        }
        Document document = Jsoup.parse(respHtml);
        Element head = document.head();
        Element preloadDataElement = head.getElementById("meta-preload-data");
        String content = preloadDataElement.attr("content");

        return content;
    }

    /**
     * 解析artworks接口返回的content内容中的具体信息
     * content返回结果内容可参见"sample/artworks接口返回结果的preload-data的content内容.json"
     * https://www.pixiv.net/artworks/80385020
     * @param content
     * @return
     */
    public static PixivArtworksInterfaceResultContentBO parseArtworksResult(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        JSONObject jsonContent = JSON.parseObject(content);
        JSONObject illust = jsonContent.getJSONObject("illust");
        JSONObject user = jsonContent.getJSONObject("user");
        // 因为在illust节点里是以pixivId作为key的，所以这里只能以Map的方式进行获取
        Map<String, Object> illustInnerMap = illust.getInnerMap();
        // 因为user节点里是以作者userId作为key的，所以这里只能以Map的方式进行获取
        Map<String, Object> userInnerMap = user.getInnerMap();

        List<PixivArtworksInterfaceResultContentBO> resultContentBOList = new ArrayList<>(4);
        // 这里的Map目前只看过一个元素的情况，如果有多个，可能需要另外处理
        for (Map.Entry<String, Object> entry : illustInnerMap.entrySet()) {
            JSONObject jsonIllust = (JSONObject)entry.getValue();
            if (jsonIllust == null) {
                return null;
            }
            JSONObject urls = jsonIllust.getJSONObject("urls");
            JSONObject tags = jsonIllust.getJSONObject("tags");
            JSONObject userIllusts = jsonIllust.getJSONObject("userIllusts");

            List<String> userIllustIdList = null;
            if (userIllusts != null) {
                Map<String, Object> userIllustsInnerMap = userIllusts.getInnerMap();
                userIllustIdList = userIllustsInnerMap.keySet().stream().collect(Collectors.toList());
            }


            PixivArtworksInterfaceResultContentBO resultContentBO = new PixivArtworksInterfaceResultContentBO();
            PixivPictureDetailInfoBO pictureDetailInfoBO = new PixivPictureDetailInfoBO();

            List<PixivPictureTagBO> tagBOList = new ArrayList<>(16);

            pictureDetailInfoBO.setIllustId(jsonIllust.getString("illustId"));
            pictureDetailInfoBO.setIllustTitle(jsonIllust.getString("illustTitle"));
            pictureDetailInfoBO.setIllustComment(jsonIllust.getString("illustComment"));
            pictureDetailInfoBO.setId(jsonIllust.getString("id"));
            pictureDetailInfoBO.setTitle(jsonIllust.getString("title"));
            pictureDetailInfoBO.setDescription(jsonIllust.getString("description"));
            pictureDetailInfoBO.setIllustType(jsonIllust.getInteger("illustType"));
            pictureDetailInfoBO.setCreateDate(jsonIllust.getDate("createDate"));
            pictureDetailInfoBO.setUploadDate(jsonIllust.getDate("uploadDate"));
            pictureDetailInfoBO.setRestrict(jsonIllust.getString("restrict"));
            pictureDetailInfoBO.setXRestrict(jsonIllust.getString("xRestrict"));
            pictureDetailInfoBO.setSl(jsonIllust.getString("sl"));
            pictureDetailInfoBO.setMiniUrl(urls.getString("mini"));
            pictureDetailInfoBO.setThumbUrl(urls.getString("thumb"));
            pictureDetailInfoBO.setSmallUrl(urls.getString("small"));
            pictureDetailInfoBO.setRegularUrl(urls.getString("regular"));
            pictureDetailInfoBO.setOriginalUrl(urls.getString("original"));
            pictureDetailInfoBO.setUserId(jsonIllust.getString("userId"));
            pictureDetailInfoBO.setUserName(jsonIllust.getString("userName"));
            pictureDetailInfoBO.setUserAccount(jsonIllust.getString("userAccount"));
            pictureDetailInfoBO.setPageCount(jsonIllust.getInteger("pageCount"));
            pictureDetailInfoBO.setWidth(jsonIllust.getInteger("width"));
            pictureDetailInfoBO.setHeight(jsonIllust.getInteger("height"));
            pictureDetailInfoBO.setBookmarkCount(jsonIllust.getInteger("bookmarkCount"));
            pictureDetailInfoBO.setLikeCount(jsonIllust.getInteger("likeCount"));
            pictureDetailInfoBO.setCommentCount(jsonIllust.getInteger("commentCount"));
            pictureDetailInfoBO.setResponseCount(jsonIllust.getInteger("responseCount"));
            pictureDetailInfoBO.setViewCount(jsonIllust.getInteger("viewCount"));
            pictureDetailInfoBO.setUserIllusts(userIllustIdList);

            JSONArray tagsArray = tags.getJSONArray("tags");
            for (int i = 0; i < tagsArray.size(); ++i) {
                JSONObject jsonTag = tagsArray.getJSONObject(i);
                PixivPictureTagBO tagBO = new PixivPictureTagBO();
                tagBO.setTag(jsonTag.getString("tag"));
                tagBO.setLocked(jsonTag.getBoolean("locked"));
                tagBO.setDeletable(jsonTag.getBoolean("deletable"));
                tagBO.setUserId(jsonTag.getString("userId"));
                tagBO.setUserName(jsonTag.getString("userName"));
                // TODO--translation字段是个可能对应多语言的对象，这里暂时不处理，如果以后需要tag的多语言翻译，则再处理它

                tagBOList.add(tagBO);
            }

            resultContentBO.setPictureDetailInfoBO(pictureDetailInfoBO);
            resultContentBO.setTagBOList(tagBOList);

            resultContentBOList.add(resultContentBO);
        }

        if (resultContentBOList.size() != 1) {
            // 这里做个记录吧，万一真有多个的情况呢
            log.error("artworks接口返回的content内部包含不止一个pixivId的数据，content内容为：\n{}", jsonContent);
        }

        PixivArtworksInterfaceResultContentBO result = resultContentBOList.size() == 0 ? null : resultContentBOList.get(0);

        if (result != null) {
            // 作者信息放在这里进行提取
            PixivPictureAuthorInfoBO authorInfoBO = new PixivPictureAuthorInfoBO();

            for (Map.Entry<String, Object> entry : userInnerMap.entrySet()) {
                JSONObject jsonUser = (JSONObject) entry.getValue();

                authorInfoBO.setUserId(jsonUser.getString("userId"));
                authorInfoBO.setUserName(jsonUser.getString("name"));
                authorInfoBO.setImage(jsonUser.getString("image"));
                authorInfoBO.setImageBig(jsonUser.getString("imageBig"));
                authorInfoBO.setPremium(jsonUser.getBoolean("premium"));
                authorInfoBO.setIsFollowed(jsonUser.getBoolean("isFollowed"));
                authorInfoBO.setIsMypixiv(jsonUser.getBoolean("isMypixiv"));
                authorInfoBO.setIsBlocking(jsonUser.getBoolean("isBlocking"));
                authorInfoBO.setBackground(jsonUser.getString("background"));
                authorInfoBO.setPartial(jsonUser.getInteger("partial"));
                // 这里只取第一个，如果会出现两个作者userId的情况，则以后再处理吧
                break;
            }

            result.setAuthorInfoBO(authorInfoBO);
        }


        // TODO--下面这里有点冗余了，看以后是否需要从这里拿到的作者这里获取其所有作品ID吧
        /*if (result.getPictureDetailInfoBO().getUserId().equals(authorInfoBO.getUserId())) {
            authorInfoBO.setUserIllusts(result.getPictureDetailInfoBO().getUserIllusts());
        }*/

        return result;
    }


    /**
     * 从https://www.pixiv.net/ajax/illust/80391469/pages接口返回结果中解析出多张图片的原始大图地址
     * 返回结果内容可参见“sample/ajax-illust-80391469-pages接口返回结果样例.txt”
     * @param respJSONStr 上面接口返回的JSON格式的字符串
     * @return
     */
    public static List<String> getUrlsFromAjaxIllustPageResult(String respJSONStr) {
        List<String> originalUrls = new ArrayList<>(8);
        if (StringUtils.isEmpty(respJSONStr)) {
            return originalUrls;
        }
        JSONObject respJSON = JSON.parseObject(respJSONStr);
        JSONArray body = respJSON.getJSONArray("body");


        for (int i = 0; i < body.size(); ++i) {
            JSONObject element = body.getJSONObject(i);
            JSONObject urls = element.getJSONObject("urls");
            String originalUrl = urls.getString("original");
            originalUrls.add(originalUrl);
            // TODO--这里其实还可以获取原始图片的width、height、thumb_mini、small、regular等信息的，之后看需要再补充
        }

        // TODO--临时测试用，之后删除
        //log.info("解析ajax/illust/{pixivId}/pages接口返回结果的respJSONStr=\n{}\n得到的原始图片地址有：\n{}", respJSONStr, JSON.toJSON(originalUrls));

        return originalUrls;
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


    /**
     * 从/ajax/user/1277076/profile/all接口的返回结果中解析出作者的插画及漫画信息
     * 获取返回结果的接口请求可参见PixivRequestUtils.gerResponseFromAjaxUserProfileAll()方法
     * 返回结果内容可参见"sample/用户所有图片ajax-user-pixivId-profile-all接口返回.json"文件
     * @param respJSONStr
     * @return
     */
    public static PixivAuthorProfileBO getAuthorIllustAndMangaInfo(String respJSONStr) {
        PixivAuthorProfileBO result = new PixivAuthorProfileBO();
        if (StringUtils.isEmpty(respJSONStr)) {
            return result;
        }

        // 解析返回结果
        JSONObject respJSON = JSON.parseObject(respJSONStr);
        JSONObject body = respJSON.getJSONObject("body");
        JSONObject illust = body.getJSONObject("illusts");
        JSONObject manga = body.getJSONObject("manga");
        // TODO--还有其它信息，可参见上面列出的文件，现在暂时不处理，之后如果有需要，可以在这里进行添加

        Set<String> illustIdList = illust.getInnerMap().keySet();
        Set<String> mangaIdList = manga.getInnerMap().keySet();

        result.setIllustIdList(illustIdList);
        result.setMangaIdList(mangaIdList);

        return result;
    }

}
