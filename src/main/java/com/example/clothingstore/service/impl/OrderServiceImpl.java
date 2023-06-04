package com.example.clothingstore.service.impl;

import com.example.clothingstore.config.LocalVariable;
import com.example.clothingstore.model.OrderEntity;
import com.example.clothingstore.model.ProductEntity;
import com.example.clothingstore.repository.OrderRepository;
import com.example.clothingstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Override
    public Page<OrderEntity> getAllPaging(int page) {
        return orderRepository.findAll(PageRequest.of(page, LocalVariable.OrderPagingLimit));

    }

    @Override
    public Page<OrderEntity> getAllByUserId(Long UserId, int page) {
        return orderRepository.findAllByUserEntityId(UserId,PageRequest.of(page,LocalVariable.OrderPagingLimit,
                Sort.by("id").descending()));
    }

    @Override
    public List<OrderEntity> getAllPendingOrderByUserId(Long UserId, String Status) {
        return orderRepository.getAllByUserEntityIdAndStatus(UserId, Status);
    }

    @Override
    public List<OrderEntity> getAllHistoryOrderByUserId(Long UserId, String Status) {
        return orderRepository.getAllByUserEntityIdAndStatusNot(UserId, Status);
    }

    @Override
    public OrderEntity findOrderById(Long OrderId) {
        return orderRepository.findById(OrderId).get();
    }

    @Override
    public void addNewOrder(OrderEntity orderEntity) {
        orderRepository.save(orderEntity);
    }

    @Override
    public List<OrderEntity> getAllOrderByUserId(Long userid) {
        return orderRepository.getAllByUserEntitiesIdOrderByCreate_atDesc(userid);
    }

    @Override
    public List<OrderEntity> getAllByUserId(Long userid) {
        return orderRepository.getAllByUserEntitiesIdOrderByCreate_atDesc(userid);
    }

    @Override
    public List<OrderEntity> getAllOrder() {
        return orderRepository.getAllOrderByDate();
    }

    @Override
    public boolean existOrderByUser(Long userId) {
        return orderRepository.existsByUserEntityId(userId);
    }

    @Override
    public List<OrderEntity> getAllPaging(Integer pageNo, Integer pageSize, String sortBy, String status) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        if (status.equals("Active"))
        {
            Page<OrderEntity> pagedResult = orderRepository.findAll(paging);
            if(pagedResult.hasContent()) {
                return pagedResult.getContent();
            } else {
                return new ArrayList<OrderEntity>();
            }
        }
        else
        {
            Page<OrderEntity> pagedResult = orderRepository.findAllByStatus(status,paging);
            if(pagedResult.hasContent()) {
                return pagedResult.getContent();
            } else {
                return new ArrayList<OrderEntity>();
            }
        }
    }

    @Override
    public String getAllOrderByDay() {
        return orderRepository.getTotalPriceCurrentDate();
    }

    @Override
    public String getAllOrderByMonth() {
        return orderRepository.getTotalPriceCurrentMonth();
    }

    @Override
    public String getAllOrderByYear() {
        return orderRepository.getTotalPriceCurrentYear();
    }

    @Override
    public Integer getAllOrderByDate(int Year, int Month, int day) {
        return orderRepository.getTotalPriceByDate(Year, Month, day);
    }

    @Override
    public Integer getRevenueBetween2Day(String startDate, String endDate) {
        return orderRepository.totalVenueBetween2Day(startDate, endDate);
    }
}
