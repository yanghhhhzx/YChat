package com.ychat.chat.config.others;

// 导入所需的Netty包


import com.ychat.chat.websocket.others.WebRTCHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

public class WebRTCServer {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebRTCHandler());

            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            channelFuture.addListener((GenericFutureListener<ChannelFuture>) future -> {
                if (future.isSuccess()) {
                    System.out.println("WebRTC Server started on port " + PORT);
                } else {
                    System.err.println("WebRTC Server failed to start");
                }
            });

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
