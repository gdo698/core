package com.core.erp.controller;

import com.core.erp.dto.sales.SalesTransactionDTO;
import com.core.erp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 거래 ID로 거래 상세 조회
    @GetMapping
    public ResponseEntity<List<SalesTransactionDTO>> getAllByStore(@RequestParam Integer storeId) {
        List<SalesTransactionDTO> result = transactionService.getAllTransactionsByStore(storeId);
        return ResponseEntity.ok(result);
    }
}
