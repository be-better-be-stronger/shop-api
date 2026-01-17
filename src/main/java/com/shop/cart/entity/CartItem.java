package com.shop.cart.entity;

import java.math.BigDecimal;

import com.shop.catalog.entity.Product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="cart_items",
       uniqueConstraints=@UniqueConstraint(
    		   columnNames={"cart_id","product_id"}
    		   )
)
@Getter @Setter @NoArgsConstructor
public class CartItem {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="cart_id")
  private Cart cart;

  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="product_id", nullable = false)
  private Product product;

  @Column(nullable=false)
  private int qty;

  @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal unitPrice;



  public CartItem(Cart cart, Product product, int qty, BigDecimal unitPrice) {
    this.cart = cart;        // owning side: FK nằm ở đây
    this.product = product;
    this.qty = qty;
    this.unitPrice = unitPrice;
  }

  
}
