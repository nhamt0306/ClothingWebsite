package com.example.clothingstore.repository;

import com.example.clothingstore.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByOrderEntityId(Long orderId);
    List<TransactionEntity> findAllByColorAndSizeAndProductEntityId(String color, Long size, Long product_id);
}
