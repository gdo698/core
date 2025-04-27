package com.core.erp.dto;

import com.core.erp.domain.WeatherDataEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WeatherDataDTO {

    private int weatherId;
    private String wtLocation;
    private LocalDate wtDate;
    private Double wtTemperature;
    private String wtCondition;
    private Integer wtHumidity;
    private Double wtPrecipitation;
    private LocalDateTime wtCreatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public WeatherDataDTO(WeatherDataEntity entity) {
        this.weatherId = entity.getWeatherId();
        this.wtLocation = entity.getWtLocation();
        this.wtDate = entity.getWtDate();
        this.wtTemperature = entity.getWtTemperature();
        this.wtCondition = entity.getWtCondition();
        this.wtHumidity = entity.getWtHumidity();
        this.wtPrecipitation = entity.getWtPrecipitation();
        this.wtCreatedAt = entity.getWtCreatedAt();
    }
}