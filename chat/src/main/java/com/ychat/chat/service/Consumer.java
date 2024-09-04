package com.ychat.chat.service;

import com.ychat.chat.domain.MessageToOne;
import com.ychat.chat.websocket.MyWebSocketHandler;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费方
 * 1.如果两个消费者group和topic都一样，则二者轮循接收消息
 * 2.如果两个消费者topic一样，而group不一样，则消息变成广播机制
 * RocketMQListener<>泛型必须和接收的消息类型相同
 */
@Component
@RocketMQMessageListener(
        //todo 要根据监听当前实例的端口改
        topic = "8866",                           // 1.topic：消息的发送者使用同一个topic
        consumerGroup = "group1",                 // 2.group：这东西无所谓,不用和生产者group相同,消息订阅全看topic,(在RocketMQ中消费者和发送者组没有关系,)
        selectorExpression = "*",                 // 3.tag：设置为 * 时，表示全部。
        messageModel = MessageModel.CLUSTERING    // 4.消费模式：默认 CLUSTERING （ CLUSTERING：负载均衡 ）（ BROADCASTING：广播机制 ）
)
public class Consumer implements RocketMQListener<MessageToOne> {

    //这个消费者只是用来发送消息的
    @Override
    public void onMessage(MessageToOne messageToOne) {
        try {
            // 睡眠五十毫秒，确保消息成功接收（演示专用，勿喷）
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (messageToOne != null) {
            if (MyWebSocketHandler.sendMessageByUserId(messageToOne)==0){
                System.out.println("目标对象已经下线");
                //无线返回异常,因为消息无论如何都会保存到数据库
            }
        }
    }
}
