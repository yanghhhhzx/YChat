package com.ychat.chat.domain;

import com.ychat.common.utils.transition.Transition;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String content;
    private String sendTime;
    private String sender;      //发送者id
    private Long chat;          //归属群聊
    private Integer tag;        //用来标记是群聊消息还是单聊消息？或者用来做其他的标记？？
    private int type;           //类型（图片、视频、信息等）
    private int ttl;

    public static Message getMessageByMsg(String sender,TextWebSocketFrame msg) {
        //获取msg中数据并分词
        String[] a = msg.text().split(",", 2);
        Long chatId = null;
        chatId = Long.parseLong(a[0]);
        //用于测试 chatId = Long.parseLong("13015322521112576");
        if (chatId == null) {
            System.out.println("无法创建message");
        }
        //确保前端发送过来的消息能被划分为2个部分
        //0是群聊，1是内容
        String content = a[1];
        Date now = new Date();
        String dateString= Transition.DateToString(now);
        Message message = Message.builder()
                .content(content)
                .sender(sender)
                .chat(chatId)
                .sendTime(dateString)
                .build();

        return message;
    }
}
