package com.example.clothingstore.service;

import com.example.clothingstore.dto.ColorAndTypeDTO;
import com.example.clothingstore.model.CartProductEntity;

import java.util.List;
import java.util.Optional;

public interface CartProductService {
    CartProductEntity save(CartProductEntity cartProductEntity);
    List<CartProductEntity> getAllProductByCartId(Long cartId);
    void delete(Long id);
    Boolean existsByProduct(Long productId, String color, Long size, Long cartId);
    void deleteProductInCart(Long cartId, Long productId, String color, Long size);
    CartProductEntity increaseQuantity(Long productId, Long cartId, String color, Long size);
    CartProductEntity decreaseQuantity(Long productId, Long cartId, String color, Long size);

    CartProductEntity setQuantity(Long productId, Long cartId, String color, Long size, Long quantity);
}
