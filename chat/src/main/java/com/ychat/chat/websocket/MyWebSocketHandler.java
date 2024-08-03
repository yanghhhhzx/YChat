package com.ychat.chat.websocket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ychat.chat.domain.Chat;
import com.ychat.chat.domain.Message;
import com.ychat.chat.service.ChatService;
import com.ychat.chat.service.MessageService;
import com.ychat.chat.utils.ChatRedis;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;
import com.ychat.chat.utils.ChannelContext;
import com.ychat.common.utils.transition.Transition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component

/**
 * 只处理收发消息，其他的我都写在httpHandle
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static List<ChannelHandlerContext> channelGroup;

    private final MessageService messageService;

    private final ChatService chatService;

    private final ChatRedis chatRedis;

    static {
        channelGroup = new ArrayList<>(); ;
    }

    public MyWebSocketHandler(MessageService messageService, ChatService chatService, ChatRedis channelRedis) {

        this.messageService = messageService;
        this.chatService = chatService;
        this.chatRedis = channelRedis;
    }

    /**
     * 已经通过拦截器拿到用户id了，放在ThreadLocal，现在需要把他放在channel里
     * （但是没有拿到账号，需要根据id查账号）
     * @param ctx
     * @throws Exception
     */

    // 服务器接受客户端的数据信息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到的数据：" + msg.text());
        //获取根据msg获取message
        Message message=Message.getMessageByMsg(ChannelContext.getcontext(ctx.channel(),"userId"),msg);
        //保存到mysql
        messageService.saveMessageIntoMysql(message);
        System.out.println("获取到群聊id" + message.getChat());

        //1.根据chat在数据库中找到该群聊的所有成员。
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("chat", message.getChat());
        Chat chat = chatService.getOne(queryWrapper);

        //这里存储采用的是方法2，利用字段member存储成员，获取改群聊的成员id
        List<String> memberIds = Transition.StringToList(chat.getMember());
        System.out.println("查询群聊到成员" + memberIds);

        System.out.println("开始查询在线成员");
        // 2.根据将群聊成员列表遍历，根据redis查找在线的成员的channel。
        List<ChannelHandlerContext> channelHandlerContexts = new ArrayList<>();
        for (String memberId : memberIds) {
            String c = chatRedis.getChannelId(memberId);
            for (ChannelHandlerContext cha : channelGroup) {
                if (ChannelContext.getcontext(ctx.channel(),"userId").equals(c)) {
                    channelHandlerContexts.add(cha);
                }
            }
        }
        if (!channelHandlerContexts.isEmpty()) {
            // 3.向在线成员发送消息
            System.out.println("向在线成员发送消息");
            sendAllMessage(channelHandlerContexts, message);
        }
        //4.对信息包含的群聊的所有成员的未读群聊表进行更新（已经登录的人员会在断开连接时清除）
        for (String memberId : memberIds) {
            if (message.getChat() != null) {
                chatRedis.addChatIntoUnRead(message.getChat(), memberId);
            }
        }
        super.channelRead(ctx, msg);
    }

//     向固定的channel发消息
    private void sendMessage(ChannelHandlerContext ctx, Message msg) {
        ctx.channel().writeAndFlush(msg);
    }

    // 向指定的channel集合发送消息
    private void sendAllMessage(List<ChannelHandlerContext> channelHandlerContexts, Message msg) {
        for (ChannelHandlerContext cha : channelHandlerContexts) {
            cha.writeAndFlush(msg);
        }
    }


}
