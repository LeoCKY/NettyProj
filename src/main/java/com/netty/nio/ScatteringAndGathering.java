package com.netty.nio;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Scattering: 將數據寫入到 buffer時，可以採用 buffer 數組，依次寫入 [分散]
 * Gathering:
 */
public class ScatteringAndGathering {

    public static void main(String[] args) throws Exception {

        // 使用 ServerSocketChannel 和 SocketChannel 網路

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        serverSocketChannel.socket().bind(inetSocketAddress);

        // 創建 buffer 數組
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        // 等客戶端連接 (telnet)
        SocketChannel socketChannel = serverSocketChannel.accept();
        int messageLength = 8; //假定從客戶端接收8個字節

        // 循環的讀取
        while (true) {

            int byteRead = 0;

            while (byteRead < messageLength) {
                long l = socketChannel.read(byteBuffers);
                byteRead += 1;  // 累計讀取的自節數
                System.out.println("byteRead = " + byteRead);
                // 打印當前buffer的 position & limit
                Arrays.asList(byteBuffers).stream().map(byteBuffer ->
                        "position = " + byteBuffer.position() + ", limit" + byteBuffer.limit()).forEach(System.out::println);
            }

            //將所有的 buffer 進行 flip
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.flip());
            //將數據讀出顯示到客戶端
            long byteWrite = 0;
            while (byteRead < messageLength) {
                long l = socketChannel.write(byteBuffers);
                byteWrite += 1;
            }

            // 將所有的 buffer 進行 clear
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.clear());

            System.out.println("byteRead : = " + byteRead + " byteWrite :" + byteWrite + ", messageLength " + messageLength);
        }

    }
}
