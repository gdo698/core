package com.core.barcode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BarcodeProductWrapper {

    @JsonProperty("row")
    private List<BarcodeProductDTO> products;
}
