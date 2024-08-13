package com.ychat.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ychat.chat.domain.Chat;

public interface ChatService extends IService<Chat> {

    void addChat(Chat chat);

    void addChatMembers(Chat chat);
}
