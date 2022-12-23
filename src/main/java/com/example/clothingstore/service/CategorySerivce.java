package com.example.clothingstore.service;

import com.example.clothingstore.model.CategoryEntity;
import com.example.clothingstore.model.ProductEntity;

import java.util.List;

public interface CategorySerivce {
    List<CategoryEntity> getAllCategory();
    CategoryEntity save(CategoryEntity categoryEntity);
    CategoryEntity findCategoryById(Long id);
    List<CategoryEntity> findAllCategoryByName(String name);
    List<CategoryEntity> findByCatParentId(Long id);
    List<CategoryEntity> findAllCategoryActive();
    void deleteCategoryById(Long id);
    List<CategoryEntity> getAllCatPaging(Integer pageNo, Integer pageSize);
}
