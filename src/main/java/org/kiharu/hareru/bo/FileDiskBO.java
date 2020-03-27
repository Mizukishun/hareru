package org.kiharu.hareru.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileDiskBO implements Serializable {

    /** 磁盘路径 **/
    private String path;
    /** 磁盘名 **/
    private String name;
    /** 可用容量 **/
    private Long usableSpace;
    /** 磁盘总容量 **/
    private Long totalSpace;

}
