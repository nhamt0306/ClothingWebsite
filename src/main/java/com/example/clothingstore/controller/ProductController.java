package com.example.clothingstore.controller;

import com.example.clothingstore.config.LocalVariable;
import com.example.clothingstore.dto.ProductDTO;
import com.example.clothingstore.config.mapper.ProductMapper;
import com.example.clothingstore.config.mapper.ProductPagingResponse;
import com.example.clothingstore.dto.PromptDTO;
import com.example.clothingstore.model.CategoryEntity;
import com.example.clothingstore.model.ElasticProduct;
import com.example.clothingstore.model.ProductEntity;
import com.example.clothingstore.model.TypeEntity;
import com.example.clothingstore.security.principal.UserDetailService;
import com.example.clothingstore.service.impl.CategoryServiceImpl;
import com.example.clothingstore.service.impl.CommentServiceImpl;
import com.example.clothingstore.service.impl.ProductServiceImpl;
import com.example.clothingstore.service.impl.TypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static co.elastic.clients.elasticsearch.ingest.ProcessorBuilders.foreach;

@CrossOrigin(origins = "*")   //Để ghép AuthController với các controller khác
@RequestMapping
@RestController
public class ProductController {
    @Autowired
    TypeServiceImpl typeService;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    UserDetailService userDetailService;

    @Autowired
    CommentServiceImpl commentService;

    @GetMapping("/product/getAll")
    public ResponseEntity<?> getAllProduct()
    {
        List<ProductEntity> productEntityList = productService.getAllProduct();
        List<ProductMapper> responseProductList = new ArrayList<>();
        for (ProductEntity productEntity : productEntityList)
        {
            if (!productEntity.getTypeEntities().isEmpty())
            {
                ProductMapper productMapper = new ProductMapper(
                        productEntity.getId(),
                        productEntity.getName(),
                        productEntity.getDescription(),
                        productEntity.getImage(),
                        productEntity.getAvgRating(),
                        productEntity.getTypeEntities().get(0).getPrice(),
                        productEntity.getTypeEntities().get(0).getSize(),
                        productEntity.getTypeEntities().get(0).getColor(),
                        productEntity.getTypeEntities().get(0).getSale(),
                        productEntity.getTypeEntities().get(0).getSold(),
                        productEntity.getTypeEntities().get(0).getQuantity(),
                        productEntity.getCategoryEntity().getId(),
                        productEntity.getCategoryEntity().getName(),
                        commentService.countCommentByProductId(productEntity.getId())
                );
                responseProductList.add(productMapper);
            }
            else
            {
                ProductMapper productMapper = new ProductMapper(
                        productEntity.getId(),
                        productEntity.getName(),
                        productEntity.getDescription(),
                        productEntity.getImage(),
                        productEntity.getAvgRating(),
                        Long.valueOf(0L),
                        Long.valueOf(0L),
                        "Đang cập nhật",
                        Long.valueOf(0L),
                        Long.valueOf(0L),
                        Long.valueOf(0L),
                        productEntity.getCategoryEntity().getId(),
                        productEntity.getCategoryEntity().getName(),
                        commentService.countCommentByProductId(productEntity.getId())
                );
                responseProductList.add(productMapper);
            }
        }
        return ResponseEntity.ok(responseProductList);
    }

