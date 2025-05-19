package com.core.erp.dto.partTimer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyDeviceDTO {
    private String phone;       // 아르바이트 전화번호
    private String code;        // 인증번호
    private String deviceId;    // 기기 UUID
    private String deviceName;  // 기기명
}