package com.lyzh.netty.gateway.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月14日 - 下午3:52:58
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public final class FileBuffer {

    public void createOfflineBuffer(String id) {
    }

    public static List<BufferFile> scandBufferFile(String buffPath) {

        List<BufferFile> result = new ArrayList<>();

        if (!Objects.isNull(buffPath)) {
            Path path = Paths.get(buffPath);
            try {
                Files.walkFileTree(path, new BufferFileVisitor(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
