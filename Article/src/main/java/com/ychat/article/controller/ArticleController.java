package com.ychat.article.controller;

import com.ychat.article.domain.dto.ArticleDTO;
import com.ychat.article.domain.po.Article;
import com.ychat.article.service.ArticleService;
import com.ychat.file.service.FileStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Api(tags = "文章相关接口")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;

    private final FileStorageService fileStorageService;
    @ApiOperation("创建动态接口")
    @PostMapping(value = "insert")
    public int insert(@RequestBody @Validated ArticleDTO articleDTO) {

        articleService.save(articleDTO);

        return 1;
    }

    @ApiOperation("上传图片接口")
    @PostMapping("/upload")
    public String uoloadImg(String prefix, MultipartFile file) throws IOException {
//        获取input流
        InputStream inputStream= file.getInputStream();
//        生成uuid文件名
        String filename = file.getOriginalFilename();
        String fileExName = null;
        if (filename != null) {
            fileExName = filename.substring(filename.lastIndexOf("."));
        }
        filename = UUID.randomUUID() + fileExName;
//文件前缀是用来对文件进行划分整理的。
        return fileStorageService.uploadImgFile(prefix, filename, inputStream);
    }

}
