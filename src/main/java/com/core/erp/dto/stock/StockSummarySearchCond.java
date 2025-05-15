package com.core.erp.dto.stock;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
@ToString
public class StockSummarySearchCond {

    private Integer categoryId;       // 카테고리 ID (null 허용)
    private String productName;       // 상품명 검색 (부분일치)

    @NumberFormat
    private String barcode;           // 바코드는 문자열 유지 (0으로 시작하는 경우를 고려)

    private int page = 0;             // 기본 페이지
    private int size = 10;            // 기본 페이지 크기

    public boolean hasSearchKeyword() {
        return (productName != null && !productName.trim().isEmpty()) ||
                (barcode != null && !barcode.trim().isEmpty());
    }
}
