package com.shop.catalog.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.shop.catalog.dto.request.PageProductRequest;
import com.shop.catalog.dto.request.UpsertProductRequest;
import com.shop.catalog.entity.Category;
import com.shop.catalog.entity.Product;
import com.shop.catalog.repository.CategoryRepository;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.common.upload.LocalUploadService;
import com.shop.common.upload.UploadDir;

public class ProductServiceImplTest {
	//1. mock
	@Mock
	ProductRepository productRepo;
	@Mock
	CategoryRepository categoryRepo;
	@Mock
	LocalUploadService uploadService;

	//2. SUT
	@InjectMocks
	ProductServiceImpl service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	//  3. helper
	private static PageProductRequest req(Integer page, Integer size, String sort, String dir, String q, Integer cat) {
		PageProductRequest r = new PageProductRequest();
		r.setPage(page);
		r.setSize(size);
		r.setSort(sort);
		r.setDir(dir);
		r.setQ(q);
		r.setCat(cat);
		return r;
	}

	// =========================
	// search() tests
	// =========================

	@Test
	void search_noFilter_shouldCallFindActiveTrue_withDefaultSortNameAsc_andClampPage() {
		// page client 1-based -> 0-based; page <=0 => 0
		PageProductRequest r = req(0, 10, null, null, null, null);

		when(productRepo.findByIsActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(productRepo).findByIsActiveTrue(captor.capture());
		verify(productRepo, never()).searchActive(any(), any(), any());

		Pageable p = captor.getValue();
		assertEquals(0, p.getPageNumber());
		assertEquals(10, p.getPageSize());

		Sort.Order order = p.getSort().getOrderFor("name");
		assertNotNull(order);
		assertEquals(Direction.ASC, order.getDirection());
	}

	@Test
	void search_sizeLessThan1_shouldDefaultTo10() {
		PageProductRequest r = req(1, 0, "name", "asc", null, null);

		when(productRepo.findByIsActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(productRepo).findByIsActiveTrue(captor.capture());
		assertEquals(10, captor.getValue().getPageSize());
	}

	@Test
	void search_sizeGreaterThan50_shouldClampTo50() {
		PageProductRequest r = req(1, 999, "name", "asc", null, null);

		when(productRepo.findByIsActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(productRepo).findByIsActiveTrue(captor.capture());
		assertEquals(50, captor.getValue().getPageSize());
	}

	@Test
	void search_sortNotAllowed_shouldFallbackToName() {
		PageProductRequest r = req(1, 10, "id", "asc", null, null); // "id" không nằm trong whitelist

		when(productRepo.findByIsActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(productRepo).findByIsActiveTrue(captor.capture());

		Pageable p = captor.getValue();
		assertNotNull(p.getSort().getOrderFor("name"));
		assertNull(p.getSort().getOrderFor("id"));
	}

	@Test
	void search_dirNotAllowed_shouldFallbackToAsc() {
		PageProductRequest r = req(1, 10, "price", "xxx", null, null);

		when(productRepo.findByIsActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(productRepo).findByIsActiveTrue(captor.capture());

		Pageable p = captor.getValue();
		Sort.Order order = p.getSort().getOrderFor("price");
		assertNotNull(order);
		assertEquals(Direction.ASC, order.getDirection());
	}

	@Test
	void search_dirDesc_shouldUseDesc() {
		PageProductRequest r = req(2, 10, "price", "desc", null, null); // page=2 -> index=1

		when(productRepo.findByIsActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(productRepo).findByIsActiveTrue(captor.capture());

		Pageable p = captor.getValue();
		assertEquals(1, p.getPageNumber());
		Sort.Order order = p.getSort().getOrderFor("price");
		assertNotNull(order);
		assertEquals(Direction.DESC, order.getDirection());
	}

	@Test
	void search_withQOrCat_shouldCallSearchActive() {
		PageProductRequest r = req(1, 10, "name", "asc", "  iphone  ", null);

		when(productRepo.searchActive(any(), any(), any(Pageable.class))).thenReturn(Page.empty());

		service.search(r);

		verify(productRepo, never()).findByIsActiveTrue(any(Pageable.class));
		verify(productRepo).searchActive(any(), any(), any(Pageable.class));
	}

	// =========================
	// getById() tests
	// =========================

	@Test
	void getById_inactiveProduct_shouldThrowNotFound() {
		Product p = new Product();
		p.setId(1);
		p.setIsActive(false);

		when(productRepo.findById(1)).thenReturn(Optional.of(p));

		ApiException ex = assertThrows(ApiException.class, () -> service.getById(1));
		assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getCode());
	}

	@Test
	void getById_notExist_shouldThrowNotFound() {
		when(productRepo.findById(99)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> service.getById(99));
		assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getCode());
	}

	// =========================
	// create() tests
	// =========================

	@Test
	void create_categoryNotFound_shouldThrowNotFound_andNotSaveProduct() {
		UpsertProductRequest req = new UpsertProductRequest();
		req.setCategoryId(10);

		when(categoryRepo.findById(10)).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
		assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getCode());

		verify(productRepo, never()).save(any());
		verifyNoInteractions(uploadService);
	}

	@Test
	void create_withStagingUrl_shouldMoveAndSetFinalUrl_andSaveOnce() {
		UpsertProductRequest req = new UpsertProductRequest();
		req.setName("P1");
		req.setStock(10);
		req.setPrice(new BigDecimal("100"));
		req.setCategoryId(1);
		req.setImageUrl("/uploads/staging/a.png");
		req.setDescription("d");

		Category c = new Category();
		c.setId(1);

		when(categoryRepo.findById(1)).thenReturn(Optional.of(c));

		// save: return same entity, giả lập JPA assign id
		when(productRepo.save(any(Product.class))).thenAnswer(inv -> {
			Product p = inv.getArgument(0);
			p.setId(123);
			return p;
		});

		when(uploadService.moveImage("/uploads/staging/a.png", UploadDir.PRODUCTS))
				.thenReturn("/uploads/products/a.png");

		var res = service.create(req);

		assertNotNull(res);

		verify(productRepo, times(1)).save(any(Product.class));
		verify(uploadService, times(1)).moveImage("/uploads/staging/a.png", UploadDir.PRODUCTS);
		verify(uploadService, never()).deleteByUrl(anyString());
	}

	@Test
	void create_whenMoveImageThrows_shouldDeleteStaging_andThrowErrServer() {
		UpsertProductRequest req = new UpsertProductRequest();
		req.setName("P1");
		req.setStock(10);
		req.setPrice(new BigDecimal("100"));
		req.setCategoryId(1);
		req.setImageUrl("/uploads/staging/a.png");

		Category c = new Category();
		c.setId(1);

		when(categoryRepo.findById(1)).thenReturn(Optional.of(c));
		when(productRepo.save(any(Product.class))).thenAnswer(inv -> {
			Product p = inv.getArgument(0);
			p.setId(1);
			return p;
		});

		when(uploadService.moveImage(anyString(), eq(UploadDir.PRODUCTS))).thenThrow(new RuntimeException("IO error"));

		ApiException ex = assertThrows(ApiException.class, () -> service.create(req));
		assertEquals(ErrorCode.ERR_SERVER, ex.getCode());

		// safeDelete(stagingUrl, finalUrl) => stagingUrl phải bị xóa
		verify(uploadService).deleteByUrl("/uploads/staging/a.png");
	}
}
