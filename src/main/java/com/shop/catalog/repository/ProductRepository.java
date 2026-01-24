package com.shop.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shop.catalog.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	Page<Product> findByIsActiveTrue(Pageable pageable);
	Page<Product> findByCategory_IdAndIsActiveTrue(Integer categoryId, Pageable pageable);

	@Query(
	        value = """
	            select p
	            from Product p
	            join p.category c
	            where p.isActive = true
	              and (:cat is null or c.id = :cat)
	              and (
	                   :q is null
	                   or lower(p.name) like lower(concat('%', :q, '%'))
	              )
	        """,
	        countQuery = """
	            select count(p)
	            from Product p
	            join p.category c
	            where p.isActive = true
	              and (:cat is null or c.id = :cat)
	              and (
	                   :q is null
	                   or lower(p.name) like lower(concat('%', :q, '%'))
	              )
	        """
	    )
	    Page<Product> searchActive(@Param("q") String q, @Param("cat") Integer cat, Pageable pageable);
}
