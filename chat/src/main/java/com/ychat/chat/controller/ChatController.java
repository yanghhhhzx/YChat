package com.ychat.chat.controller;

import com.ychat.chat.domain.Chat;
import com.ychat.chat.service.ChatService;
import com.ychat.common.utils.transition.Transition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "群聊相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @ApiOperation("创建群聊接口")
    @PostMapping(value = "creatChat")
    public int addChat(@RequestBody @Validated Chat chat) {
        List<String> members=Transition.StringToList(chat.getMember());

        chatService.addChat(chat);
        return 1;
    }
}