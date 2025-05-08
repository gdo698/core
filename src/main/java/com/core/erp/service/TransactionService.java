package com.core.erp.service;
import com.core.erp.domain.SalesDetailEntity;
import com.core.erp.domain.SalesTransactionEntity;
import com.core.erp.dto.SalesDetailDTO;
import com.core.erp.dto.SalesTransactionDTO;
import com.core.erp.repository.SalesDetailRepository;
import com.core.erp.repository.SalesTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final SalesTransactionRepository transactionRepository;
    private final SalesDetailRepository detailRepository;

    public List<SalesTransactionDTO> getAllTransactionsByStore(Integer storeId) {
        List<SalesTransactionEntity> transactions =
                transactionRepository.findByStore_StoreIdOrderByPaidAtDesc(storeId);

        return transactions.stream().map(transaction -> {
            SalesTransactionDTO dto = new SalesTransactionDTO(transaction);

            // fetch join으로 상품/카테고리 정보까지 로딩
            List<SalesDetailEntity> detailEntities =
                    detailRepository.findWithProductByTransactionId(transaction.getTransactionId());

            List<SalesDetailDTO> detailDTOs = detailEntities.stream()
                    .map(SalesDetailDTO::new)
                    .toList();

            dto.setDetails(detailDTOs);
            return dto;
        }).toList();
    }
}

