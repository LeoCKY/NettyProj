package com.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public static void main(String[] args) {

        // 創建 BossGroup 和 WorkGroup
        // 說明
        // 1. 創建兩個線程組 bossGroup 和 workerGroup
        // 2. bossGroup 只是處理連接請求，真正的和客戶端業務處理，會交給 workerGroup 完成
        // 3. 兩個都是無限循環
        // 4. bossGroup 和 workerGroup 含有的子線程(NioEventLoop)的個數
        // 默認實際 cpu核數 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 創建服務器端的啟動對象，配置參數
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 使用鏈式編程來進行設置
            bootstrap.group(bossGroup, workerGroup) //設置兩個線程組
                    .channel(NioServerSocketChannel.class) // 使用 NioSocket 作為服務器的通道實現
                    .option(ChannelOption.SO_BACKLOG, 128)// 設置線程對列得到連接個數
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 設置保持活動連接狀態
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 創建一個通道測試對象 (匿名對象)
                        // 給 pipeline 設置處理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("客戶 socketchannel hashcode = " +socketChannel.hashCode());
                            // 可以使用一個集合管理 socketChannel，再推送消息時，可已將業務加入到
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    }); // 給我們的 workerGroup 的 EventLoop 對應的管道設置處理器

            System.out.println("....服務器 is ready... ");

            // 綁定一個端口並且同步，生成一個 ChannelFuture 對象
            // 啟動服務器(并綁定端口)
            ChannelFuture cf = bootstrap.bind(6668).sync();

            // 給 cf 註冊監聽器。監控我們關心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(cf.isSuccess()) {
                        System.out.println("監聽端口 6668 成功");
                    } else {
                        System.out.println("監聽端口 6668 失敗");
                    }
                }
            });

            // 對關閉通道進行監聽
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
