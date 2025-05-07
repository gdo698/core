package com.core.erp.service;

import com.core.erp.domain.EmailVerificationEntity;
import com.core.erp.repository.EmailVerificationRepository;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmployeeRepository employeeRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 랜덤 인증 코드 생성 (6자리 숫자)
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    // 인증 이메일 발송
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        String subject = "CORE ERP 회원가입 이메일 인증";
        String text = "안녕하세요, CORE ERP 회원가입을 위한 인증 코드입니다.\n\n" +
                "인증 코드: " + verificationCode + "\n\n" +
                "이 코드는 30분 동안 유효합니다.\n" +
                "코드를 입력하여 이메일 인증을 완료해주세요.";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패: " + e.getMessage());
        }
    }

    // 이메일 인증 코드 저장 및 이메일 발송
    @Transactional
    public void createAndSendVerificationCode(String email) {
        // 이미 가입된 회원인지 확인
        if (employeeRepository.findByLoginId(email).isPresent()) {
            throw new IllegalStateException("이미 가입된 이메일입니다");
        }

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // 기존 인증 코드 삭제
        emailVerificationRepository.deleteByEmail(email);

        // 새 인증 코드 저장
        EmailVerificationEntity verification = new EmailVerificationEntity();
        verification.setEmail(email);
        verification.setVerificationCode(verificationCode);
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 30분 후 만료
        verification.setVerified(false);
        emailVerificationRepository.save(verification);

        // 이메일 발송
        sendVerificationEmail(email, verificationCode);
    }

    // 인증 코드 확인
    @Transactional
    public boolean verifyEmail(String email, String code) {
        Optional<EmailVerificationEntity> verificationOpt =
                emailVerificationRepository.findByEmailAndVerificationCode(email, code);

        if (verificationOpt.isPresent()) {
            EmailVerificationEntity verification = verificationOpt.get();

            // 만료 시간 확인
            if (verification.getExpiryDate().isAfter(LocalDateTime.now())) {
                // 인증 완료 처리
                verification.setVerified(true);
                emailVerificationRepository.save(verification);
                return true;
            }
        }

        return false;
    }

    // 이메일 인증 상태 확인
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmailAndVerified(email, true).isPresent();
    }
}