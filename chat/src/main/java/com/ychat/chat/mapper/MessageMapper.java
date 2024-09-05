package com.ychat.chat.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ychat.chat.domain.Message;
import org.apache.ibatis.annotations.Insert;

import java.util.Date;
import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MessageMapper extends BaseMapper<Message> {

    List<Message> selectMessageByChatIdAndTime(List<Long> chatIds, Date time);

    @Insert("insert into message ( id,content, send_time, sender, chat, type) VALUES ( #{id},#{content}, #{sendTime}, #{sender}, #{chat}, #{type} )")
    void addMessage(Message message);
}
