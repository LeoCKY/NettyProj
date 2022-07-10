package com.netty.nio;


import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/*
 * 說明
 * 1. MappedByteBuffer 可讓文件直接在內存(堆外內存)修改，操作系統不需要拷貝一次
 */
public class MappedByteBuffer {

    public static void main(String[] args) throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt","rw");
        //獲取對應的通道
        FileChannel channel = randomAccessFile.getChannel();

        /*
         * 參數 1: FileChannel.MaoMode.Read_WRITE 使用的讀寫模式
         * 參數 2: 0: 可以直接修改的起始位置
         * 參數 3: 5: 是映射到內存的的大小(不是索引位置)，即將 1.txt的多個字節映射到內存
         * 可以直接修改的範圍就是 0 - 5
         * 實際類型 DirectByBuffer
         */

        java.nio.MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE,0,5);

        mappedByteBuffer.put(0,(byte) 'H');
        mappedByteBuffer.put(3,(byte) '9');
//        mappedByteBuffer.put(5,(byte) 'Y'); // IndexOutOfBoundException

        randomAccessFile.close();
        System.out.println("Modify Success !");
    }
}
