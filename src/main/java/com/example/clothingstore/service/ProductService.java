package com.example.clothingstore.service;

import com.example.clothingstore.model.ProductEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductEntity> getAllProduct();
    ProductEntity save(ProductEntity productEntity);
    ProductEntity findProductById(Long id);
    List<ProductEntity> findProductByName(String name);
    void delete(Long id);
    List<ProductEntity> findProductByCat(Long catId);
    boolean existByProductId(Long id);
    ProductEntity uploadImage(long id, MultipartFile image);
}
