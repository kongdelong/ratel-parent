package com.ratel.framework.exception;

public class EntityExistException extends RuntimeException {

    public EntityExistException(Class clazz, String field, String val) {
        super(EntityExistException.generateMessage(field, val));
    }

    private static String generateMessage(String field, String val) {
        return field + ": " + val + " 已经存在";
    }
}
