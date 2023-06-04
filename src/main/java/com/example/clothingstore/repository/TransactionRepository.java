package com.example.clothingstore.repository;

import com.example.clothingstore.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByOrderEntityId(Long orderId);
    List<TransactionEntity> findAllByColorAndSizeAndProductEntityId(String color, Long size, Long product_id);
    @Query(nativeQuery = true, value = "Select T.id, T.create_at, T.update_at, T.color, SUM(T.quantity) as quantity, T.size, T.unit_price, T.order_id, T.product_id, T.is_commented\n" +
            "From clothing_store.transactions T, clothing_store.orders O\n" +
            "WHERE  O.id = T.order_id AND O.status = 'DONE' "+
            "AND T.update_at BETWEEN cast((:startd) as datetime) AND cast((:endd) as datetime)" +
            "GROUP BY T.product_id\n" +
            "ORDER BY SUM(T.quantity) LIMIT 7")
    List<TransactionEntity> topProductSaleBetween2Day(@Param("startd") Date startd, @Param("endd") Date endd);
}
