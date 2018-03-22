package com.lyzh.netty.gateway.file;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyzh.netty.gateway.common.Conversion;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月14日 - 下午1:55:51
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public final class AccessFileDao {

    private static final Logger logger = LoggerFactory.getLogger(AccessFileDao.class);
    
    private static final String ROOT = "."+File.separator+"buffer"+File.separator;

    public static OutputStream create(String fileName) {
        Path path = Paths.get(ROOT, fileName);
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(Files.newOutputStream(path, CREATE, APPEND));
        } catch (IOException ex) {
            // create file stream failure;
            logger.error("Create File failure.{}",path.toString());
        }
        return out;
    }

    public static List<String> read(Integer hashCode, SocketAddress socket) {
        List<String> result = null;
        Path path = Paths.get(ROOT, hashCode + "=" + socket.toString().substring(1).replace(":", "p").replace(".", "d"));
        logger.info("The is Regular file {} . {}",Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS),path);
        try {
            result = Files.readAllLines(path);
        } catch (IOException e) {
            logger.error("Read data file failure. File {}",path.toString());
        }
        return result;
    }
    
    public static void write(SocketAddress k, Channel channel, ByteBuf buf) {
        byte[] dst = new byte[buf.readableBytes()];
        buf.readBytes(dst);
        OutputStream write = AccessFileDao
                .create(channel.hashCode() + "=" + k.toString().substring(1).replace(":", "p").replace(".", "d"));
        try {
            write.write(Conversion.bytesToHexString(dst).getBytes());
            write.write('\n');
            write.flush();
        } catch (IOException e) {
            logger.error("Cache Msg to buffer file. failure.");
        }
    }
}
