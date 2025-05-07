package com.core.barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BarcodeProductResponse {

    @JsonProperty("C005") // 서비스 ID
    private BarcodeProductWrapper wrapper;
}
