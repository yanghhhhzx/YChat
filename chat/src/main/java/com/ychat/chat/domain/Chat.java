package com.ychat.chat.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    private long id;

    @ApiModelProperty(value = "群聊名", required = true)
    @NotNull(message = "群聊名不能为空")
    private String title;

    @ApiModelProperty(value = "成员", required = true)
    @NotNull(message = "成员不能为空")
    private String member;
    private String owner;//群主
}
