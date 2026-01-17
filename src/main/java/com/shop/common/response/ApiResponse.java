package com.shop.common.response;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;

  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder().success(true).message("OK").data(data).build();
  }
}
