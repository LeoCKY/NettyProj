package com.netty.nio;

import java.nio.ByteBuffer;

public class NIOBytesBufferPutGet {

    public static void main(String[] args) {

        // 創建一個 buffer
        ByteBuffer buffer = ByteBuffer.allocate(64);

        // 類型化方式放入數據
        buffer.putInt(100);
        buffer.putLong(9);
        buffer.putChar('L');
        buffer.putShort((short) 4);

        //取出
        buffer.flip();

        System.out.println();

        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getChar());
        System.out.println(buffer.getShort());
    }
}
