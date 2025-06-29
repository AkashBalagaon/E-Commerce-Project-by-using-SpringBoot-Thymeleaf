package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Cart {
	
	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private Long id; 
	
	@OneToOne
	Customer customer;

	public Long getId() {
		return id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	

}
