package com.core.erp.dto.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreInquiryRequestDTO {
    private Integer storeId;
    private String inqPhone;
    private String inqContent;
    private Integer inqType; // 1: 컴플레인, 2: 칭찬, 3: 건의/문의
}