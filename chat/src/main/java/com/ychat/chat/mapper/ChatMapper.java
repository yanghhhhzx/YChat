package com.ychat.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ychat.chat.domain.Chat;
import org.apache.ibatis.annotations.Insert;

public interface ChatMapper  extends BaseMapper<Chat> {

    @Insert("insert into chat(id,owner_name,title,member) values (#{id},#{owner_name},#{title},#{member})")
    void addChat(Chat chat);

}
