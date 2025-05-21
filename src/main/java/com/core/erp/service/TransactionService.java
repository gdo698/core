package com.core.erp.service;
import com.core.erp.domain.SalesDetailEntity;
import com.core.erp.domain.SalesTransactionEntity;
import com.core.erp.dto.sales.SalesDetailDTO;
import com.core.erp.dto.sales.SalesTransactionDTO;
import com.core.erp.repository.SalesDetailRepository;
import com.core.erp.repository.SalesTransactionRepository;
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
            List<SalesDetailDTO> detailDTOs = detailRepository
                    .findWithProductByTransactionId(transaction.getTransactionId())
                    .stream()
                    .map(SalesDetailDTO::new)
                    .toList();

            return new SalesTransactionDTO(transaction, detailDTOs);
        }).toList();
    }
}

