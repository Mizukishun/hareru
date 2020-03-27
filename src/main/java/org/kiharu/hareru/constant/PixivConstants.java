package org.kiharu.hareru.constant;

public class PixivConstants {

    /**
     * 保存图片的目录
     * TODO--之后需要改成可自动选择拥有最大剩余容量的硬盘
     */
    public static String PICTURE_SAVE_DIR = "F:/PixivDemo/";

    /**
     * 为了获取图片原始大图地址，需要先请求这个地址的信息，后面接pixivId
     */
    public static String PIXIV_ARTWORKS_PATH = "https://www.pixiv.net/artworks/";

    /**
     * 为了从artworks返回的结果中匹配出原始大图地址的正则表达式，得到如下结果
     * https://i.pximg.net/img-original/img/2019/01/05/00/30/59/72497087_p0.jpg
     * TODO--待确认是否所有的原始大图地址都是以"https://i.pximg.net/img-original/"开头的
     */
    public static String PIXIV_ARTWORKS_IMG_ORIGINAL_REGEX = "https://i.pximg.net/img-original/[^\"]*";

    /**
     * 获取推荐的图片pixivId的接口地址前缀和后缀，中间是原始图片pixivId
     * https://www.pixiv.net/ajax/illust/80298568/recommend/init?limit=18
     */
    public static String PIXIV_ILLUST_RECOMMEND_PREFIX = "https://www.pixiv.net/ajax/illust/";
    public static String PIXIV_ILLUST_RECOMMEND_SUFFIX = "/recommend/init?limit=18";

}