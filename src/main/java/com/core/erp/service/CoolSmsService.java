package com.core.erp.service;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CoolSmsService implements SmsService {

    @Value("${sms.apiKey}")
    private String apiKey;

    @Value("${sms.apiSecret}")
    private String apiSecret;

    @Value("${sms.senderPhone}")
    private String senderPhone;

    @Value("${sms.enabled:true}")
    private boolean smsEnabled;

    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();

    private DefaultMessageService messageService;

    private DefaultMessageService getMessageService() {
        if (messageService == null) {
            messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        }
        return messageService;
    }

    @Override
    public void sendVerificationCode(String phone) {
        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        codeStorage.put(phone, code);

        // ✅ 개발 환경이면 발송 생략
        if (!smsEnabled) {
            log.info("🛠 [개발모드] 인증번호 [{}] 발송 생략 → 대상: {}", code, phone);
            return;
        }

        Message message = new Message();
        message.setFrom(senderPhone);
        message.setTo(phone);
        message.setText("[편의점ERP] 인증번호는 [" + code + "] 입니다.");

        try {
            getMessageService().send(message);
            log.info("✅ 인증번호 [{}] 전송 완료 to {}", code, phone);
        } catch (Exception e) {
            log.error("❌ 문자 전송 실패: {}", e.getMessage());
            throw new RuntimeException("SMS 전송 실패", e);
        }
    }

    @Override
    public boolean verify(String phone, String code) {
        String savedCode = codeStorage.get(phone);
        return code != null && code.equals(savedCode);
    }
}