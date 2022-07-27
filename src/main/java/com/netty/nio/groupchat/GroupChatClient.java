package com.netty.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class GroupChatClient {

    // 定義相關的屬性
    private final String HOST = "127.0.0.1";  //服務器的IP

    private final int PORT = 6667; // 服務器端口

    private Selector selector;

    private SocketChannel socketChannel;

    private String userName;

    // 初始化工作
    public GroupChatClient() throws IOException {
        selector = Selector.open();
        // 設置服務器
        socketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        //將 Channel 註冊到 selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到 userName
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(userName + " is ok....");
    }

    // 向服務器發送消息
    public void sendInfo(String info) {
        info = userName + " 說 : " + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 讀取從服務器回復的訊息
    public void readInfo() {

        try {
            int readChannel = selector.select();

            if (readChannel > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        // 得到相關的通道
                        SocketChannel sc = (SocketChannel) key.channel();
                        // 得到一個 Buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // 讀取
                        sc.read(buffer);
                        // 把讀到的緩衝區的數據轉成字串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }

                }
            } else {
//                System.out.println("沒有可以用的通道...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