    @GetMapping("/product")
    public Object getAllProducts(@RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "8") Integer pageSize,
                                            @RequestParam(defaultValue = "-id") String sortBy,
                                            @RequestParam(defaultValue = "") Long catId,
                                            @RequestParam(defaultValue = "") Integer rating,
                                            @RequestParam(defaultValue = "") String keyword) {
        Page<ProductEntity> productEntityList;
        ProductPagingResponse productPagingResponse;

        try {
            if (pageNo < 1 || pageSize < 1) {
                productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
                return new ResponseEntity<>(productPagingResponse, HttpStatus.BAD_REQUEST);
            }

            productEntityList = productService.getAllProductByFiltering(pageNo - 1, pageSize, sortBy, catId, rating, keyword);

            if (productEntityList.getTotalElements() == 0) {
                productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
                return new ResponseEntity<>(productPagingResponse, HttpStatus.NOT_FOUND);
            }

            if (pageNo > productEntityList.getTotalPages()) {
                productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
                return new ResponseEntity<>(productPagingResponse, HttpStatus.BAD_REQUEST);
            }

            List<ProductMapper> responseProductList = new ArrayList<>();
            for (ProductEntity productEntity : productEntityList)
            {
                if (!productEntity.getTypeEntities().isEmpty())
                {
                    ProductMapper productMapper = new ProductMapper(
                            productEntity.getId(),
                            productEntity.getName(),
                            productEntity.getDescription(),
                            productEntity.getImage(),
                            productEntity.getAvgRating(),
                            productEntity.getTypeEntities().get(0).getPrice(),
                            productEntity.getTypeEntities().get(0).getSize(),
                            productEntity.getTypeEntities().get(0).getColor(),
                            productEntity.getTypeEntities().get(0).getSale(),
                            productEntity.getTypeEntities().get(0).getSold(),
                            productEntity.getTypeEntities().get(0).getQuantity(),
                            productEntity.getCategoryEntity().getId(),
                            productEntity.getCategoryEntity().getName(),
                            commentService.countCommentByProductId(productEntity.getId()) == null ? 0L : commentService.countCommentByProductId(productEntity.getId()),
                            productEntity.getStatus()
                    );
                    responseProductList.add(productMapper);
                }
                else
                {
                    ProductMapper productMapper = new ProductMapper(
                            productEntity.getId(),
                            productEntity.getName(),
                            productEntity.getDescription(),
                            productEntity.getImage(),
                            productEntity.getAvgRating(),
                            Long.valueOf(0L), Long.valueOf(0L),
                            "Đang cập nhật",
                            Long.valueOf(0L),
                            Long.valueOf(0L),
                            Long.valueOf(0L),
                            productEntity.getCategoryEntity().getId(),
                            productEntity.getCategoryEntity().getName(),
                            commentService.countCommentByProductId(productEntity.getId()) == null ? 0L : commentService.countCommentByProductId(productEntity.getId()),
                            productEntity.getStatus()
                    );
                    responseProductList.add(productMapper);
                }
            }
            productPagingResponse = new ProductPagingResponse(responseProductList, productEntityList.getTotalElements());
            return productPagingResponse;
        } catch(Exception e) {
            productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
            return new ResponseEntity<>(productPagingResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product/elastic-search")
    public Object getAllProductsElasticSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                 @RequestParam(defaultValue = "8") Integer pageSize,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "") Long catId,
                                 @RequestParam(defaultValue = "") Integer rating,
                                 @RequestParam(defaultValue = "") String keyword) {
        Page<ElasticProduct> productEntityList;
        ProductPagingResponse productPagingResponse;

        try {
            if (pageNo < 1 || pageSize < 1) {
                productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
                return new ResponseEntity<>(productPagingResponse, HttpStatus.BAD_REQUEST);
            }

            productEntityList = productService.getElasticSearchProductByFiltering(pageNo - 1, pageSize, sortBy, catId, rating, keyword);

            if (productEntityList.getTotalElements() == 0) {
                productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
                return new ResponseEntity<>(productPagingResponse, HttpStatus.NOT_FOUND);
            }

            if (pageNo > productEntityList.getTotalPages()) {
                productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
                return new ResponseEntity<>(productPagingResponse, HttpStatus.BAD_REQUEST);
            }

            List<ProductMapper> responseProductList = new ArrayList<>();
            for (ElasticProduct elasticProduct : productEntityList)
            {
                    ProductMapper productMapper = new ProductMapper(
                            elasticProduct.getId(),
                            elasticProduct.getName(),
                            elasticProduct.getDescription(),
                            elasticProduct.getImage(),
                            Long.parseLong(elasticProduct.getAvgRating()),
                            elasticProduct.getPrice(),
                            Long.parseLong(elasticProduct.getSize()),
                            elasticProduct.getColor(),
                            Long.parseLong(elasticProduct.getSale()),
                            elasticProduct.getSold(),
                            Long.parseLong(elasticProduct.getQuantity()),
                            elasticProduct.getId(),
                            elasticProduct.getName(),
                            Long.parseLong(elasticProduct.getCountComment()),
                            elasticProduct.getStatus()
                    );
                    responseProductList.add(productMapper);

            }
            productPagingResponse = new ProductPagingResponse(responseProductList, productEntityList.getTotalElements());
            return productPagingResponse;
        } catch(Exception e) {
            productPagingResponse = new ProductPagingResponse(new ArrayList<>(), 0);
            return new ResponseEntity<>(productPagingResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProductById(@PathVariable long id){
        try {
            ProductEntity productEntity = productService.findProductById(id);
            if (!productEntity.getTypeEntities().isEmpty())
            {
                ProductMapper productMapper = new ProductMapper(productEntity.getId(),
                        productEntity.getName(),
                        productEntity.getDescription(),
                        productEntity.getImage(),
                        productEntity.getAvgRating(),
                        productEntity.getTypeEntities().get(0).getPrice(),
                        productEntity.getTypeEntities().get(0).getSize(),
                        productEntity.getTypeEntities().get(0).getColor(),
                        productEntity.getTypeEntities().get(0).getSale(),
                        productEntity.getTypeEntities().get(0).getSold(),
                        productEntity.getTypeEntities().get(0).getQuantity(),
                        productEntity.getCategoryEntity().getId(),
                        productEntity.getCategoryEntity().getName(),
                        commentService.countCommentByProductId(productEntity.getId()) == null ? 0L : commentService.countCommentByProductId(productEntity.getId()),
                        productEntity.getStatus());
                return ResponseEntity.ok(productMapper);
            }
            else {
                return new ResponseEntity<>("Type of product is invalid!", HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Cannot find product with id = " + id, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product/category/{id}")
    public ResponseEntity<?> getProductByCategory(@PathVariable long id){
        try {
            List<ProductEntity> productEntityList = productService.findProductByCat(id);
            List<ProductMapper> responseProductList = new ArrayList<>();
            for (ProductEntity productEntity : productEntityList)
            {
                ProductMapper productMapper = new ProductMapper(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getImage(), productEntity.getAvgRating(), productEntity.getTypeEntities().get(0).getPrice(), productEntity.getTypeEntities().get(0).getSize(), productEntity.getTypeEntities().get(0).getColor(), productEntity.getTypeEntities().get(0).getSale(), productEntity.getTypeEntities().get(0).getSold(), productEntity.getTypeEntities().get(0).getQuantity(), productEntity.getCategoryEntity().getId(), productEntity.getCategoryEntity().getName(), commentService.countCommentByProductId(productEntity.getId()));
                responseProductList.add(productMapper);
            }
            return ResponseEntity.ok(responseProductList);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Cannot find product with category id = " + id, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/product/search")
    public Object searchProduct(@RequestParam(defaultValue = "1") Integer pageNo,
                                @RequestParam(defaultValue = "100") Integer pageSize,
                                @RequestParam(defaultValue = "id") String sortBy,
                                @RequestParam(required = false) String keyword){
        try {
            Integer maxPageSize;
            Integer maxPageNo;
            List<ProductEntity> productEntityList = new ArrayList<>();
            List<ProductEntity> numberItemList = new ArrayList<>();
            if (keyword != null) {
                maxPageSize = productService.getAllProduct().size();
                if (pageSize > maxPageSize)
                {
                    pageSize = 12;
                }
                maxPageNo = maxPageSize / pageSize;
                if (pageNo > maxPageNo +1)
                {
                    pageNo = maxPageNo +1;
                }
                productEntityList = productService.getAllProductByKeyword(pageNo-1, pageSize, sortBy, keyword);
                numberItemList = productService.findProductByName(keyword);
            }

            List<ProductMapper> responseProductList = new ArrayList<>();
            for (ProductEntity productEntity : productEntityList)
            {
                if (!productEntity.getTypeEntities().isEmpty())
                {
                    ProductMapper productMapper = new ProductMapper(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getImage(), productEntity.getAvgRating(), productEntity.getTypeEntities().get(0).getPrice(), productEntity.getTypeEntities().get(0).getSize(), productEntity.getTypeEntities().get(0).getColor(), productEntity.getTypeEntities().get(0).getSale(), productEntity.getTypeEntities().get(0).getSold(), productEntity.getTypeEntities().get(0).getQuantity(), productEntity.getCategoryEntity().getId(), productEntity.getCategoryEntity().getName(),                         commentService.countCommentByProductId(productEntity.getId()) == null ? 0L : commentService.countCommentByProductId(productEntity.getId()));
                    responseProductList.add(productMapper);
                }
                else
                {
                    ProductMapper productMapper = new ProductMapper(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getImage(), productEntity.getAvgRating(), Long.valueOf(0L), Long.valueOf(0L), "Đang cập nhật", Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(0L), productEntity.getCategoryEntity().getId(), productEntity.getCategoryEntity().getName(),                         commentService.countCommentByProductId(productEntity.getId()) == null ? 0L : commentService.countCommentByProductId(productEntity.getId()));
                    responseProductList.add(productMapper);
                }
            }
            ProductPagingResponse productPagingResponse = new ProductPagingResponse(responseProductList, numberItemList.size());
            return productPagingResponse;
        }
        catch (Exception e)
        {
            return new ResponseEntity<>("Cannot find product", HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/admin/product/create")
    public Object createProduct(@RequestBody ProductDTO productDTO) throws ParseException {
        ProductEntity productEntity = new ProductEntity();
        if (productDTO.getName().equals(null))
        {
            return "Tên danh mục không hợp lê!";
        }
        productEntity.setName(productDTO.getName());
        productEntity.setAvgRating(Long.valueOf(5));
        productEntity.setImage(productDTO.getImage());
        productEntity.setDescription(productDTO.getDescription());
        CategoryEntity categoryEntity = categoryService.findCategoryById(productDTO.getCategory_id());
        if (categoryEntity == null)
        {
            return "Danh mục không tồn tại!";
        }
        productEntity.setCategoryEntity(categoryEntity);
        productEntity.setUpdate_at(new Timestamp(System.currentTimeMillis()));
        productEntity.setCreate_at(new Timestamp(System.currentTimeMillis()));
        productService.save(productEntity);
        // return entity for front end
        if (!productEntity.getTypeEntities().isEmpty())
        {
            ProductMapper productMapper = new ProductMapper(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getImage(), productEntity.getAvgRating(), productEntity.getTypeEntities().get(0).getPrice(), productEntity.getTypeEntities().get(0).getSize(), productEntity.getTypeEntities().get(0).getColor(), productEntity.getTypeEntities().get(0).getSale(), productEntity.getTypeEntities().get(0).getSold(), productEntity.getTypeEntities().get(0).getQuantity(), productEntity.getCategoryEntity().getId(), productEntity.getCategoryEntity().getName());
            return productMapper;
        }
        ProductMapper productMapper = new ProductMapper(productEntity.getId(), productEntity.getName(), productEntity.getDescription(), productEntity.getImage(), productEntity.getAvgRating(), Long.valueOf(0L), Long.valueOf(0L), "Đang cập nhật", Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(0L), productEntity.getCategoryEntity().getId(), productEntity.getCategoryEntity().getName());
        return productMapper;
    }

    @DeleteMapping("/admin/product/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable long id)
    {
        productService.delete(id);
        return ResponseEntity.ok(LocalVariable.messageDeleteCatSuccess);
    }

    @PutMapping("/admin/product/{id}")
    public Object updateProduct(@PathVariable long id, @RequestBody ProductDTO productDTO) throws ParseException {
        if(!productService.existByProductId(id)) {
            return new ResponseEntity<>("Không tìm được sản phẩm", HttpStatus.NOT_FOUND);
        }

        ProductEntity productEntity = productService.findProductById(id);
        productEntity.setName(productDTO.getName());
        productEntity.setDescription(productDTO.getDescription());

        CategoryEntity categoryEntity = categoryService.findCategoryById(productDTO.getCategory_id());
        if (categoryEntity == null){
            return new ResponseEntity<>("Danh mục không tồn tại", HttpStatus.NOT_FOUND);
        }
        productEntity.setCategoryEntity(categoryEntity);
        productEntity.setUpdate_at(new Timestamp(System.currentTimeMillis()));
        productEntity.setCreate_at(new Timestamp(System.currentTimeMillis()));
        productService.save(productEntity);

        return ResponseEntity.ok(
                new ProductDTO(productEntity.getName(),
                        productEntity.getDescription(),
                        productEntity.getImage(),
                        productEntity.getCategoryEntity().getId())
        );
    }

    @GetMapping("/product/prompt")
    public Object searchProduct(){
        HashMap<String, String> colorMap = new HashMap<String, String>();
        colorMap.put("f1f1f1", "Trắng");
        colorMap.put("000", "Đen");
        colorMap.put("8c8c8c", "Xám");
        colorMap.put("d9b99b", "Be");
        colorMap.put("186287", "Xanh nước nhạt");
        colorMap.put("114abd", "Xanh nước đậm");
        colorMap.put("6e10a1", "Tím");

        PromptDTO promptDTO = new PromptDTO();

        List<ProductEntity> productEntities = productService.getAllProduct();
        for (ProductEntity productEntity: productEntities) {
            promptDTO.setPrompt(promptDTO.getPrompt() + "Tên:" + productEntity.getName().trim());
            promptDTO.setPrompt(promptDTO.getPrompt() + ",");

            List<TypeEntity> typeEntities = typeService.getAllTypeByProduct(productEntity.getId());
            for (TypeEntity typeEntity : typeEntities) {
                promptDTO.setPrompt(promptDTO.getPrompt() + "size:" +  typeEntity.getSize().toString());
                promptDTO.setPrompt(promptDTO.getPrompt() + "-");
                promptDTO.setPrompt(promptDTO.getPrompt() + "màu:" +  colorMap.get(typeEntity.getColor().trim()));
                promptDTO.setPrompt(promptDTO.getPrompt() + "-");
                promptDTO.setPrompt(promptDTO.getPrompt() + "giá:" +  typeEntity.getPrice().toString());
                promptDTO.setPrompt(promptDTO.getPrompt() + "-");
                promptDTO.setPrompt(promptDTO.getPrompt() + "số lượng:" +  typeEntity.getQuantity());
                if (typeEntities.indexOf(typeEntity) == typeEntities.size() - 1)
                    promptDTO.setPrompt(promptDTO.getPrompt() + ";");
                else
                    promptDTO.setPrompt(promptDTO.getPrompt() + ",");
            }
        }
//        return ResponseEntity.ok(promptDTO);
        return ResponseEntity.ok(promptDTO.getPrompt());
    }
}
