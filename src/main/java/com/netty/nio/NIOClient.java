package com.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    private final static String IP = "127.0.0.1";

    private final static int PORT = 6666;

    public static void main(String[] args) throws Exception {

        // 得到一個網路通道
        SocketChannel socketChannel = SocketChannel.open();
        // 設置非阻塞
        socketChannel.configureBlocking(false);
        // 提供服務器端的 IP 和 端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress(IP, PORT);

        // 連接服務器
        if (!socketChannel.connect(inetSocketAddress)) {

            while (!socketChannel.finishConnect()) {
                System.out.println("因為連接需要時間，客戶端不會阻塞，可以做其它工作...");
            }
        }
        // ...如果連接成功，就發送數據
        String str = "hello, Leo";
        // Wraps a byte array into a buffer
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        // 發送數據，將 buffer 數據寫入 channel
        socketChannel.write(buffer);
        System.in.read();

    }
}
