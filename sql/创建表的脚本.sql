-- 创建保存pixiv图片的数据库
CREATE DATABASE `pixiv_db`
DEFAULT CHARACTER SET = utf8mb4 COLLATE utf8mb4_general_ci;

use pixiv_db;

DROP TABLE IF EXISTS `pixiv_db`.`pixiv_picture_info`;
CREATE TABLE `pixiv_db`.`pixiv_picture_info` (
    id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，自增ID',
    pixiv_id VARCHAR(256) DEFAULT NULL COMMENT 'P站图片ID',
    author_id VARCHAR(256) DEFAULT NULL COMMENT '图片作者ID',
    author_name VARCHAR(256) DEFAULT NULL COMMENT '图片作者名称',
    original_url VARCHAR(1024) DEFAULT NULL COMMENT '原始图片地址',
    title VARCHAR(256) DEFAULT NULL COMMENT '图片标题',
    description VARCHAR(1024) DEFAULT NULL COMMENT '图片描述',
    pic_suffix VARCHAR(10) DEFAULT NULL COMMENT '图片类型后缀',
    r18_restrict TINYINT(1) DEFAULT NULL COMMENT '是否R18',
    width INT(11) UNSIGNED DEFAULT NULL COMMENT '宽度',
    height INT(11) UNSIGNED DEFAULT NULL COMMENT '高度',
    page_count INT(11) UNSIGNED DEFAULT NULL COMMENT '同一pixivId对应图片的数量',
    bookmark_count INT(11) UNSIGNED DEFAULT NULL COMMENT '收藏数',
    like_count INT(11) UNSIGNED DEFAULT NULL COMMENT '赞数',
    view_count INT(11) UNSIGNED DEFAULT NULL COMMENT '观看数',
    comment_count INT(11) UNSIGNED DEFAULT NULL COMMENT '评论数',
    response_count INT(11) UNSIGNED DEFAULT NULL COMMENT '回复数',
    mini_url VARCHAR(1024) DEFAULT NULL COMMENT '迷你图地址',
    thumb_url VARCHAR(1024) DEFAULT NULL COMMENT '缩略图地址',
    small_url VARCHAR(1024) DEFAULT NULL COMMENT '小图地址',
    regular_url VARCHAR(1024) DEFAULT NULL COMMENT '正常图地址',
    upload_date VARCHAR(8) DEFAULT NULL COMMENT '图片上传到P站的日期，yyyyMMdd格式',
    upload_time VARCHAR(8) DEFAULT NULL COMMENT '图片上传到P站的时间，HHmmss格式',
    local_pic_path VARCHAR(256) DEFAULT NULL COMMENT '图片本地保存路径',
    local_pic_name VARCHAR(256) DEFAULT NULL COMMENT '图片本地保存文件名',
    pic_size_b INT(11) UNSIGNED DEFAULT NULL COMMENT '图片大小，以B为单位',
    pic_size_kb INT(11) UNSIGNED DEFAULT NULL COMMENT '图片大小，以KB为单位',
    pic_size_mb INT(11) UNSIGNED DEFAULT NULL COMMENT '图片大小，以MB为单位',
    download_success TINYINT(1) DEFAULT 1 COMMENT '是否下载成功,0-下载失败，1-下载成功',
    add_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '新建记录时间',
    update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新记录时间',
    PRIMARY KEY (id),
    INDEX pid (pixiv_id),
    INDEX aid (author_id)
) ENGINE=INNODB DEFAULT CHARACTER SET = 'utf8mb4' COMMENT = 'P站图片信息';


-- Pixiv的所有图片
DROP TABLE IF EXISTS `pixiv_db`.`pixiv_all_picture`;
CREATE TABLE `pixiv_db`.`pixiv_all_picture` (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，自增ID',
    pixiv_id VARCHAR(256) DEFAULT NULL COMMENT 'P站图片ID',
    author_id VARCHAR(256) DEFAULT NULL COMMENT '图片作者ID',
    original_url VARCHAR(1024) DEFAULT NULL COMMENT '原始图片地址',
    upload_date VARCHAR(8) DEFAULT NULL COMMENT '图片上传到P站的日期，yyyyMMdd格式',
    upload_time VARCHAR(8) DEFAULT NULL COMMENT '图片上传到P站的时间，HHmmss格式',
    pic_suffix VARCHAR(10) DEFAULT NULL COMMENT '图片后缀，也即图片类型',
    r18_restrict TINYINT(1) DEFAULT NULL COMMENT '是否R18, 0-否，1-是',
    existed TINYINT(1) DEFAULT 1 COMMENT '图片在pixiv是否存在，0-不存在，无法找到，1-存在',
    downloaded TINYINT(1) DEFAULT 0 COMMENT '是否已下载到本地，0-未下载，1-已下载',
    add_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
    update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX pic(pixiv_id),
    INDEX aid(author_id)
) ENGINE=INNODB DEFAULT CHARACTER SET = 'utf8mb4' COMMENT = 'P站所有图片';

