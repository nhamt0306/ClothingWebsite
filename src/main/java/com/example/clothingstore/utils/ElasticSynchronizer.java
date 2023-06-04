package com.example.clothingstore.utils;

import com.example.clothingstore.config.mapper.ProductMapper;
import com.example.clothingstore.model.ElasticProduct;
import com.example.clothingstore.model.ProductEntity;
import com.example.clothingstore.repository.ProductElasticRepository;
import com.example.clothingstore.repository.ProductRepository;
import com.example.clothingstore.service.impl.CommentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class ElasticSynchronizer {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductElasticRepository productElasticRepository;

    @Autowired
    CommentServiceImpl commentService;

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSynchronizer.class);

    private Integer count = -1;

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void sync() {
        LOG.info("Start Syncing - {}", LocalDateTime.now());
        this.syncProducts();
        LOG.info(" End Syncing - {}", LocalDateTime.now());
    }

    private void syncProducts() {
//        Specification<ProductEntity> productEntitySpecification = (root, criteriaQuery, criteriaBuilder) ->
//                getModificationDatePredicate(criteriaBuilder, root);

        count++;
        LOG.info("Current page - {}", count);

        Page<ProductEntity> products;
        Pageable paging = PageRequest.of(count, 1000);
        products = productRepository.findAll(paging);

//        if (productElasticRepository.count() == 0) {
//            products = productRepository.findAll(paging);
//        } else {
//            products = productRepository.findAll(productEntitySpecification, paging);
//        }

        if (products.getSize() == 0) {
            count = -1;
        }

        for (ProductEntity productEntity : products) {
            LOG.info("Syncing Product - {}", productEntity.getId());
            ElasticProduct elasticProduct = new ElasticProduct(
                    productEntity.getId(),
                    productEntity.getName(),
                    productEntity.getDescription(),
                    productEntity.getImage(),
                    productEntity.getAvgRating().toString(),
                    productEntity.getTypeEntities().get(0).getPrice(),
                    productEntity.getTypeEntities().get(0).getSize().toString(),
                    productEntity.getTypeEntities().get(0).getColor(),
                    productEntity.getTypeEntities().get(0).getSale().toString(),
                    productEntity.getTypeEntities().get(0).getSold(),
                    productEntity.getTypeEntities().get(0).getQuantity().toString(),
                    productEntity.getCategoryEntity().getId().toString(),
                    productEntity.getCategoryEntity().getName(),
                    commentService.countCommentByProductId(productEntity.getId()) == null ? "0" : commentService.countCommentByProductId(productEntity.getId()).toString(),
                    productEntity.getStatus()
                );

            productElasticRepository.save(elasticProduct);
        }
    }

    private static Predicate getModificationDatePredicate(CriteriaBuilder cb, Root<?> root) {
        Expression<Timestamp> currentTime;
        currentTime = cb.currentTimestamp();
        Expression<Timestamp> currentTimeMinus = cb.literal(new Timestamp(System.currentTimeMillis() -
                (60_000)));
        return cb.between(root.<Date>get("updated_at"),
                currentTimeMinus,
                currentTime
        );
    }
}