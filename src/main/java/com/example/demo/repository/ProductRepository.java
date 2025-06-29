package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.dto.Status;
import com.example.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	List<Product> findByMerchant_id(Long id);

	List<Product> findByStatus(Status approved, Sort ascending);

	List<Product> findByStatusAndCategory(Status approved, String category, Sort ascending);

	List<Product> findByStatusAndNameLike(Status approved, String string, Sort ascending);

}
