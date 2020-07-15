package com.ratel.modules.jsth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@Document(collection = "userDk")
public class MongoUserDkDomain extends BaseMongoDocument {

    @Field
    private Float latitude;

    @Field
    private Float longitude;

    @Field
    private String location;

    @Field
    private String bodyLocation;

    @Field
    private String desc;

    @Field
    private String dkDate;

    @Field
    private String type;

}
