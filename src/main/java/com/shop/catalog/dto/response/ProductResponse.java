package com.shop.catalog.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
  private Integer id;
  private String name;
  private BigDecimal price;
  private Integer stock;
  private Integer categoryId;
  private String categoryName;
  private Boolean isActive;
  private String imageUrl;
}

