package com.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 說明
 * 1. 自定義一個 Handler 需要繼續 netty 規定好的某個 HandlerAdapter (規範)
 * 2. 自定義一個 Handler，才能稱為一個 handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx 上下文對象，含有管道 pipeline，通道 channel，地址
     * @param msg 客戶端的發送數據
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         System.out.println("服務器讀取線程  " + Thread.currentThread().getName());
         System.out.println("server ctx = " + ctx);
         System.out.println("看看 channel 和 pipeline的關係");
         Channel channel = ctx.channel();
         ChannelPipeline pipeline = ctx.pipeline(); //本質是一個雙向鏈接，出站入站

         // 將 msg 轉成一個 ByteBuf
         // ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer
         ByteBuf buf = (ByteBuf) msg;
         System.out.println("客戶端發送消息是 : " + buf.toString(CharsetUtil.UTF_8));
         System.out.println("客戶端地址 : " + channel.remoteAddress());
         **/

        // 比如我們有一個非常耗時長的業務 -> 異步執行 -> 提交給 channel 對應的
        // NOIEventLoop 的 taskQueue 中
        /********************************   解決方案 1: 用戶程序自定義的普通任務  ********************************/
        /**
         * 同一個線程所以
         *  花費 : 5 + 5 = 10
         */
        taskQueueFun(ctx);
        /**************************************************************************************************/

        /********************************   解決方案 2: 用戶程序自定義定時任務 ->該任務是提交到 scheduledTaskQueue中  ********************************/
        scheduledTaskQueue(ctx);
        /**************************************************************************************************/
    }

    private void taskQueueFun(ChannelHandlerContext ctx){
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(5 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客戶端 ~ 2", CharsetUtil.UTF_8));
                System.out.println(new Date() + " -------- channel2 code = " + ctx.channel().hashCode());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        ctx.channel().eventLoop().execute(()->{
            try {
                Thread.sleep(5 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客戶端 ~ 3", CharsetUtil.UTF_8));
                System.out.println(new Date() + " -------- channel3 code = " + ctx.channel().hashCode());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void scheduledTaskQueue(ChannelHandlerContext ctx) {
        ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(5 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客戶端 ~ 4", CharsetUtil.UTF_8));
                System.out.println(new Date() + " -------- channel4 code = " + ctx.channel().hashCode());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);
    }


    /**
     * 資料讀取完畢
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // writeAndFlush 是 write + flush
        // 將數據寫入到緩存，並刷新
        // 一般說，我們對這個發送的數據進行編碼
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hi, Client!", CharsetUtil.UTF_8));
    }

    //處理異常，一般是需要關閉通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
