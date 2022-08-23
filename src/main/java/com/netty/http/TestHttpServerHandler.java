package com.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;


/**
 * 說明
 * 1.SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
 * 2.HttpObject 客戶端和服務器端相互通訊的數據被封裝封裝程 HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    // channelRead0 讀取客戶端數據
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        // 判斷 msg是不是 httpRequest
        if (msg instanceof HttpRequest) {
            System.out.println("pipeline hashcode " + ctx.pipeline().hashCode() +
                    " TestHttpServerHandler hash = " + this.hashCode());

            System.out.println("msg 類型 = " + msg.getClass());
            System.out.println("客戶端地址 + " + ctx.channel().remoteAddress());
            System.out.println("客戶端msg + " + msg);

            // 獲取到
            HttpRequest httpRequest = (HttpRequest) msg;
            // 獲取 uri，過濾指定的資源
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                System.out.println("請求了 favicon.ico，不做響應");
                return;
            }

            // 回復信息給瀏覽器 [http 協議]
            ByteBuf content = Unpooled.copiedBuffer("hello, 我是服務器", CharsetUtil.UTF_8);

            // 構造一個 http的效應，即 httpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 將構建好 response 返回
            ctx.writeAndFlush(response);
        }

    }
}
