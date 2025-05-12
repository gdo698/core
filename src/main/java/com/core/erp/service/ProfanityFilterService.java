package com.core.erp.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ProfanityFilterService {

    // 기본 욕설 목록 (필요에 따라 확장 가능)
    private Set<String> profanityWords = new HashSet<>(Arrays.asList(
        "개새끼", "병신", "씨발", "좆", "지랄", "염병", "썅", "꺼져", "새끼", "시발", 
        "미친", "엿먹어", "개자식", "쓰레기", "바보", "멍청이", "등신", "찐따"
    ));
    
    /**
     * 텍스트에 욕설이 포함되어 있는지 확인
     * @param text 검사할 텍스트
     * @return 욕설 포함 여부
     */
    public boolean containsProfanity(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        String normalizedText = normalizeText(text);
        
        // 기본 욕설 단어 검사
        for (String word : profanityWords) {
            if (normalizedText.contains(word)) {
                return true;
            }
        }
        
        // 비슷한 패턴이나 변형된 욕설도 검사 (예: ㅅㅂ, ㅄ 등)
        if (containsObfuscatedProfanity(normalizedText)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 욕설 탐지 메시지 반환
     * @param text 검사할 텍스트
     * @return 욕설 포함 시 해당 메시지, 없으면 null
     */
    public String validateText(String text) {
        if (containsProfanity(text)) {
            return "부적절한 표현이 포함되어 있습니다. 다시 작성해주세요.";
        }
        return null;
    }
    
    // 텍스트 정규화 (공백 제거, 소문자 변환 등)
    private String normalizeText(String text) {
        return text.replace(" ", "").toLowerCase();
    }
    
    // 변형된 욕설 패턴 검사 (초성 등)
    private boolean containsObfuscatedProfanity(String text) {
        // ㅅㅂ, ㅄ 등의 초성 욕설 패턴
        Pattern initialPattern = Pattern.compile("ㅅㅂ|ㅂㅅ|ㅄ|ㅆㅂ|ㅈㄹ|ㅅㅂㄴ|ㄷㅊ");
        return initialPattern.matcher(text).find();
    }
} 