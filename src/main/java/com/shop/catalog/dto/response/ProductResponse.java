package com.shop.catalog.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
  private Integer id;
  private String name;
  private BigDecimal price;
  private Integer stock;
  private Integer categoryId;
  private String categoryName;
}
