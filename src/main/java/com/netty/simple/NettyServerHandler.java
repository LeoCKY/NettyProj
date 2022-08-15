package com.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

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
