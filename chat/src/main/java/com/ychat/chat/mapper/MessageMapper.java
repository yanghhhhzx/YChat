package com.ychat.chat.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ychat.chat.domain.Message;
import org.apache.ibatis.annotations.Insert;

import java.util.Date;
import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MessageMapper extends BaseMapper<Message> {

    List<Message> selectMessageByChatIdAndTime(List<Long> chatIds, Date time);

    @Insert("insert into ychat_message_0.message_0 ( content, send_time, sender, chat, type, ttl ) VALUES ( #{content}, #{sendTime}, #{sender}, #{chat}, #{type}, #{ttl} )")
    void addMessage(Message message);
}
