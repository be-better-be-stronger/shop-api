package com.shop.catalog.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.catalog.dto.request.PageProductRequest;
import com.shop.catalog.dto.request.PageProductStatusRequest;
import com.shop.catalog.dto.request.UpsertProductRequest;
import com.shop.catalog.dto.response.ProductResponse;
import com.shop.catalog.service.ProductQueryService;
import com.shop.catalog.service.ProductService;
import com.shop.common.response.ApiResponse;
import com.shop.common.upload.UploadDir;
import com.shop.common.upload.UploadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

	private final ProductService productService;
	private final UploadService uploadService;
	private final ProductQueryService productQueryService;

	@GetMapping
    public ApiResponse<Page<ProductResponse>> list(
            @Valid PageProductRequest page,          // page, size, q, cat, sort, dir
            @RequestParam(required = false) Boolean status
    ) {
        PageProductStatusRequest req = new PageProductStatusRequest();
        req.setPage(page);
        req.setStatus(status);
        return ApiResponse.ok(productQueryService.findAdminProducts(req));
    }
	
	@PostMapping
	  public ApiResponse<ProductResponse> create( 
			  @Valid @RequestBody UpsertProductRequest req) {
	    return ApiResponse.ok(productService.create(req));
	  }

	  @PutMapping("/{id}")
	  public ApiResponse<ProductResponse> update(
	      @PathVariable Integer id,
	      @Valid @RequestBody UpsertProductRequest req) {
	    return ApiResponse.ok(productService.update(id, req));
	  }

	  @PatchMapping("/{id}/disable")
	  public ApiResponse<Void> disable(@PathVariable Integer id) {
	    productService.disable(id);
	    return ApiResponse.ok(null);
	  }
	  
	  @PatchMapping("/{id}/enable")
	  public ApiResponse<Void> enable(@PathVariable Integer id) {
	    productService.enable(id);
	    return ApiResponse.ok(null);
	  }
	  
	  @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	  public ApiResponse<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
	    String imageUrl = uploadService.uploadImage(file, UploadDir.STAGING);
	    return ApiResponse.ok(imageUrl);
	  }
}
