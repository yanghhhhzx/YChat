package com.ychat.article.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    private long id;
    private String headline;
    private String content;
    private String sendTime;
    private String senderId;      //发送者id
    private Integer tag;
    private int type;           //类型
    private List<String> labels; //标签
    private List<String> images; //图片链接
    private List<String> videos; //视频链接

}
