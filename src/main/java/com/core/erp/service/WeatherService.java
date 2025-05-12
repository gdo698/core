package com.core.erp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WeatherService {
    private final RestTemplate restTemplate;
    
    @Value("${openweathermap.api.key:defaultapikey}")
    private String openWeatherMapApiKey;
    
    @Value("${data.go.kr.api.key:defaultapikey}")
    private String dataGoKrApiKey;
    
    private final String OPENWEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather";
    // 기상청 종관기상관측(ASOS) 일자료 조회서비스 API 주소
    private final String KOREA_WEATHER_API_URL = "http://apis.data.go.kr/1360000/AsosDalyInfoService/getWthrDataList";
    
    // 날씨 데이터 캐싱을 위한 Map (날짜 -> 날씨)
    private final Map<String, String> weatherCache = new ConcurrentHashMap<>();
    
    // 서울 관측소 지점번호 (108: 서울)
    private final String DEFAULT_STN_ID = "108";
    
    // 월별 기후 평년값 기반 날씨 추정 (1991-2020 평년값 기준)
    // 월별 강수일수와 운량을 기반으로 날씨를 추정함
    private final Map<Month, WeatherStats> monthlyWeatherStats = new HashMap<>();
    
    @Autowired
    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        
        // 월별 통계 데이터 초기화 (서울 기준)
        // 데이터 출처: 기상청 기후평년값(1991-2020)
        // 강수일수가 많고 평균 전운량이 많으면 흐림, 강수일수가 많고 평균 전운량이 적으면 비, 강수일수가 적고 평균 전운량이 많으면 구름많음, 강수일수가 적고 평균 전운량이 적으면 맑음
        monthlyWeatherStats.put(Month.JANUARY, new WeatherStats(6.9, 3.4, 0.0)); // 1월
        monthlyWeatherStats.put(Month.FEBRUARY, new WeatherStats(5.5, 3.6, 0.0)); // 2월
        monthlyWeatherStats.put(Month.MARCH, new WeatherStats(7.1, 4.4, 0.0));    // 3월
        monthlyWeatherStats.put(Month.APRIL, new WeatherStats(8.2, 4.6, 0.0));    // 4월
        monthlyWeatherStats.put(Month.MAY, new WeatherStats(8.0, 4.9, 0.0));      // 5월
        monthlyWeatherStats.put(Month.JUNE, new WeatherStats(9.5, 5.6, 0.0));     // 6월
        monthlyWeatherStats.put(Month.JULY, new WeatherStats(16.2, 6.5, 0.0));    // 7월
        monthlyWeatherStats.put(Month.AUGUST, new WeatherStats(14.1, 5.7, 0.0));  // 8월
        monthlyWeatherStats.put(Month.SEPTEMBER, new WeatherStats(8.8, 4.8, 0.0)); // 9월
        monthlyWeatherStats.put(Month.OCTOBER, new WeatherStats(6.6, 3.9, 0.0));  // 10월
        monthlyWeatherStats.put(Month.NOVEMBER, new WeatherStats(7.2, 3.6, 0.0)); // 11월
        monthlyWeatherStats.put(Month.DECEMBER, new WeatherStats(6.8, 3.3, 0.0)); // 12월
    }
    
    /**
     * 현재 날씨 상태 조회 (OpenWeatherMap)
     * @param lat 위도
     * @param lon 경도
     * @return 날씨 상태 (Clear, Rain, Snow 등)
     */
    public String getWeatherCondition(double lat, double lon) {
        try {
            String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric&lang=kr", 
                                     OPENWEATHER_API_URL, lat, lon, openWeatherMapApiKey);
            
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
     * 특정 날짜의 날씨 조회 (기상청 ASOS 일자료 API)
     * @param date 조회할 날짜
     * @param stnId 지점번호 (null이면 서울 기본값)
     * @return 날씨 상태 (한글)
     */
    public String getWeatherForDate(LocalDateTime date, String stnId, String unused) {
        String dateKey = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 캐시된 날씨 데이터가 있으면 반환
        if (weatherCache.containsKey(dateKey)) {
            return weatherCache.get(dateKey);
        }
        
        try {
            // 날짜를 기상청 API 포맷으로 변환
            String targetDate = dateKey;
            
            // 지점번호가 없으면 기본값 사용 (서울)
            if (stnId == null) {
                stnId = DEFAULT_STN_ID;
            }
            
            // 현재 날짜보다 미래인 경우 현재 날짜로 대체
            LocalDate today = LocalDate.now();
            LocalDate queryDate = date.toLocalDate();
            
            if (queryDate.isAfter(today)) {
                targetDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                log.warn("미래 날짜({})의 날씨를 요청하여 현재 날짜({})로 대체합니다.", dateKey, targetDate);
            } 
            
            // 조회 시점으로부터 3년 이상 지난 과거 날짜인 경우 통계 기반 추정치 사용
            if (queryDate.isBefore(today.minusYears(3))) {
                String estimatedWeather = estimateWeatherFromStatistics(queryDate);
                log.info("과거 날짜({})의 날씨를 통계 기반으로 추정: {}", dateKey, estimatedWeather);
                weatherCache.put(dateKey, estimatedWeather);
                return estimatedWeather;
            }
            
            // API 요청 파라미터 구성
            String url = UriComponentsBuilder.fromHttpUrl(KOREA_WEATHER_API_URL)
                    .queryParam("serviceKey", dataGoKrApiKey)
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "10")
                    .queryParam("dataType", "XML")  // XML로 응답 받기
                    .queryParam("dataCd", "ASOS")
                    .queryParam("dateCd", "DAY")
                    .queryParam("startDt", targetDate)
                    .queryParam("endDt", targetDate)
                    .queryParam("stnIds", stnId)
                    .build()
                    .toUriString();
            
            log.info("API 요청 URL: {}", url);
            
            try {
                // XML 응답 직접 받아오기
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                String xmlResponse = response.getBody();
                
                if (xmlResponse == null || xmlResponse.isEmpty()) {
                    log.warn("기상청 API 응답이 없거나 비어있습니다.");
                    String estimatedWeather = estimateWeatherFromStatistics(queryDate);
                    weatherCache.put(dateKey, estimatedWeather);
                    return estimatedWeather;
                }
                
                // XML 파싱
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
                doc.getDocumentElement().normalize();
                
                // 응답 파싱
                NodeList itemList = doc.getElementsByTagName("item");
                
                if (itemList.getLength() == 0) {
                    log.warn("해당 날짜({})에 대한 날씨 데이터가 없습니다.", targetDate);
                    String estimatedWeather = estimateWeatherFromStatistics(queryDate);
                    weatherCache.put(dateKey, estimatedWeather);
                    return estimatedWeather;
                }
                
                // 첫 번째 아이템 가져오기
                Node itemNode = itemList.item(0);
                Element item = (Element) itemNode;
                
                // 기상청 데이터 추출
                double rainAmount = getDoubleValueFromElement(item, "sumRn", 0);
                double snowAmount = getDoubleValueFromElement(item, "ddMes", 0);
                String weatherCondition;
                
                if (snowAmount > 0) {
                    weatherCondition = "눈";
                } else if (rainAmount > 0) {
                    weatherCondition = "비";
                } else {
                    // 평균 전운량(avgTca) 기준으로 맑음/흐림 판단
                    double avgTca = getDoubleValueFromElement(item, "avgTca", 0);
                    if (avgTca < 2) {
                        weatherCondition = "맑음";
                    } else if (avgTca < 6) {
                        weatherCondition = "구름많음";
                    } else {
                        weatherCondition = "흐림";
                    }
                    
                    // 일기현상(iscs) 필드가 있으면 참고
                    String iscs = getStringValueFromElement(item, "iscs", null);
                    if (iscs != null && !iscs.isEmpty()) {
                        if (iscs.contains("안개")) {
                            weatherCondition = "안개";
                        } else if (iscs.contains("황사")) {
                            weatherCondition = "먼지";
                        }
                    }
                }
                
                // 결과를 캐시에 저장
                weatherCache.put(dateKey, weatherCondition);
                log.info("날짜: {}, 날씨: {}", dateKey, weatherCondition);
                
                return weatherCondition;
            } catch (Exception e) {
                log.error("XML 파싱 오류: {}", e.getMessage(), e);
                String estimatedWeather = estimateWeatherFromStatistics(queryDate);
                weatherCache.put(dateKey, estimatedWeather);
                return estimatedWeather;
            }
        } catch (Exception e) {
            log.error("기상청 ASOS API 날씨 정보 조회 실패: {}", e.getMessage(), e);
            String estimatedWeather = estimateWeatherFromStatistics(date.toLocalDate());
            weatherCache.put(dateKey, estimatedWeather);
            return estimatedWeather;
        }
    }
    
    /**
     * 월별 통계 데이터 기반으로 날씨 추정
     * @param date 날짜
     * @return 추정된 날씨 상태
     */
    private String estimateWeatherFromStatistics(LocalDate date) {
        Month month = date.getMonth();
        WeatherStats stats = monthlyWeatherStats.get(month);
        
        if (stats == null) {
            return "맑음"; // 기본값
        }
        
        // 날짜의 일(day)을 기반으로 랜덤성 부여 (같은 날짜면 항상 같은 날씨 추정)
        int dayOfMonth = date.getDayOfMonth();
        
        // 비가 올 확률: 월간 강수일수 / 월의 일수
        double rainProbability = stats.rainyDays / date.lengthOfMonth();
        
        // 눈이 올 확률: 겨울철(11-3월)에 약간의 확률 부여, 그 외에는 0
        double snowProbability = 0;
        if (month == Month.DECEMBER || month == Month.JANUARY || month == Month.FEBRUARY || month == Month.MARCH || month == Month.NOVEMBER) {
            snowProbability = stats.snowyDays / date.lengthOfMonth();
        }
        
        // 일의 숫자를 0-1 사이의 값으로 정규화 (결정론적 랜덤성)
        double normalizedDay = (double) dayOfMonth / date.lengthOfMonth();
        
        // 날씨 결정 로직
        if (normalizedDay < snowProbability) {
            return "눈";
        } else if (normalizedDay < (snowProbability + rainProbability)) {
            return "비";
        } else {
            // 구름 많음/맑음 결정: 평균 전운량 활용
            if (stats.cloudCover > 5.0) {
                return "흐림";
            } else if (stats.cloudCover > 3.0) {
                return "구름많음";
            } else {
                return "맑음";
            }
        }
    }

    /**
     * XML Element에서 double 값 안전하게 가져오기
     */
    private double getDoubleValueFromElement(Element element, String tagName, double defaultValue) {
        try {
            NodeList nodeList = element.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                String value = nodeList.item(0).getTextContent();
                if (value != null && !value.isEmpty()) {
                    return Double.parseDouble(value);
                }
            }
        } catch (Exception e) {
            log.warn("값 변환 실패: {} -> {}", tagName, element.getElementsByTagName(tagName));
        }
        return defaultValue;
    }
    
    /**
     * XML Element에서 String 값 안전하게 가져오기
     */
    private String getStringValueFromElement(Element element, String tagName, String defaultValue) {
        try {
            NodeList nodeList = element.getElementsByTagName(tagName);
            if (nodeList.getLength() > 0) {
                return nodeList.item(0).getTextContent();
            }
        } catch (Exception e) {
            log.warn("값 변환 실패: {}", tagName);
        }
        return defaultValue;
    }
    
    /**
     * Map에서 double 값 안전하게 가져오기
     */
    private double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.get(key);
        if (value == null) {
            return defaultValue;
        }
        
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (Exception e) {
            log.warn("값 변환 실패: {} -> {}", key, value);
        }
        
        return defaultValue;
    }
    
    /**
     * Map에서 String 값 안전하게 가져오기
     */
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
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
    
    /**
     * 월별 기후 통계 데이터를 담는 내부 클래스
     */
    private static class WeatherStats {
        final double rainyDays;    // 월평균 강수일수
        final double cloudCover;   // 월평균 전운량
        final double snowyDays;    // 월평균 적설일수
        
        public WeatherStats(double rainyDays, double cloudCover, double snowyDays) {
            this.rainyDays = rainyDays;
            this.cloudCover = cloudCover;
            this.snowyDays = snowyDays;
        }
    }
} 