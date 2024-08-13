package com.ychat.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.chat.domain.Chat;
import com.ychat.chat.mapper.ChatMapper;
import com.ychat.chat.service.ChatService;
import com.ychat.common.utils.SnowFlake;
import com.ychat.common.utils.transition.Transition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {
    private final ChatMapper chatMapper;

    @Override
    public void addChat(Chat chat) {
        SnowFlake snowFlake=new SnowFlake(1,1,1);
        chat.setId(snowFlake.nextId());
        save(chat);
    }

    @Override
    public void addChatMembers(Chat chat) {
        String addMembers=chat.getMember();

        QueryWrapper<Chat> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("id", chat.getId());
        Chat existChat=baseMapper.selectOne(queryWrapper);
        String existChatMember=existChat.getMember();
        Set<String> addSet=Transition.StringToSet(addMembers);
        Set<String> existSet=Transition.StringToSet(existChatMember);
        Set<String> unionSet = new HashSet<>(addSet);
        unionSet.addAll(existSet);

        //更新数据库
        existChat.setMember(unionSet.toString());
        saveOrUpdate(existChat);
    }

}

