package com.example.clothingstore.model;

import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseClassEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String status = "Active";
    private Long parentId;

    // Relationship with table ProductEntity
    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL)
    private List<ProductEntity> productEntities =new ArrayList<>();

    // Constructor

    public CategoryEntity() {
    }

    public CategoryEntity(Long id, String name, String status, Long parentId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.parentId = parentId;
    }


    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
