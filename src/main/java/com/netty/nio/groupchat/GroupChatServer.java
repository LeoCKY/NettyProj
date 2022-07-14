package com.netty.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    // 定義屬性
    private Selector selector;

    private ServerSocketChannel listenChannel;

    private static final int PORT = 6667;

    public GroupChatServer() {
        try {
            // 得到選擇器
            selector = Selector.open();
            // ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            // 綁定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            // 設置非阻塞模式
            listenChannel.configureBlocking(false);
            // 將該 listenChannel 註冊到 selector
            listenChannel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 監聽
    public void listen() {
        try {

            // 循環處理
            while (true) {
                int count = selector.select();

                if (count > 0) { //有事件處理

                    // 遍歷得到 selectionKey 集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        // 取出 selectionKey
                        SelectionKey key = iterator.next();

                        //監聽到 accept
                        if (key.isAcceptable()) {
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            // 將該 sc 註冊到 selector
                            sc.register(selector, SelectionKey.OP_READ);

                            // 提示
                            System.out.println(sc.getRemoteAddress() + " 上線 ");
                        }

                        if (key.isReadable()) { // 通道發送 read 事件，即通道是可讀的狀態
                            // 處理讀 (專門寫方法 ...)
                            readData(key);
                        }
                        // 當前的 key刪除，防止重複處理
                        iterator.remove();
                    }
                } else {
                    System.out.println("等待 ....");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 讀取客戶端消息
    private void readData(SelectionKey key) {

        // 取到關聯的 channel
        SocketChannel channel = null;

        try {
            //得到 channel
            channel = (SocketChannel) key.channel();
            // 創建 buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int count = channel.read(buffer);
            // 根據 count的值做處理
            if (count > 0) {
                //把緩存區的數據轉成字符串
                String msg = new String(buffer.array());
                //輸出該消息
                System.out.println("form 客戶端: " + msg);

                //向其他的客戶端轉發消息(去掉自己)，專門寫一個方法處理
                sendInfoToOtherClients(msg, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "離線了...");
                // 取消註冊
                key.channel();
                // 關閉通道
                channel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //轉發消息給其它(通道)
    private void sendInfoToOtherClients(String msg, SocketChannel self) throws IOException {
        System.out.println("服務器轉發消息中...");
        //遍歷 所有註冊到 selector 上的 SocketChannel,並排除 self
        for (SelectionKey key : selector.keys()) {

            // 通過 key 取出對應的 SocketChannel
            Channel targetChannel = key.channel();

            // 排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != self) {
                //轉型
                SocketChannel dest = (SocketChannel) targetChannel;
                // 將 msg 存儲到 buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //將 buffer的數據寫入通道
                dest.write(buffer);
            }

        }
    }

    // 構造器
    // 初始化工作
    public static void main(String[] args) {
        // 創建服務器對象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }

}
