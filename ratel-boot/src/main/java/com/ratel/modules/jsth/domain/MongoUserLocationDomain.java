package com.ratel.modules.jsth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Setter
@Getter
@Document(collection = "userLocation")
public class MongoUserLocationDomain  extends BaseMongoDocument {
    @Field
    private String nickName;
    @Field
    private String avatarUrl;
    @Field
    private Double latitude;
    @Field
    private Double longitude;
    @Field
    private String userId;
    @Field
    private String city;
    @Field
    private String country;
    @Field
    private String gender;
    @Field
    private String province;

    @Field
    private Long jf;

    @Field
    private Long jb;

    @Field
    private Map zanMap;

    public String code;
    public String anonymousCode;
    public String appid;
    public String secret;

}
