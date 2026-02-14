package com.shop.order.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.order.dto.CheckoutResponse;
import com.shop.order.dto.OrderResponse;
import com.shop.order.mapper.OrderMapper;
import com.shop.order.repository.OrderRepository;
import com.shop.order.service.CheckoutTxService;
import com.shop.order.service.OrderService;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final CheckoutTxService checkoutTxService;
	private final OrderRepository orderRepo;
	private final OrderMapper orderMapper;

	@Override
	public CheckoutResponse checkout(String email) {
		int max = 3;
		for (int attempt = 1; attempt <= max; attempt++) {
			try {
				return checkoutTxService.checkoutOnce(email); 
			} catch (OptimisticLockException e) { // nếu không khớp version
				handleOptimisticRetry(attempt, max);
			}
		}
		throw new IllegalStateException("Unexpected retry flow");
	}

	@Override
	public List<OrderResponse> myOrders(String email) {
		return orderRepo.findMyOrdersWithItems(email).stream()
		        .map(orderMapper::toOrderRes).toList();
	}

	private void handleOptimisticRetry(int attempt, int max) {
		if (attempt == max) {
			throw new OptimisticLockException();
		}
		sleep(attempt);
	}
	
	@Override
	public List<OrderResponse> getAllOrders() {
		return orderRepo.findAll().stream().map(orderMapper::toOrderRes).toList();
	}

	private void sleep(int attempt) {
		try {
			Thread.sleep(80L * attempt);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}	
		
	
	
}
