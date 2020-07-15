package com.ratel.modules.jsth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Setter
@Getter
@Document(collection = "users")
public class MongoUserDomain extends BaseMongoDocument {

    @Field
    private Map wxUserInfo;

}
