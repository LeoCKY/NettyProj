package com.netty.bio;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) throws Exception {
        // 線程池機制
        // 思路
        // 1. 創建一個線程池
        // 2. 如果有客戶連接，就創建一個線程，與之通訊 (單獨寫一個方法)

        ExecutorService threadPool = Executors.newCachedThreadPool();

        // 創建 ServerSocket
        ServerSocket serverSocket = new ServerSocket(5566);
        System.out.println("Server socket start...");

        while (true) {
            System.out.println(" 線程訊息 id = " + Thread.currentThread().getId() + " 名字 = " + Thread.currentThread().getName());
            // 監聽，等待客戶端連接
            System.out.println("等待連接....");
            final Socket socket = serverSocket.accept();
            System.out.println("連接到一個客戶端");

            // 就創建一個線程，與之通訊(單獨寫一個方法)
            threadPool.execute(new Runnable() {
                public void run() {
                    // 與客戶端通訊...
                    handler(socket);
                }
            });

        }
    }

    public static void handler(Socket socket) {

        try {
            System.out.println(" 線 程 信 息 id = " + Thread.currentThread().getId() + Thread.currentThread().getName());
            byte[] bytes = new byte[1024];
            // 通過 socket 獲取輸入流
            InputStream is = socket.getInputStream();
            // 循環的讀取客戶端
            while (true) {
                System.out.println(" 線 程 信 息 id = " + Thread.currentThread().getId() + Thread.currentThread().getName());
                System.out.println("read.... ");
                int read = is.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read)); // 輸出客戶端發送的數據
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("關閉和 client 的連接");
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
