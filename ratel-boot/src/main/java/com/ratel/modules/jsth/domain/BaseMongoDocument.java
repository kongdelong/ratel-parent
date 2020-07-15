package com.ratel.modules.jsth.domain;

import com.alibaba.fastjson.JSONObject;
import com.ratel.modules.jsth.annotation.AutoIncKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public abstract class BaseMongoDocument implements Serializable {

    @AutoIncKey
    @Id
    protected String id;

    @Field
    private String openId;

    @Field
    protected String creatUserId;
    @Field
    protected String creatUserName;
    @Field
    protected String updateUserId;
    @Field
    protected String updateUserName;
    @Field
    protected String status;//狀態
    @Field
    protected Date createTime;
    @Field
    protected Date updateTime;

    @Field
    private JSONObject extData = new JSONObject();

    @Field
    protected String busiKey;

    public Object get(String key) {
        return extData.get(key);
    }

    public void put(String key, Object value) {
        extData.put(key, value);
    }
}
