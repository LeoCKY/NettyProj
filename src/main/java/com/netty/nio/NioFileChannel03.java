package com.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioFileChannel03 {

    public static void main(String[] args) throws Exception {

        FileInputStream fileInputStream = new FileInputStream("1.txt");
        FileChannel fileChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true) { // 循環讀取

            //這裡有一個重要的操作， 一定不要忘了

             /*
                public Buffer clear() {
                    this.position = 0;
                    this.limit = this.capacity;
                    this.mark = -1;
                    return this;
                 }
             */
            byteBuffer.clear(); // 清空 buffer
            int read = fileChannel01.read(byteBuffer);
            System.out.println("read = " + read);

            if (read == -1) { //表示讀完
                break;
            }
            //將buffer 中的數據寫入到 fileChannel02 -- 2.txt
            byteBuffer.flip();
            fileChannel02.write(byteBuffer);
        }

        // 關閉流
        fileInputStream.close();
        fileOutputStream.close();
    }

}
