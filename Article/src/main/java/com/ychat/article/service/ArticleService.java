package com.ychat.article.service;

import com.ychat.article.domain.dto.ArticleDTO;
import com.ychat.article.domain.po.Article;

import java.util.List;

public interface ArticleService {

    public void save(ArticleDTO article) ;

    public void delete(ArticleDTO article) ;

    public List<Article> findByUserId(String userId) ;

}
