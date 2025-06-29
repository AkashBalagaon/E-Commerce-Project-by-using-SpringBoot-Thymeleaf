package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Customer;

public interface CartRepository  extends JpaRepository<Cart, Long>{

	Cart findByCustomer(Customer customer);

}
