package com.shop.cart.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shop.cart.dto.request.AddCartItemRequest;
import com.shop.cart.dto.request.UpdateCartItemQtyRequest;
import com.shop.cart.dto.response.CartItemResponse;
import com.shop.cart.dto.response.CartResponse;
import com.shop.cart.entity.Cart;
import com.shop.cart.entity.CartItem;
import com.shop.cart.repository.CartItemRepository;
import com.shop.cart.repository.CartRepository;
import com.shop.catalog.entity.Product;
import com.shop.catalog.repository.ProductRepository;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
	
	private static final String EMAIL = "test@gmail.com";
	
	@Mock
	CartRepository cartRepo;
	@Mock
	CartItemRepository itemRepo;
	@Mock
	ProductRepository productRepo;
	
	@InjectMocks
	private CartServiceImpl cartService;
	
	@Test
	void getMyCart_should_throw_when_cart_not_found() {
		when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.empty());
		assertThrows(ApiException.class, () -> cartService.getMyCart(EMAIL));
		// Verify
	    verifyNoInteractions(itemRepo);
	}
	
	@Test
	void getMyCart_should_return_empty_cart_when_cart_exists_but_has_no_items() {
	    // Arrange
	    Cart cart = new Cart(1);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
	    when(itemRepo.findByCartId(cart.getId())).thenReturn(List.of());

	    // Act
	    CartResponse res = cartService.getMyCart(EMAIL);

	    // Assert
	    assertNotNull(res);
	    assertEquals(cart.getId(), res.getCartId());
	    assertTrue(res.getItems().isEmpty());
	    assertEquals(BigDecimal.ZERO, res.getTotal());
	}
	
	@Test
	void getMyCart_should_return_cart_with_items_and_total() {
	    // Arrange
	    Cart cart = new Cart(1);

	    Product product = new Product();
	    product.setId(1);
	    product.setName("p");

	    CartItem item = new CartItem(cart, product, 2, BigDecimal.valueOf(50));
	    item.setId(10);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
	    when(itemRepo.findByCartId(cart.getId())).thenReturn(List.of(item));

	    // Act
	    CartResponse res = cartService.getMyCart(EMAIL);

	    // Assert
	    assertNotNull(res);
	    assertEquals(cart.getId(), res.getCartId());
	    assertEquals(1, res.getItems().size());
	    
	    
	    CartItemResponse resItem = res.getItems().get(0);
	    assertEquals(item.getId(), resItem.getItemId());
	    assertEquals(item.getProduct().getId(), resItem.getProductId());
	    assertEquals(item.getProduct().getName(), resItem.getProductName());
	    assertEquals(item.getUnitPrice(), resItem.getUnitPrice());
	    assertEquals(item.getQty(), resItem.getQty());
	    assertEquals(BigDecimal.valueOf(100), resItem.getLineTotal());

	    assertEquals(BigDecimal.valueOf(100), res.getTotal());
	}
	
	@Test
	void addItem_should_throw_when_cart_not_found() {
	    // Arrange
	    AddCartItemRequest req = new AddCartItemRequest(1, 2);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.empty());

	    // Act + Assert
	    assertThrows(ApiException.class, () -> cartService.addItem(EMAIL, req));

	    // Verify
	    verifyNoInteractions(productRepo);
	}
	
	
	@Test
    void addItem_should_throw_when_product_not_found() {		
		// Arrange
        AddCartItemRequest req = new AddCartItemRequest(1, 2);
        Cart cart = mock(Cart.class);

        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1)).thenReturn(Optional.empty());

        // Act
        ApiException ex = assertThrows(ApiException.class, () -> cartService.addItem(EMAIL, req));
        
        // Assert
        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getCode());

        // Verify
        verify(cart, never()).getQtyOfItem(anyInt());
        verify(cart, never()).addProduct(any(), anyInt(), any());
	}
	
	@Test
	void addItem_should_throw_when_product_is_inactive() {
	    // Arrange
	    String email = "test@gmail.com";
	    AddCartItemRequest req = new AddCartItemRequest(1, 2);

	    Cart cart = mock(Cart.class);
	    Product product = new Product();
	    product.setIsActive(false);

	    when(cartRepo.findByUserEmail(email)).thenReturn(Optional.of(cart));
	    when(productRepo.findById(req.getProductId())).thenReturn(Optional.of(product));

	    // Act + Assert
	    ApiException ex = assertThrows(ApiException.class, () -> cartService.addItem(email, req));
	    assertEquals(ErrorCode.ERR_BAD_REQUEST, ex.getCode());
	    
	    verify(cart, never()).getQtyOfItem(anyInt());
	    verify(cart, never()).addProduct(any(), anyInt(), any());
	
	}
	
	@Test
	void addItem_should_throw_when_not_enough_stock() {
	    // Arrange
	    AddCartItemRequest req = new AddCartItemRequest(1, 2);

	    Cart cart = mock(Cart.class);

	    Product product = new Product();
	    product.setId(1);
	    product.setIsActive(true);
	    product.setStock(3);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
	    when(productRepo.findById(req.getProductId())).thenReturn(Optional.of(product));
	    when(cart.getQtyOfItem(product.getId())).thenReturn(2); // total = 2 + 2 = 4 > 3

	    // Act + Assert
	    assertThrows(ApiException.class, () -> cartService.addItem(EMAIL, req));
	    verify(cart, never()).addProduct(any(), anyInt(), any());
	}

	@Test
    void addItem_should_add_item_when_product_is_active_and_stock_is_enough() {
        // Arrange
        AddCartItemRequest req = new AddCartItemRequest(1, 2);

        Cart cart = org.mockito.Mockito.mock(Cart.class);

        Product product = new Product();
        product.setId(1);
        product.setIsActive(true);
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(100));

        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(cart.getQtyOfItem(1)).thenReturn(3);

        // Act + Assert
        assertDoesNotThrow(() -> cartService.addItem(EMAIL, req));

        verify(cart).addProduct(product, req.getQty(), product.getPrice());
    }
	
	@Test
	void updateQty_should_throw_when_cart_not_found() {
	    // Arrange
	    Integer itemId = 1;
	    UpdateCartItemQtyRequest req = new UpdateCartItemQtyRequest(3);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.empty());

	    // Act + Assert
	    assertThrows(ApiException.class, () -> cartService.updateQty(EMAIL, itemId, req));

	    verifyNoInteractions(itemRepo);
	}
	
	@Test
	void updateQty_should_throw_when_item_not_found() {
	    // Arrange
	    Integer itemId = 1;
	    UpdateCartItemQtyRequest req = new UpdateCartItemQtyRequest(3);

	    Cart cart = new Cart(10);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
	    when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

	    // Act + Assert
	    assertThrows(ApiException.class, () -> cartService.updateQty(EMAIL, itemId, req));

	}
	
	@Test
    void updateQty_should_throw_when_item_does_not_belong_to_user_cart() {
        // Arrange
        String email = "test@gmail.com";
        Integer itemId = 1;
        UpdateCartItemQtyRequest req = new UpdateCartItemQtyRequest(3);

        Cart cart = new Cart(10);      

        Cart anotherCart = new Cart(20);

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setQty(1);
        item.setCart(anotherCart);

        when(cartRepo.findByUserEmail(email)).thenReturn(Optional.of(cart));
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        // Act + Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> cartService.updateQty(email, itemId, req));

        assertEquals(ErrorCode.ERR_FORBIDDEN, ex.getCode());
        assertEquals("It's not your cart", ex.getMessage());
        assertEquals(1, item.getQty());
    }
	
	@Test
	void updateQty_should_throw_when_item_belongs_to_user_cart_but_qty_invalid() {
	    // Arrange
	    Integer itemId = 1;
	    UpdateCartItemQtyRequest req = new UpdateCartItemQtyRequest(0);

	    Cart cart = new Cart(10);

	    CartItem item = new CartItem();
	    item.setId(itemId);
	    item.setQty(2);
	    item.setCart(cart);

	    when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
	    when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

	    // Act + Assert
	    ApiException ex = assertThrows(ApiException.class,
	            () -> cartService.updateQty(EMAIL, itemId, req));

	    assertEquals(ErrorCode.ERR_BAD_REQUEST, ex.getCode());
	}

    @Test
    void updateQty_should_update_qty_when_item_belongs_to_user_cart() {
        // Arrange
        Integer itemId = 1;
        UpdateCartItemQtyRequest req = new UpdateCartItemQtyRequest(5);

        Cart cart = new Cart(10);

        CartItem item = new CartItem();
        item.setId(itemId);
        item.setQty(1);
        item.setCart(cart);

        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        // Act
        cartService.updateQty(EMAIL, itemId, req);

        // Assert
        assertEquals(req.getQty(), item.getQty());
    }
    
    @Test
    void removeItem_should_throw_when_cart_not_found() {
        // Arrange
        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.empty());

        // Act + Assert
        ApiException ex = assertThrows(ApiException.class,
                () -> cartService.removeItem(EMAIL, 1));

        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getCode());

        verifyNoInteractions(itemRepo);
    }
    
    @Test
    void removeItem_should_throw_when_item_not_found() {
        // Arrange
        Cart cart = new Cart(1);

        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
        when(itemRepo.findById(1)).thenReturn(Optional.empty());

        // Act
        ApiException ex = assertThrows(ApiException.class,
                () -> cartService.removeItem(EMAIL, 1));

        // Assert
        assertEquals(ErrorCode.ERR_NOT_FOUND, ex.getCode());

        // Verify
        verify(itemRepo, never()).delete(any());
    }
    
    @Test
    void removeItem_should_throw_when_item_not_belong_to_user_cart() {
        // Arrange
        Cart userCart = new Cart(1);

        Cart anotherCart = new Cart(2);

        CartItem item = new CartItem();
        item.setId(1);
        item.setCart(anotherCart);

        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(userCart));
        when(itemRepo.findById(1)).thenReturn(Optional.of(item));

        // Act
        ApiException ex = assertThrows(ApiException.class,
                () -> cartService.removeItem(EMAIL, 1));

        // Assert
        assertEquals(ErrorCode.ERR_FORBIDDEN, ex.getCode());

        // Verify
        verify(itemRepo, never()).delete(any());
    }
    
    @Test
    void removeItem_should_delete_item_when_valid() {
        // Arrange
        Cart cart = new Cart(1);

        CartItem item = new CartItem();
        item.setId(1);
        item.setCart(cart);

        when(cartRepo.findByUserEmail(EMAIL)).thenReturn(Optional.of(cart));
        when(itemRepo.findById(1)).thenReturn(Optional.of(item));

        // Act
        cartService.removeItem(EMAIL, 1);

        // Assert
        // không throw exception

        // Verify
        verify(itemRepo).delete(item);
    }
	
	
}
