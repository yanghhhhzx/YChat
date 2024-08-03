package com.ychat.chat.config;

import com.ychat.chat.service.ChatService;
import com.ychat.chat.service.MessageService;
import com.ychat.chat.utils.ChannelContext;
import com.ychat.chat.utils.ChatRedis;
import com.ychat.chat.utils.JwtTool;
import com.ychat.chat.websocket.FilterHandler;
import com.ychat.chat.websocket.HttpRequestHandler;
import com.ychat.chat.websocket.MyWebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;


@Configuration
@RequiredArgsConstructor
public class WebSocketConfig {

    private final ChatRedis chatRedis;
    private final ChannelContext channelContext;
    private final MessageService messageService;
    private final ChatService chatService;
    private final JwtTool jwtTool;


    // 在应用启动时启动Netty服务器
    @PostConstruct
    public void startNettyServer() {
        //主线程池：负责接收客户端的连接请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //从线程池：负责处理已经建立连接的客户端的读写请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //创建服务驱动，服务器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        /**
                         *  经过测试，同样是inboundHandler先后addLast其实不影响他的执行顺序！！！
                         *  当有消息进来时，他会根据pipeline一步一步往下走，执行完一个传递给下一个ChannelHandler
                         */
                        // Http编解码器
                        pipeline.addLast(new HttpServerCodec());

                        // 3. 添加ChunkedWriteHandler（可选，但如果你需要发送大消息，它很有用）
                        pipeline.addLast(new ChunkedWriteHandler());

                        // 2. 添加一个用于处理大请求的聚合器（可选，取决于你的需求）
                        pipeline.addLast(new HttpObjectAggregator(8192));// 例如，最大请求体大小为8KB

                        //*******************************************************
                        //*   下面两个是我自定义handler，如果token检验在网关则去掉第一    *
                        //*******************************************************
                        //完成过滤器功能，将token检验并转为userinfo，测试通过
                        pipeline.addLast("filter",new FilterHandler(jwtTool));
                        //用于获取http请求的请求头中的userinfo
                        pipeline.addLast("http", new HttpRequestHandler(chatRedis, channelContext,messageService));


                        // 处理WebSocket升级握手、Ping、Pong等消息
                        // 将http协议升级为websocket
                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        // 自定义WebSocket处理器

                        pipeline.addLast(new MyWebSocketHandler(messageService, chatService, chatRedis));


                    }
                });
        //绑定端口启动netty服务
        serverBootstrap.bind(8888)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        System.out.println("Netty Server started on port 8888");
                    } else {
                        System.err.println("Netty Server start failed on port 8888");
                    }
                });
    }
}