// src/main/java/com/shop/common/GlobalExceptionHandler.java
package com.shop.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.shop.common.ErrorCode;
import com.shop.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse<Void>> handle(ApiException e) {
    return ResponseEntity
        .status(e.getStatus())
        .body(ApiResponse.fail(e.getCode()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValid(MethodArgumentNotValidException e) {
    String msg = e.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .orElse("Validation error");
    return ResponseEntity.badRequest().body(ApiResponse.fail(msg));
  }
  
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex) {
      // trả 404 theo format response của mày
      return ResponseEntity.status(ErrorCode.ERR_NOT_FOUND.getStatus())
              .body(ApiResponse.fail("ERR_NOT_FOUND")); // đổi theo response wrapper của mày
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleOther(Exception e) {
    return ResponseEntity.internalServerError()
        .body(ApiResponse.fail("ERR_INTERNAL"));
  }
}
