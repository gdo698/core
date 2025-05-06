package com.core.pos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefundRequestDTO {

    private Integer transactionId;
    private String refundReason;

}
