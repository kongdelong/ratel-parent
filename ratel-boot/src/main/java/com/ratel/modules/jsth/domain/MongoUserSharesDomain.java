package com.ratel.modules.jsth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Setter
@Getter
@Document(collection = "usershares")
public class MongoUserSharesDomain extends BaseMongoDocument {

    @Field
    private String nickName;
    @Field
    private String avatarUrl;
    @Field
    private String content;
    @Field
    private Boolean enable;
    @Field
    private List<String> detailPics;
    @Field
    private String date;
}
