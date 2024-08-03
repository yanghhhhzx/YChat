package com.ychat.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ychat.chat.domain.Chat;

public interface ChatService extends IService<Chat> {

    public void addChat(Chat chat);
}
