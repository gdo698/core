package com.core.barcode.controller;

import com.core.barcode.dto.BarcodeProductDTO;
import com.core.barcode.service.BarcodeApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeApiController {

    private final BarcodeApiService barcodeApiService;

    @GetMapping
    public ResponseEntity<?> getProductByBarcode(@RequestParam String code) {
        BarcodeProductDTO dto  = barcodeApiService.getBarcodeProduct(code);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

}
