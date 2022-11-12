package com.example.clothingstore.service;

import com.example.clothingstore.model.TypeEntity;

import java.util.List;

public interface TypeService {
    List<TypeEntity> getAll();
    TypeEntity save(TypeEntity type);
    TypeEntity findTypeById(Long id);
    void delete(Long id);
    List<TypeEntity> getAllTypeByProduct(Long proId);
    TypeEntity getTypeByColorAndSizeAndProductId(String color, Long size, Long productId);
}
