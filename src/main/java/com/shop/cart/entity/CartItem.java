package com.shop.cart.entity;

import java.math.BigDecimal;

import com.shop.catalog.entity.Product;
import com.shop.common.ErrorCode;
import com.shop.common.exception.ApiException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = { "cart_id", "product_id" }))
@Getter
@Setter
@NoArgsConstructor
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cart_id")
	private Cart cart;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private int qty;

	@Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice;

	public CartItem(Cart cart, Product product, int qty, BigDecimal unitPrice) {
		this.cart = cart; // owning side: FK nằm ở đây
		this.product = product;
		this.qty = qty;
		this.unitPrice = unitPrice;
	}

	public BigDecimal computeLineTotal() {
		return unitPrice.multiply(BigDecimal.valueOf(qty));
	}

	public void increaseQty(int qty) {
		validateQty(qty);
		this.qty += qty;

	}

	public void changeQty(int qty) {
		validateQty(qty);
		this.qty = qty;

	}

	private void validateQty(int qty) {
		if (qty <= 0)
			throw new ApiException(ErrorCode.ERR_BAD_REQUEST, 
					"Quantity must be greater than zero");		
	}

	public void detach() {
		this.cart = null;
	}

}
