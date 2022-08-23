package com.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitialize extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        // 向管道加入處理器

        //得到管道
        ChannelPipeline pipeline = ch.pipeline();

        // 加入一個 netty 提供的 httpServerCodec codec => [coder - decoder]
        // HttpServerCode 說明
        // 1.HttpServerCodec 是 netty 提供的處理 http 的 編-解碼器
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        // 2.增加一個自定義的 handler
        pipeline.addLast("MyTestHttpServerHandler", new TestHttpServerHandler());
    }
}
