package com.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) {
        // 客戶端需要一個事件循環組
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 創建客戶端啟動對象
            // 注意客戶端使用的不是 ServerBootstrap 而是Bootstrap
            Bootstrap bootstrap = new Bootstrap();

            // 設置相關參數
            bootstrap.group(group) //設置線程組
                    .channel(NioSocketChannel.class) // 設置客戶端通道的實現類(反射)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler()); // 加入處理器
                        }
                    });

            System.out.println("客戶端 ok...");
            // 啟動客戶端去連接服務器端
            // 關於 ChannelFuture 要分析，涉及到 netty 的異步模型
            // sync() 異步
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            // 給關閉通道進行監聽
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
