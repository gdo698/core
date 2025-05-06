package com.core.erp.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalaryDetailDTO {

    private String payDate;
    private Integer month;
    private Integer baseSalary;
    private Integer bonus;
    private Integer deductTotal;
    private Integer netSalary;

    private Integer totalBonus;
    private Integer totalDeduct;
    private Integer totalNetSalary;

    // ✅ LocalDate 기반 생성자 (월별 상세 보기용)
    public SalaryDetailDTO(LocalDate localDate, int baseSalary, int bonus, int deductTotal, int netSalary) {
        this.payDate = localDate.toString();
        this.month = localDate.getMonthValue();
        this.baseSalary = baseSalary;
        this.bonus = bonus;
        this.deductTotal = deductTotal;
        this.netSalary = netSalary;
    }

    // ✅ 연도별 누적용 (optional)
    public SalaryDetailDTO(Integer month, Integer totalBonus, Integer totalDeduct, Integer totalNetSalary) {
        this.month = month;
        this.totalBonus = totalBonus;
        this.totalDeduct = totalDeduct;
        this.totalNetSalary = totalNetSalary;
    }
}
