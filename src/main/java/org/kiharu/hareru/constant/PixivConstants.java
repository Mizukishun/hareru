package org.kiharu.hareru.constant;

import org.kiharu.hareru.util.PixivUtils;

import java.io.File;

public class PixivConstants {

    /**
     * 保存图片的目录
     * TODO--之后需要改成可自动选择拥有最大剩余容量的硬盘
     */
    public static String PICTURE_SAVE_DIR = "F:/PixivDemo/";

    /**
     * 保存下载的Pixiv图片的最顶层文件夹名称
     */
    public static String PICTURE_SAVE_TOP_FOLDER_NAME = "PixivDownload";

    /**
     * 为了获取图片原始大图地址，需要先请求这个地址的信息，后面接pixivId
     */
    public static String PIXIV_ARTWORKS_PATH = "https://www.pixiv.net/artworks/";

    /**
     * 为了从artworks返回的结果中匹配出原始大图地址的正则表达式，得到如下结果
     * https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg
     * TODO--待确认是否所有的原始大图地址都是以"https://i.pximg.net/img-original/"开头的
     */
    public static String PIXIV_REGEX_ARTWORKS_IMG_ORIGINAL = "https://i.pximg.net/img-original/[^\"]*";

    /**
     * 获取推荐的图片pixivId的接口地址前缀和后缀，中间是原始图片pixivId
     * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
     */
    public static String PIXIV_ILLUST_RECOMMEND_PREFIX = "https://www.pixiv.net/ajax/illust/";
    public static String PIXIV_ILLUST_RECOMMEND_SUFFIX = "/recommend/init?limit=18";

    /**
     * 获取图片保存的目录路径（只在项目初始化之后设置一次）
     */
    public static File savedPicFolder = PixivUtils.getSavedPicFolder();

    /**
     * 根据pixivId获取其对应的多张图片原始大图地址的接口地址
     * https://www.pixiv.net/ajax/illust/80391469/pages
     */
    public static String PIXIV_AJAX_ILLUST_PAGES_PREFIX = "https://www.pixiv.net/ajax/illust/";
    public static String PIXIV_AJAX_ILLUST_PAGES_SUFFIX = "/pages";

    /**
     * 根据作者pixivUserId获取该作者所有作品信息的接口地址，中间的1277076是该作者的pixivUserId
     * https://www.pixiv.net/ajax/user/1277076/profile/all
     */
    public static String PIXIV_AJAX_USER_PROFILE_ALL_PREFIX = "https://www.pixiv.net/ajax/user/";
    public static String PIXIV_AJAX_USER_PROFILE_ALL_SUFFIX = "/profile/all";

    /**
     * 综合R18每日排行榜接口地址
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330&p=2&format=json
     */
    public static String PIXIV_RANKING_DAILY_R18_PREFIX = "https://www.pixiv.net/ranking.php?mode=daily_r18&date=";
    public static String PIXIV_RANKING_DAILY_R18_SUFFIX = "&p=2&format=json";

    /**
     * 综合R18每日排行榜返回结果中匹配出涉及图片pixivId的正则表达式，得到类似如下的结果
     * https://i.pximg.net/user-profile/img/2019/03/07/06/06/19/15490371_b5507fac57efd9ff5bc928d89cf33c93_50.png
     */
    public static String PIXIV_REGEX_RANKING_DAILY_R18 = "https://i.pximg.net/c/[^\"]*";

}
