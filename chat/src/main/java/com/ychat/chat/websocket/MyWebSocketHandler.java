package com.ychat.chat.websocket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ychat.chat.config.WebSocketConfig;
import com.ychat.chat.domain.Chat;
import com.ychat.chat.domain.Message;
import com.ychat.chat.domain.MessageToOne;
import com.ychat.chat.service.ChatService;
import com.ychat.chat.service.MessageService;
import com.ychat.chat.mapper.ChatRedis;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import com.ychat.chat.utils.ChannelContext;
import com.ychat.common.utils.transition.Transition;
import com.ychat.chat.service.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
/*
  只处理收发消息，其他的我都写在httpHandle
 */
public class MyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //新的用来记录channel的，使用用户id做key
    //使用用户id来做key的话，用户就不能同时在多个地方同时登录了。
    //如果要多个地方同时登录就得四合院channel做key，因为我暂时没有考虑多端登录就先用用户id
    public static ConcurrentHashMap<String, ChannelHandlerContext> channels = new ConcurrentHashMap<>();
//已经无用
//    public static List<ChannelHandlerContext> channelGroup;

    private final MessageService messageService;

    private final ChatService chatService;

    private final ChatRedis chatRedis;

    private final Producer producer;

    public MyWebSocketHandler(MessageService messageService, ChatService chatService, ChatRedis channelRedis, Producer producer) {

        this.messageService = messageService;
        this.chatService = chatService;
        this.chatRedis = channelRedis;
        this.producer = producer;
    }

    /**
     * 已经通过拦截器拿到用户id了，放在ThreadLocal，现在需要把他放在channel里
     * （但是没有拿到账号，需要根据id查账号）
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
            String post = chatRedis.getPost(memberId);
            MessageToOne messageToOne=new MessageToOne();
            //复制一个
            BeanUtils.copyProperties(message,messageToOne);
            messageToOne.setToOne(memberId);
            //利用RocketMQ发送异步消息 todo 为了测试先注释掉
//            if (post != null) {producer.asyncSend(WebSocketConfig.websocketPost,messageToOne);}

            //*****下面代码只是方便测试：给自己发一个短信:*****
            messageToOne.setToOne(message.getSender());//把id改为当前用户
            sendMessageByUserId(messageToOne);
            ctx.writeAndFlush("发送成功");
            //*****上面代码只是方便测试：给自己发一个短信 *****
        }
        //3.对信息包含的群聊的所有成员的未读群聊表进行更新（已经登录的人员会在断开连接时清除）
        for (String memberId : memberIds) {
            if (message.getChat() != null) {
                chatRedis.addChatIntoUnRead(message.getChat(), memberId);
            }
        }

        super.channelRead(ctx, msg);
    }

    public static int sendMessageByUserId(MessageToOne messageToOne){
        //如果发现当前hashmap里有目标
        if (channels.containsKey(messageToOne.getToOne())){
            Message message=new Message();
            BeanUtils.copyProperties(messageToOne,message);
            channels.get(messageToOne.getToOne()).writeAndFlush(message);
            return 1;
        }
        return 0;//发送失败,可能是目标刚好下线了
    }

}
