package org.kiharu.hareru.pixiv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.kiharu.hareru.bo.PixivArtworksInterfaceResultContentBO;
import org.kiharu.hareru.bo.PixivPictureAuthorInfoBO;
import org.kiharu.hareru.bo.PixivPictureDetailInfoBO;
import org.kiharu.hareru.bo.PixivPictureTagBO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Pixiv接口返回结果的解析处理类
 */
@Slf4j
@Service
public class PixivInterfaceResultParser {

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
                int size = userIllustsInnerMap.size();
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

        PixivArtworksInterfaceResultContentBO result = resultContentBOList.size() == 0 ? new PixivArtworksInterfaceResultContentBO() : resultContentBOList.get(0);

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

        // TODO--下面这里有点冗余了，看以后是否需要从这里拿到的作者这里获取其所有作品ID吧
        /*if (result.getPictureDetailInfoBO().getUserId().equals(authorInfoBO.getUserId())) {
            authorInfoBO.setUserIllusts(result.getPictureDetailInfoBO().getUserIllusts());
        }*/

        result.setAuthorInfoBO(authorInfoBO);

        return result;
    }

}
