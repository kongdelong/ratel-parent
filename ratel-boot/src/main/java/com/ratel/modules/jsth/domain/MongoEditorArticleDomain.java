package com.ratel.modules.jsth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@Document(collection = "editorArticle")
public class MongoEditorArticleDomain extends BaseMongoDocument {

    @Field
    private String tags;
    @Field
    private String title;
    @Field
    private String comments;//描述
    @Field
    private String content;
    @Field
    private String date;
    @Field
    private Long viewCount;
    @Field
    private Long likeNum;
    @Field
    private String listImg;//封面图片
    @Field
    private String parent;//封面图片
    @Field
    private String level;
    @Field
    private Integer order;

}
