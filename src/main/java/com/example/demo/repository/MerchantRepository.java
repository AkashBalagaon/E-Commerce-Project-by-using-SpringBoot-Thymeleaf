package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Merchant;

public interface MerchantRepository extends JpaRepository<Merchant, Long>{

	boolean existsByEmail(String email);

	Merchant findByEmail(String email);

}
