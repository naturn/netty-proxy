package com.lyzh.netty.gateway.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月15日 - 下午2:32:09
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class CommonUtilTest {

    @Test
    public void test() {
//        FileBuffer fileBuffer = new FileBuffer();
//        fileBuffer.scandOfflineBuffer();
        Path path = Paths.get("./buffer/", "-13944016=127d0d0d1p1028");
        System.out.println(Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS));
        try {
            Files.readAllLines(path).forEach(System.out::println);;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
    }

}
