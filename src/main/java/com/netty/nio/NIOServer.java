package com.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    public static void main(String[] args) throws Exception {

        // 創建 ServerSocketChannel -> ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 得到一個 Selector 對象
        Selector selector = Selector.open();

        // 綁定一個端口 6666，在服務器端監聽
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // 設置為非阻塞
        serverSocketChannel.configureBlocking(false);

        // 把 serverSocketChannel 註冊到 selector 關心 事件為 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 循環等待客戶端連接
        while (true) {
            // 這裡等待 1 秒，如果沒有事情發生，返回
            if (selector.select(1000) == 0) { // 沒有事件發生
                System.out.println("服務器等待了 1 秒，無連接");
                continue;
            }
            // 如果返回 > 0，就獲取到相關的 selectKey 集合
            // 1.如果返回的 > 0，表示已經獲取到關注的事件
            // 2.select.selectedKeys() 返回關注事件的集合
            // 通過 selectionKeys 反向獲取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 遍歷 Set<SelectionKey>, 使用迭代器遍歷
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                // 獲取到 SelectionKey
                SelectionKey key = keyIterator.next();
                // 根據 key 對應的通道發生的事件做相應處理
                if (key.isAcceptable()) { //如果是 OP_ACCEPT，有新的客戶連接
                    // 該客戶端生成一個 SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println(" 客戶端連接成功 生成了一個 socketChannel " + socketChannel.hashCode());
                    // 將 SocketChannel 設置為非阻塞
                    socketChannel.configureBlocking(false);
                    // 將 socketChannel 註冊到 select，關注事件為 OP_READ，同時給 socketChannel
                    // 關聯一個 Buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if (key.isReadable()) { // 發生 OP_READ
                    // 通過 key 反向獲取到對應 channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 獲取到該 channel 關聯的 buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("form 客戶端 " + new String(buffer.array()));
                }
                // 手動從集合中移動當前的 selectionKey，防止重複操作
                keyIterator.remove();
            }
        }


    }
}
