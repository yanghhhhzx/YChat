package com.ychat.chat.websocket;

import com.ychat.chat.config.WebSocketConfig;
import com.ychat.chat.domain.Message;
import com.ychat.chat.service.MessageService;
import com.ychat.chat.utils.ChannelContext;
import com.ychat.chat.utils.ChatRedis;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.springframework.stereotype.Component;
import com.ychat.common.utils.transition.Transition;

import java.util.List;

import static com.ychat.chat.websocket.MyWebSocketHandler.channels;

/**
 * SimpleChannelInboundHandler用于处理入站请求
 */
@Component
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    //如果在websocket中使用new来创建HttpRequestHandler的话，这里不能使用autowire注解！！
    //
    private final ChatRedis channelRedis;

    private final MessageService messageService;

    public HttpRequestHandler(ChatRedis channelRedis, MessageService messageService) {

        this.channelRedis = channelRedis;
        this.messageService = messageService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            // 1.获取请求头信息
            String userInfo = request.headers().get("user-info");
            System.out.println("获取到user"+userInfo);

            //把userid放入channel的上下文中，后面发送的时候要用
            //下面这个ChannelContext是我自己定义的！不是netty包下的那个。
            ChannelContext.addcontext(ctx.channel(),"userId",userInfo);
            System.out.println("保存成功");

            //加入ConcurrentHashMap,使用用户id作为key
            channels.put(ChannelContext.getcontext(ctx.channel(),"userId"),ctx);

            // 将channel和userid的对应关系保存到redis
            channelRedis.addPost(userInfo, WebSocketConfig.websocketPost);
            System.out.println("post已保存到redis");

            //**************************************************
            //在解析http请求时，顺便获取未读信息
            //放在这里。保证他是只有在建立连接时会执行（因为只有建立连接是http）
            //**************************************************

            List<String>chatIds =channelRedis.getUnReadChat(userInfo);//获取chat
            List<Long> chatList=Transition.StringListToLongList(chatIds);//转换chat
            String time=channelRedis.getLastTime(userInfo);//获取time
            List<Message>messages= messageService.getNewMessagesFromMysql(chatList,time);//获取message

            this.sendMessage(ctx,messages);
        }

        super.channelRead(ctx, msg);
//        ctx.fireChannelRead(msg);
    }
    // 客户端与服务器建立连接的时候触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通道开启！");
        super.channelActive(ctx);
    }

    //将未读信息发送过去客户端
    private void sendMessage(ChannelHandlerContext ctx, List<Message> msg) {
        for (Message msg1 : msg) {
            ctx.channel().writeAndFlush(msg1);
        }
    }

    // 客户端与服务器关闭连接的时候触发
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开连接：通道关闭！");
        String userId=ChannelContext.getcontext(ctx.channel(),"userId");
        channelRedis.removeUnReadChat(userId);//删除UnReadChat
        channelRedis.setLastTime(userId);//改LastTime
        channels.remove(ctx.channel().toString());
        super.channelInactive(ctx);
    }

}