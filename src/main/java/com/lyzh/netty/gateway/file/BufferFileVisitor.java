package com.lyzh.netty.gateway.file;

import static java.nio.file.FileVisitResult.TERMINATE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author Naturn
 * 
 * @Date 2018年3月14日 - 下午5:27:45
 *
 * @Email juddersky@gmail.com
 *
 * @Version 0.0.1
 */

public class BufferFileVisitor extends SimpleFileVisitor<Path> {

    private List<BufferFile> bufferFiles;

    public BufferFileVisitor(List<BufferFile> bufferFiles) {
        this.bufferFiles = bufferFiles;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {

        if (attr.isRegularFile()) {
            System.out.format("Regular file : %s \n", file);
            BufferFile buffer = buildBufferFile(file);
            if (buffer != null) {
                bufferFiles.add(buffer);
            }
            return FileVisitResult.CONTINUE;
        }
        return TERMINATE;
    }

    public BufferFile buildBufferFile(Path file) {

        String[] idRemote = file.getFileName().toString().split("=");
        if (idRemote.length == 2) {
            BufferFile buffer = new BufferFile();
            buffer.setBufferId(idRemote[0]);
            String[] address = idRemote[1].split("p");
            SocketAddress socket = new InetSocketAddress(address[0].replace("d", "."), Integer.parseInt(address[1]));
            buffer.setSocket(socket);
            buffer.setData(new ArrayBlockingQueue<>(1024 * 1024));
            buffer.setPath(file);
            fillData(buffer, file);
            return buffer;
        }
        return null;
    }

    private void fillData(BufferFile bufferFile, Path file) {
        try {
            bufferFile.getData().addAll(Files.readAllLines(file));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
