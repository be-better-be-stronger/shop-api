package com.shop.cart.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.shop.catalog.entity.Product;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;
import com.shop.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@Getter 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();
	
	@Version
	private Integer version;
	
	public Cart(User user) {
        this.user = user;
    }
	
	public Cart(int id) {
		this.id = id;
	}

    public void addProduct(Product product, int qty, BigDecimal unitPrice) {
        Optional<CartItem> existingItem = findItemByProductId(product.getId());
        if (existingItem.isPresent()) {
            existingItem.get().increaseQty(qty);
            return;
        }

        CartItem newItem = new CartItem(this, product, qty, unitPrice);
        this.items.add(newItem);
    }
    
    public int getQtyOfItem(Integer productId) {
    	return findItemByProductId(productId)
    			.map(CartItem::getQty)
    			.orElse(0);
    }

    public void updateProductQty(Integer productId, int qty) {
        CartItem item = findRequiredItem(productId);
        item.changeQty(qty);
    }

    public void removeProduct(Integer productId) {
        CartItem item = findRequiredItem(productId);
        this.items.remove(item);
    }
    
    public void clear() {
        for (CartItem item : new ArrayList<>(items)) {
            items.remove(item);
            item.detach();
        }
    }

  

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQty).sum();
    }

    public BigDecimal getSubtotal() {
        return items.stream()
                .map(CartItem::computeLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Optional<CartItem> findItemByProductId(Integer productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    private CartItem findRequiredItem(Integer productId) {
        return findItemByProductId(productId)
                .orElseThrow(() -> new ApiException(ErrorCode.ERR_NOT_FOUND, "Cart item not found: " + productId));
    }
    
    

   
}
