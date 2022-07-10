package com.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class NIOFileChannel04 {

    public static void main(String[] args) throws Exception {

        // 創建相關流
        FileInputStream fileInputStream = new FileInputStream("d:\\a.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\a2.jpg");

        // 獲取各個流對應的 filechannel
        FileChannel sourceChannel = fileInputStream.getChannel();
        FileChannel destCh =fileOutputStream.getChannel();

        // 使用 transferForm 完成拷貝
        destCh.transferFrom(sourceChannel,0,sourceChannel.size());
        //關閉通道和流
        sourceChannel.close();
        destCh.close();
        fileInputStream.close();
        fileOutputStream.close();


    }


}
