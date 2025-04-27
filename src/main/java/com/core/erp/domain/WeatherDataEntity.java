package com.core.erp.domain;

import com.core.erp.dto.WeatherDataDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WeatherDataEntity {

    @Id
    @Column(name = "weather_id")
    private int weatherId;

    @Column(name = "wt_location", nullable = false, length = 100)
    private String wtLocation;

    @Column(name = "wt_date", nullable = false)
    private LocalDate wtDate;

    @Column(name = "wt_temperature", nullable = false, precision = 5, scale = 2)
    private Double wtTemperature;

    @Column(name = "wt_condition", nullable = false, length = 50)
    private String wtCondition;

    @Column(name = "wt_humidity")
    private Integer wtHumidity;

    @Column(name = "wt_precipitation", precision = 5, scale = 2)
    private Double wtPrecipitation;

    @Column(name = "wt_created_at")
    private LocalDateTime wtCreatedAt;

    // DTO → Entity 변환 생성자
    public WeatherDataEntity(WeatherDataDTO dto) {
        this.weatherId = dto.getWeatherId();
        this.wtLocation = dto.getWtLocation();
        this.wtDate = dto.getWtDate();
        this.wtTemperature = dto.getWtTemperature();
        this.wtCondition = dto.getWtCondition();
        this.wtHumidity = dto.getWtHumidity();
        this.wtPrecipitation = dto.getWtPrecipitation();
        this.wtCreatedAt = dto.getWtCreatedAt();
    }
}