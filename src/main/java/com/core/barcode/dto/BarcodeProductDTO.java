package com.core.barcode.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BarcodeProductDTO {

    @JsonProperty("PRDLST_NM")
    private String productName;

    @JsonProperty("BSSH_NM")
    private String manufacturer;

    @JsonProperty("BRCD_NO")
    private String barcode;

    @JsonProperty("PRDLST_DCNM")
    private String category;

    @JsonProperty("POG_DAYCNT")
    private String expirationInfo;

    private Integer price;

    private Integer productId;

    private Integer isPromo;

}
