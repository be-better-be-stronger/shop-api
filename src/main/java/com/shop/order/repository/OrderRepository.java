package com.shop.order.repository;

import com.shop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	@Query("""
			  select distinct o
			  from Order o
			  left join fetch o.items i
			  left join fetch i.product
			  where o.user.email = :email
			  order by o.id desc
			""")
	List<Order> findMyOrdersWithItems(@Param("email") String email);

}
