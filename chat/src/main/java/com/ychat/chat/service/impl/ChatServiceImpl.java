package com.ychat.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.chat.domain.Chat;
import com.ychat.chat.mapper.ChatMapper;
import com.ychat.chat.service.ChatService;
import com.ychat.common.utils.SnowFlake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {


    @Override
    public void addChat(Chat chat) {
        SnowFlake snowFlake=new SnowFlake(1,1,1);
        chat.setId(snowFlake.nextId());
        save(chat);
    }

}

