package com.ychat.chat.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ychat.chat.domain.Message;


import java.util.Date;
import java.util.List;

@org.apache.ibatis.annotations.Mapper
public interface MessageMapper extends BaseMapper<Message> {

    List<Message> selectMessageByChatIdAndTime(List<Long> chatIds, Date time);
}
