package com.core.erp.service;

public interface SmsService {
    void sendVerificationCode(String phone);
    boolean verify(String phone, String code);
}