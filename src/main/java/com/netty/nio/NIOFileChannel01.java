package com.netty.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel01 {

    public static void main(String[] args) throws Exception {


        String str = "Hello!";
        // 創建一個輸出流 -> channel
        FileOutputStream fileOutputStream =  new FileOutputStream("D:\\file01.txt");

        // 通過 fileOutputStream 獲取對應的 FileChannel
        // 這個 fileChannel 真實類型是 FileChannelImpl
        FileChannel fileChannel =fileOutputStream.getChannel();

        // 創建一個緩衝區 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //將 str 放入 byteBuffer
        byteBuffer.put(str.getBytes());

        //對 byteBuffer 進行 flip
        byteBuffer.flip();

        // 將 byteBuffer 資料寫入到 fileChannel
        fileChannel.write(byteBuffer);
        fileChannel.close();


    }
}
