package com.ychat.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ychat.chat.domain.Message;

import java.util.List;

public interface MessageService extends IService<Message> {

    void saveMessageIntoRedis(Message message);

    void saveMessageIntoMysql(Message message);

    Message getMessagesFromRedis(String messageId);

    List<Message> getNewMessagesFromMysql(List<Long> ChatIds, String time);
}
