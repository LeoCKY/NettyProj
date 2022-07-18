package com.netty.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class GroupChatClient {

    // 定義相關屬性
    private final String HOST = "127.0.0.1";

    private final int PORT = 6667;

    private Selector selector;

    private SocketChannel socketChannel;

    private String userName;


    public GroupChatClient() throws IOException {

        selector = Selector.open();

        // 連接服務器
        socketChannel = socketChannel.open(new InetSocketAddress(HOST, PORT));

        // 設置非阻塞
        socketChannel.configureBlocking(false);

        // 將 channel 註冊到 selector
        socketChannel.register(selector, SelectionKey.OP_READ);

        // 得到 userName
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(userName + " is ok... ");

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

    // 讀取服務器回復的消息
    public void readInfo() {
        try {
            int readChannels = selector.select();
            if (readChannels > 0) { // 有可以用的通道
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        // 得到相關得通道
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
                iterator.remove(); //刪除當前的 selectionKey，防止重複操作
            } else {
                System.out.println("沒有可用的通道....");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 啟動我們客戶端
        GroupChatClient chatClient = new GroupChatClient();
        // 啟動一個線程，每個 3 秒，讀取從服務器發送資料
        new Thread(() -> {
            while (true) {
                chatClient.readInfo();
                try {
                    Thread.currentThread().sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 發送資料給服務器端
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
