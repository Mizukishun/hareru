package org.kiharu.hareru.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

@Slf4j
public class HareruUtilsTest {

    @Test
    public void testGetMaxUsableSpaceDiskFile() {
        File file = HareruUtils.getMaxUsableSpaceDiskFile();
        long MB = 1024 * 1024;
        long GB = 1024 * 1024 * 1024;
        log.info("得到的最大可用容量磁盘的信息为：\nname={}\npath={}\nusableSpace={}GB\nfreeSpace={}GB\ntotalSpace={}GB\nAbsolutePath={}",
                file.getName(), file.getPath(), file.getUsableSpace() / GB, file.getFreeSpace() / GB, file.getTotalSpace() / GB, file.getAbsolutePath());
    }

    @Test
    public void testGetAllDiskFile() {
        long MB = 1024 * 1024;
        long GB = 1024 * 1024 * 1024;

        List<File> fileList = HareruUtils.getAllDiskFile();
        for (File file : fileList) {
            log.info("磁盘信息有：\nname={}\npath={}\nUsableSpace={}GB\nFreeSpace={}GB\nTotalSpace={}GB\nUsedSpace={}GB",
                    file.getName(), file.getPath(), file.getUsableSpace() / GB, file.getFreeSpace() / GB, file.getTotalSpace() /GB, (file.getTotalSpace() - file.getUsableSpace()) / GB);
        }
    }
}
