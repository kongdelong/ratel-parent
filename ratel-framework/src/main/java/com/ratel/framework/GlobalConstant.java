package com.ratel.framework;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 静态定义
 */
public class GlobalConstant {

    public static final String ROOT_VALUE = "root";
    public static final String DEFAULT_VALUE = "default";
    public static final String NONE_VALUE = "none";
    public static final String STATUS_VALUE = "1";
    public static final String DEL_FLAG_VALUE = "0"; // 已经删除

    public static final String API_MAPPING_PREFIX = "/api";

    public static final Map<Boolean, String> booleanLabelMap = new LinkedHashMap();

    static {
        booleanLabelMap.put(Boolean.TRUE, "是");
        booleanLabelMap.put(Boolean.FALSE, "否");
    }

    public static boolean isDevMode() {
        return false;
    }

    //@MetaData("类似OAuth的APP认证AccessToken请求Header名称")
    public final static String APP_AUTH_ACCESS_TOKEN = "Access-Token";

    //@MetaData("标识APP端语言选项，形如en-US|zh-CN|zh-TW|ja-JP等，用于服务端必要的国际化处理")
    public final static String APP_LOCALE = "App-Locale";

    //@MetaData("ConfigProperty:是否全局禁用开放手机号短信发送功能")
    public final static String CFG_SMS_DISABLED = "sms.disabled";

    //@MetaData(value = "ConfigProperty:系统管理员邮箱地址列表", comments = "主要用于接收一些系统层面的重要通知、异常等邮件")
    public final static String CFG_SYSTEM_EMAILS = "system.emails";

    //@MetaData("ConfigProperty:静态资源访问URI前缀列表，多个以逗号分隔")
    public static final String CFG_UPLOAD_PUBLIC_RESOURCE_URI = "upload.public.resource.uri";

    //@MetaData("DataDict:消息类型")
    public final static String DATADICT_MESSAGE_TYPE = "MESSAGE_TYPE";

    /**
     * 兼容MySQL的最大时间值。直接用LocalDateTime.MAX会导致MySQL存储异常。
     */
    public final static LocalDateTime MAX_LOCAL_DATE_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    /**
     * 兼容MySQL的最大日期值。直接用LocalDate.MAX会导致MySQL存储异常。
     */
    public final static LocalDate MAX_LOCAL_DATE = LocalDate.of(9999, 12, 31);


    public static final String RESET_PASS = "重置密码";

    public static final String RESET_MAIL = "重置邮箱";

    /**
     * 用于IP定位转换
     */
    public static final String REGION = "内网IP|内网IP";
}
