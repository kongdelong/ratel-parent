package com.ratel.modules.jsth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Setter
@Getter
@Document(collection = "userAccount")
public class MongoUserAccountDomain extends BaseMongoDocument {


    @Field
    private Long jf;

    @Field
    private Long jb;

    @Field
    private String bizz;//业务类型

    @Field
    private String type;//jf jb

    @Field
    private String date;

    @Field
    private String adId;
}
