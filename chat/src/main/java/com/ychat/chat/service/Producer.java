package com.ychat.chat.service;

import com.ychat.chat.domain.Message;
import com.ychat.chat.domain.MessageToOne;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送方
 */
@Component
public class Producer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 普通字符串消息
     */
    public void sendMessage(String topic,String message) {
        rocketMQTemplate.convertAndSend(topic,message);
    }

    /**
     * 同步消息
     */
    public void syncSend(String topic,String message) {
        SendResult sendMessage = rocketMQTemplate.syncSend(topic,message);
    }

    /**
     * 异步消息
     * topic是port
     */
    public void asyncSend(String topic, MessageToOne message) {

        SendCallback callback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送失败："+topic+":"+message);
            }
        };
        //**************下面这才是发送消息，上面那是异常处理
        rocketMQTemplate.asyncSend(topic, message, callback);
    }

    /**
     * 单向消息
     */
    public void onewaySend(String topic,String message) {
        rocketMQTemplate.sendOneWay(topic, message);
    }
}

