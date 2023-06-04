package com.example.clothingstore.service;

import com.example.clothingstore.model.TransactionEntity;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    TransactionEntity save(TransactionEntity transactionEntity);

    TransactionEntity getById(Long id);

    List<TransactionEntity> getAllByOrderId(Long orderId);
    List<TransactionEntity> getTransactionByColorAndSizeAndProductId(String color, Long size, Long productId);
    List<TransactionEntity> topProductBetween2Day(Date start, Date end);
}
