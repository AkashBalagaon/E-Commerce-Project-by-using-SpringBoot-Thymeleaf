package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Customer;
import com.example.demo.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long>{

	List<Orders> findByCustomer(Customer customer);

}
