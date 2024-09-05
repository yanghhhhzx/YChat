package com.ychat.chat.domain;

import lombok.Data;

//因为再websocket中需要点对点发送,所以引入这个类
@Data
public class MessageToOne {
    private String ToOne;          //要发送给的目标

    private long id;
    private String content;
    private String sendTime;
    private String sender;      //发送者id
    private Long chat;          //归属群聊
    private Integer tag;        //用来标记是群聊消息还是单聊消息？或者用来做其他的标记？？
    private int type;           //类型（图片、视频、信息等）
    private int ttl;
}
