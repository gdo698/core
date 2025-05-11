package com.core.erp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${openweathermap.api.key:defaultapikey}")
    private String apiKey;
    
    private final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    
    /**
     * 현재 날씨 상태 조회
     * @param lat 위도
     * @param lon 경도
     * @return 날씨 상태 (Clear, Rain, Snow 등)
     */
    public String getWeatherCondition(double lat, double lon) {
        try {
            String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric&lang=kr", 
                                     API_URL, lat, lon, apiKey);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            List<Map<String, Object>> weather = (List<Map<String, Object>>) response.getBody().get("weather");
            
            // 날씨 상태 반환 (예: Clear, Rain, Snow 등)
            return (String) weather.get(0).get("main");
        } catch (Exception e) {
            log.error("날씨 정보 조회 실패", e);
            return "Unknown";
        }
    }
    
    /**
     * 날씨 상태를 한글로 변환
     * @param weatherCondition OpenWeatherMap API에서 반환된 날씨 상태
     * @return 한글로 변환된 날씨 상태
     */
    public String translateWeatherToKorean(String weatherCondition) {
        Map<String, String> translationMap = new HashMap<>();
        translationMap.put("Clear", "맑음");
        translationMap.put("Clouds", "구름");
        translationMap.put("Rain", "비");
        translationMap.put("Drizzle", "이슬비");
        translationMap.put("Thunderstorm", "천둥번개");
        translationMap.put("Snow", "눈");
        translationMap.put("Mist", "안개");
        translationMap.put("Smoke", "연기");
        translationMap.put("Haze", "실안개");
        translationMap.put("Dust", "먼지");
        translationMap.put("Fog", "안개");
        translationMap.put("Sand", "모래");
        translationMap.put("Ash", "화산재");
        translationMap.put("Squall", "돌풍");
        translationMap.put("Tornado", "토네이도");
        
        return translationMap.getOrDefault(weatherCondition, "기타");
    }
} 