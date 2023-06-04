package com.example.clothingstore.repository;

import com.example.clothingstore.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAllByUserEntityId(Long userId, Pageable pageable);
    List<OrderEntity> getAllByUserEntityIdAndStatus(Long userEntities_id, String status);
    List<OrderEntity> getAllByUserEntityIdAndStatusNot(Long userEntities_id, String status);

    @Query(value = "select * from clothing_store.orders where user_id = :UserId order by create_at desc",nativeQuery = true)
    List<OrderEntity> getAllByUserEntitiesIdOrderByCreate_atDesc(@Param("UserId") Long UserId);

    @Query(value = "select * from orders order by create_at desc",nativeQuery = true)
    List<OrderEntity> getAllOrderByDate();

    @Query(value = "select * from orders order by create_at desc",nativeQuery = true)
    List<OrderEntity> getAllBy();

    boolean existsByUserEntityId(Long userid);
    Page<OrderEntity> findAllByStatus(String status, Pageable pageable);
    Page<OrderEntity> findAll(Pageable pageable);

    // Statistic by month
    @Query(value = "SELECT sum(total_price) FROM clothing_store.orders where status = 'DONE' and year(update_at) = YEAR(CURDATE())", nativeQuery = true)
    String getTotalPriceCurrentYear();

    // Statistic by month
    @Query(value = "SELECT sum(total_price) FROM clothing_store.orders where status = 'DONE' and year(update_at) = YEAR(CURDATE()) and month(update_at) = MONTH(CURDATE())", nativeQuery = true)
    String getTotalPriceCurrentMonth();

    // Statistic by day
    @Query(value = "SELECT sum(total_price) FROM clothing_store.orders where status = 'DONE' and year(update_at) = YEAR(CURDATE()) and month(update_at) = MONTH(CURDATE()) and day(update_at) = DAY(CURDATE())", nativeQuery = true)
    String getTotalPriceCurrentDate();

    // Statistic by day
    @Query(value = "SELECT sum(total_price) FROM clothing_store.orders where status = 'DONE' and year(update_at) = :Year and month(update_at) = :Month and day(update_at) = :Day", nativeQuery = true)
    Integer getTotalPriceByDate(@Param("Year") int Year, @Param("Month") int Month, @Param("Day") int Day);

    // Statistic between 2 day
    @Query(value = "SELECT sum(total_price) FROM clothing_store.orders where status = 'DONE' and update_at BETWEEN :StartDate AND :EndDate", nativeQuery = true)
    Integer totalVenueBetween2Day(@Param("StartDate") String StartDate, @Param("EndDate") String EndDate);
}
