package com.relly.blog.mapper;

import com.relly.blog.entity.ArticleMessageEntity;

public interface ArticleMessageMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table article_message
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table article_message
     *
     * @mbg.generated
     */
    int insert(ArticleMessageEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table article_message
     *
     * @mbg.generated
     */
    int insertSelective(ArticleMessageEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table article_message
     *
     * @mbg.generated
     */
    ArticleMessageEntity selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table article_message
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ArticleMessageEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table article_message
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ArticleMessageEntity record);
}