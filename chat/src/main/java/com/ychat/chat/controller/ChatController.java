package com.ychat.chat.controller;

import com.ychat.chat.domain.Chat;
import com.ychat.chat.domain.Message;
import com.ychat.chat.service.ChatService;
import com.ychat.chat.websocket.MyWebSocketHandler;
import com.ychat.common.utils.transition.Transition;
import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ychat.api.client.UserClient;

import java.util.List;

import static com.ychat.chat.websocket.MyWebSocketHandler.channels;

@Api(tags = "群聊相关接口")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    //启动类上要加@EnableFeignClients(clients = {UserClient.class})
    private final UserClient userClient;


    @ApiOperation("创建群聊接口")
    @PostMapping(value = "creatChat")
    public int addChat(@RequestBody @Validated Chat chat) {
        List<String> members=Transition.StringToList(chat.getMember());
        int result=userClient.existUsers(members);
        if(result==0){return 0;}
        else {
            chatService.addChat(chat);
            return 1;}
    }

    @ApiOperation("增加群聊成员接口")
    @PostMapping(value = "addChatMembers")
    public int addChatMembers(@RequestBody @Validated Chat chat) {
        List<String> members=Transition.StringToList(chat.getMember());
        int result=userClient.existUsers(members);
        if(result==0){return 0;}
        else {
            chatService.addChatMembers(chat);
            return 1;
        }
    }

    //用于发送信息。
    //todo 测试和修改
//    @PostMapping(value = "sendMessage/{channelId}")
//    public int sendMessageByChannelId(@RequestBody Message message,@PathVariable String channelId ) {
//
////        遍历当前实例的ChannelGroup，找到对应的channel
//        for (ChannelHandlerContext channel : MyWebSocketHandler.channelGroup) {
//            if (channel.channel().id().toString().equals(channelId)) {
//                channel.writeAndFlush(message);
//                return 1;//代表发送消息成功
//            }
//        }
//        return 0;//代表发送消息发送失败，找错了端口
//    }

    //用于发送信息。
    //todo 测试和修改
    @PostMapping(value = "sendMessage/{channelId}")
    public int sendMessageByUserId(@RequestBody Message message,@PathVariable String UserId ) {
        channels.get(UserId).writeAndFlush(message);;
        return 1;
    }


}