package com.ychat.article.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//这个其实目前没啥必要，可以完全用article代替，之前是以为mongoDB不能内嵌list才创建的它
public class ArticleDTO {

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
