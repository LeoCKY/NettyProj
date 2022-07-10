package com.netty.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel02 {

    public final static String  FILE_PATH = "D:\\file01.txt";

    public static void main(String[] args) throws Exception{

        // 創建文件的輸入流
        File file = new File(FILE_PATH);
        FileInputStream fileInputStream = new FileInputStream(file);

        // 通過 fileInputStream 獲取對應的 FileChannel -> 實際類型 FileChannelImpl
        FileChannel fileChannel = fileInputStream.getChannel();

        // 創建緩衝區
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());

        //將 通道的數據讀入到 Buffer
        fileChannel.read(byteBuffer);

        //將 byteBuffer 的字節資料轉成 String
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }
}
