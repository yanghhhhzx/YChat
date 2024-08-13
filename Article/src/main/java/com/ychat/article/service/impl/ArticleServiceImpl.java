package com.ychat.article.service.impl;

import com.ychat.article.domain.dto.ArticleDTO;
import com.ychat.article.domain.po.Article;
import com.ychat.article.service.ArticleService;
import com.ychat.common.utils.SnowFlake;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleServiceImpl implements ArticleService {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     *     mongoDB可以直接内嵌list集合，不用再关联查询啦。
     */
    public void save(ArticleDTO articleDTO) {
        //复制过去
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        //获取一个雪花id
        SnowFlake snowFlake=new SnowFlake(1,1,1);
        article.setId(snowFlake.nextId());
        mongoTemplate.insert(articleDTO,"ychat_article");
    }

    public void delete(ArticleDTO articleDTO) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(articleDTO.getId()));
        mongoTemplate.remove(query, Article.class, "ychat_article");
    }

    public List<Article> findByUserId(String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Article.class, "ychat_article");
    }

}
