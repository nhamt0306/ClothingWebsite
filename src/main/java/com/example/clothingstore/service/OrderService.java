package com.example.clothingstore.service;

import com.example.clothingstore.model.OrderEntity;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Page<OrderEntity> getAllPaging(int page);

    Page<OrderEntity> getAllByUserId(Long UserId,int page);

    List<OrderEntity> getAllPendingOrderByUserId(Long UserId, String Status);

    List<OrderEntity> getAllHistoryOrderByUserId(Long UserId, String Status);

    OrderEntity findOrderById(Long OrderId);
    void addNewOrder(OrderEntity orderEntity);
    List<OrderEntity> getAllOrderByUserId(Long userid);
    List<OrderEntity> getAllByUserId(Long userid);
    List<OrderEntity> getAllOrder();
    boolean existOrderByUser(Long userId);

    List<OrderEntity> getAllPaging(Integer pageNo, Integer pageSize, String sortBy, String status);
    String getAllOrderByDay();
    String getAllOrderByMonth();
    String getAllOrderByYear();
    Integer getAllOrderByDate(int Year, int Month, int day);

    Integer getRevenueBetween2Day(String startDate, String endDate);
}
