package com.shop.order.service.impl;

import com.shop.common.exception.ApiException;
import com.shop.order.repository.OrderRepository;
import com.shop.order.service.CheckoutTxService;
import com.shop.order.service.OrderService;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final CheckoutTxService checkoutTxService;
	private final OrderRepository orderRepo;

	@Override
	@Transactional
	public Object checkout(String email) {
		int max = 3;
		for (int attempt = 1; attempt <= max; attempt++) {
			try {
				return checkoutTxService.checkoutOnce(email); // đi qua proxy -> có TX
			} catch (OptimisticLockException e) { // nếu không khớp version
				handleOptimisticRetry(attempt, max);
			}
		}
		throw new IllegalStateException("Unexpected retry flow");
	}

	@Override
	public Object myOrders(String email) {
		return orderRepo.findByUserEmailOrderByIdDesc(email);
	}

	private void handleOptimisticRetry(int attempt, int max) {
		if (attempt == max) {
			throw new ApiException(HttpStatus.CONFLICT, "Có người vừa mua trước. Vui lòng thử lại.");
		}
		sleep(attempt);
	}

	private void sleep(int attempt) {
		try {
			Thread.sleep(80L * attempt);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
