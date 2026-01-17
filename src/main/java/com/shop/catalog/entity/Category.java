package com.shop.catalog.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter @Setter @NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

 // mappedBy = tên field ở Product (owning side nằm ở Product)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();    
    
    // helper để giữ đồng bộ 2 chiều (cực quan trọng)
    public void addProduct(Product p) {
        products.add(p);
        p.setCategory(this);
    }
    public void removeProduct(Product p) {
        products.remove(p);
        p.setCategory(null);
    }

   
}
