package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(nullable = false)
	private Double price;
	
	@ManyToOne
	private Orders orders;
	
	@ManyToOne
	private Product product;
	
	@ManyToOne
	Cart cart;
	
	

	public Long getId() {
		return id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getPrice() {
		return price;
	}

	public Orders getOrders() {
		return orders;
	}

	public Product getProduct() {
		return product;
	}

	public Cart getCart() {
		return cart;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}
	
}
