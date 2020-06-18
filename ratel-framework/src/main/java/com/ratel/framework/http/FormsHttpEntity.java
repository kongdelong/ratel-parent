package com.ratel.framework.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * 前台返回 同一格式
 */
@Setter
@Getter
public class FormsHttpEntity {
    private Object content;
    private Integer errCode;
    private String errMsg;
    private Boolean success = true;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;


    public FormsHttpEntity() {
        this.timestamp = LocalDateTime.now();
    }

    public FormsHttpEntity(Object content) {
        this.content = content;
        this.errCode = 0;
        this.timestamp = LocalDateTime.now();
    }

    public FormsHttpEntity(Object content, Integer errCode, String errMsg) {
        this.content = content;
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.timestamp = LocalDateTime.now();
        if (this.errCode != 0) {
            this.success = false;
        }
    }

    public static ResponseEntity ok() {
        return new ResponseEntity(new FormsHttpEntity("操作成功"), HttpStatus.OK);
    }

    public static ResponseEntity ok(Object content) {
        return new ResponseEntity(new FormsHttpEntity(content), HttpStatus.OK);
    }

    public static ResponseEntity error(Object content, Integer errCode, String errMsg) {

        return new ResponseEntity(new FormsHttpEntity(content, errCode, errMsg), HttpStatus.OK);
    }

    public static ResponseEntity error(Object content, Integer errCode, String errMsg, HttpStatus httpStatus) {
        return new ResponseEntity(new FormsHttpEntity(content, errCode, errMsg), httpStatus);
    }
}
