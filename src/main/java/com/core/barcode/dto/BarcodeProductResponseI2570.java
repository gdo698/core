package com.core.barcode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BarcodeProductResponseI2570 {

    @JsonProperty("I2570")
    private I2570Wrapper body;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class I2570Wrapper {
        @JsonProperty("row")
        private List<BarcodeProductDTO> items;
    }
}