-- Pixiv图片详细信息
DROP TABLE IF EXISTS `pixiv_db`.`pixiv_picture_detail_info`;
CREATE TABLE `pixiv_db`.`pixiv_picture_detail_info` (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，自增ID',
    pixiv_id VARCHAR(64) DEFAULT NULL COMMENT 'P站图片ID',
    existed TINYINT(1) DEFAULT NULL COMMENT '在P站是否存在，0-不存在，1-存在',
    downloaded TINYINT(1) DEFAULT 0 COMMENT '是否已下载到本地，0-未下载，1-已下载',
    original_url VARCHAR(1024) DEFAULT NULL COMMENT '原始图片地址',
    author_id VARCHAR(64) DEFAULT NULL COMMENT '图片作者ID',
    author_name VARCHAR(256) DEFAULT NULL COMMENT '图片作者名称',
    page_count INT(11) UNSIGNED DEFAULT NULL COMMENT '该pixivId对应的图片数量',
    title VARCHAR(256) DEFAULT NULL COMMENT '图片标题',
    width INT(11) UNSIGNED DEFAULT NULL COMMENT '图片宽度',
    height INT(11) UNSIGNED DEFAULT NULL COMMENT '图片高度',
    illust_type INT(11) UNSIGNED DEFAULT NULL COMMENT '图片类型，0-插画，1-漫画，2-动图',
    pic_suffix VARCHAR(64) DEFAULT NULL COMMENT '图片后缀名称',
    r18_restrict TINYINT(1) DEFAULT NULL COMMENT '是否18限图片，0-否，1-是',
    bookmark_count INT(11) UNSIGNED DEFAULT NULL COMMENT '收藏数',
    like_count INT(11) UNSIGNED DEFAULT NULL COMMENT '赞数',
    comment_count INT(11) UNSIGNED DEFAULT NULL COMMENT '评论数',
    view_count INT(11) UNSIGNED DEFAULT NULL COMMENT '观看数',
    response_count INT(11) UNSIGNED DEFAULT NULL COMMENT '回复数',
    create_date TIMESTAMP DEFAULT NULL COMMENT '图片在P站的创建时间，注意中国和日本的时区差',
    upload_date TIMESTAMP DEFAULT NULL COMMENT '图片在P站的上传时间，注意中国和日本的时区差 ',
    tags VARCHAR(1024) DEFAULT NULL COMMENT '图片的标签，以分号:分割',
    mini_url VARCHAR(1024) DEFAULT NULL COMMENT '迷你图地址',
    thumb_url VARCHAR(1024) DEFAULT NULL COMMENT '缩略图地址',
    small_url VARCHAR(1024) DEFAULT NULL COMMENT '小图地址',
    regular_url VARCHAR(1024) DEFAULT NULL COMMENT '正常图地址',
    size_b INT(11) UNSIGNED DEFAULT NULL COMMENT '图片大小，以B为单位',
    size_kb INT(11) UNSIGNED DEFAULT NULL COMMENT '图片大小，以KB为单位',
    size_mb INT(11) UNSIGNED DEFAULT NULL COMMENT '图片大小，以MB为单位',
    pixiv_id_int INT(11) UNSIGNED DEFAULT NULL COMMENT 'INT类型的图片pixiv_id，便于进行排序等处理',
    author_id_int INT(11) UNSIGNED DEFAULT NULL COMMENT 'INT类型的作者author_id，便于进行排序等处理',
    add_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录新增时间',
    update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    request_status INT(4) DEFAULT NULL COMMENT '请求状态，0-初始，1-成功，2-待重试，3-失败',
    PRIMARY KEY (id),
    INDEX pid (pixiv_id),
    INDEX aid (author_id)
) ENGINE=INNODB DEFAULT CHARACTER SET = 'utf8mb4' COMMENT 'P站图片详细信息';



