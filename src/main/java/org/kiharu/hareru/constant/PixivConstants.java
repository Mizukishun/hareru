package org.kiharu.hareru.constant;

import org.kiharu.hareru.util.PixivUtils;

import java.io.File;

public class PixivConstants {

    /**
     * 保存图片的目录
     * TODO--之后需要改成可自动选择拥有最大剩余容量的硬盘
     */
    public static final String PICTURE_SAVE_DIR = "F:/PixivDemo/";

    /**
     * 保存下载的Pixiv图片的最顶层文件夹名称
     */
    public static final String PICTURE_SAVE_TOP_FOLDER_NAME = "PixivDownload";

    /**
     * 默认的子文件夹的名称
     */
    public static final String DEFAULT_SUBJECT_FOR_FOLDER = "pixiv";

    /**
     * 为了获取图片原始大图地址，需要先请求这个地址的信息，后面接pixivId
     */
    public static final String PIXIV_ARTWORKS_PATH = "https://www.pixiv.net/artworks/";

    /**
     * 为了从artworks返回的结果中匹配出原始大图地址的正则表达式，得到如下结果
     * https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg
     * TODO--待确认是否所有的原始大图地址都是以"https://i.pximg.net/img-original/"开头的
     */
    public static final String PIXIV_REGEX_ARTWORKS_IMG_ORIGINAL = "https://i.pximg.net/img-original/[^\"]*";

    /**
     * 获取推荐的图片pixivId的接口地址前缀和后缀，中间是原始图片pixivId
     * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
     */
    public static final String PIXIV_ILLUST_RECOMMEND_PREFIX = "https://www.pixiv.net/ajax/illust/";
    public static final String PIXIV_ILLUST_RECOMMEND_SUFFIX = "/recommend/init?limit=18";

    /**
     * 获取图片本地保存的顶级目录路径（只在项目初始化之后设置一次）
     */
    public static final File SAVED_PICTURE_TOP_FOLDER = PixivUtils.getSavedPicFolder();

    /**
     * 根据pixivId获取其对应的多张图片原始大图地址的接口地址
     * https://www.pixiv.net/ajax/illust/80391469/pages
     */
    public static final String PIXIV_AJAX_ILLUST_PAGES_PREFIX = "https://www.pixiv.net/ajax/illust/";
    public static final String PIXIV_AJAX_ILLUST_PAGES_SUFFIX = "/pages";

    /**
     * 根据作者pixivUserId获取该作者所有作品信息的接口地址，中间的1277076是该作者的pixivUserId
     * https://www.pixiv.net/ajax/user/1277076/profile/all
     */
    public static final String PIXIV_AJAX_USER_PROFILE_ALL_PREFIX = "https://www.pixiv.net/ajax/user/";
    public static final String PIXIV_AJAX_USER_PROFILE_ALL_SUFFIX = "/profile/all";

    /**
     * 综合R18每日排行榜接口地址
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330
     * https://www.pixiv.net/ranking.php?mode=daily_r18&date=20200330&p=2&format=json
     */
    public static final String PIXIV_RANKING_DAILY_R18_PREFIX = "https://www.pixiv.net/ranking.php?mode=daily_r18&date=";
    public static final String PIXIV_RANKING_DAILY_R18_SUFFIX = "&p=2&format=json";

    /**
     * 综合R18每日排行榜返回结果中匹配出涉及图片pixivId的正则表达式，得到类似如下的结果
     * https://i.pximg.net/user-profile/img/2019/03/07/06/06/19/15490371_b5507fac57efd9ff5bc928d89cf33c93_50.png
     */
    public static final String PIXIV_REGEX_RANKING_DAILY_R18 = "https://i.pximg.net/c/[^\"]*";

    /**
     * 保存图片的文件夹名称，按照不同的目的将图片保存在对应主题的文件夹中
     */
    public static final String SUBJECT_PREFIX_PIXIV_ID = "P";
    public static final String SUBJECT_PREFIX_RECOMMEND = "R";
    public static final String SUBJECT_PREFIX_RECOMMEND_MULTI = "RM";
    public static final String SUBJECT_PREFIX_RECOMMEND_TWO_DEPTH = "RR";
    public static final String SUBJECT_PREFIX_RECOMMEND_AUTHOR = "RA";
    public static final String SUBJECT_PREFIX_AUTHOR = "A";
    public static final String SUBJECT_PREFIX_DAILY_R18 = "D18R";
    public static final String SUBJECT_PREFIX_DAILY_R18_MULTI = "D18RM";


    /**
     * 一次插入最大的记录条数--TODO--这里其实不应该是用记录条数来的，而应该是插入数据的字节大小来决定的，
     * 不过这个值并没有经过测试，只能说是自己想着来的--TODO--之后看弄个基准测试
     */
    public static final Integer MAX_INSERT_RECORD_COUNT = 10000;

    /**
     * 一个文件夹中最大存储图片文件的数量
     * 如果一个文件夹中的图片文件数量太大，会导致在windows资源管理器打开该文件夹的时候占用太多内存，而且打开太慢
     * 这里的数字也只是一个自己想着来的，没有经过测试--TODO--之后看是不是做下测试，看多少合适
     */
    public static final Integer MAX_DIR_FILE_COUNT = 2;

    /**
     * 0-否，1-是
     */
    public static final Integer TRUE = 1;
    public static final Integer FALSE = 0;

    /**
     * 无效的pixivId
     */
    public static final String INVALID_PIXIV_ID = "-1";

    /**
     * 请求的超时时间，以秒为单位
     */
    public static final Long REQUEST_SECONDS_TIME_OUT = 60L;

    /**
     * 异步请求时同时最大的请求数量
     */
    public static final Integer ASYNC_MAX_REQUEST_COUNT = 200;

}
