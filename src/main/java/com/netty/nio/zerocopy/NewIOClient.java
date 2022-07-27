package com.netty.nio.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NewIOClient {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 7001));
        String fileName = "";

        // 得到一個文件
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();

        // 準備發送
        long startTime = System.currentTimeMillis();
        // linux 下一個 transferTo 方法就可以完成傳輸
        // windows 下次調用 transferTo 只能發送 8m，就需要分段傳輸文件，而且要主要傳輸的位置
        // transfer To 底層使用到 零拷貝
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("發送的總的字節數 = " + transferCount
                + " 耗時 : " + (System.currentTimeMillis() - startTime));

        // 關閉
        fileChannel.close();
    }
}
