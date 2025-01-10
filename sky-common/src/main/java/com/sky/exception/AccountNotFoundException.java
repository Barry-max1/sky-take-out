package com.sky.exception;

/**
 * 账号不存在异常
 */
public class AccountNotFoundException extends BaseException {

    public AccountNotFoundException() {
    }

    //构造器
    public AccountNotFoundException(String msg) {
        super(msg);
    }

}
