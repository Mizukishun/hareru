package org.kiharu.hareru.util;

import org.kiharu.hareru.bo.FileDiskBO;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 非Pixiv相关的工具类
 */
public class FileUtils {

    /**
     * 获取最大剩余容量的磁盘
     * @return
     */
    public static File getMaxUsableSpaceDiskFile() {
        File[] files = File.listRoots();

        File result = Arrays.stream(files).max((file1, file2) -> {
            if (file1.getUsableSpace() > file2.getUsableSpace()) {
                return 1;
            } else if (file1.getUsableSpace() < file2.getUsableSpace()) {
                return -1;
            } else {
                return 0;
            }
        }).get();

        return result;
    }

    /**
     * 获取所有磁盘
     * @return
     */
    public static List<File> getAllDiskFile() {
        File[] files = File.listRoots();
        List<File> result = new ArrayList<>(16);
        Collections.addAll(result, files);
        return result;
    }
}
