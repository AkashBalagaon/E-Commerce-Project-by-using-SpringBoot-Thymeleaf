package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.dto.OrderStatus;
import com.example.demo.dto.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Orders {
	
	@Id
	@GeneratedValue(generator = "orderId")
	@SequenceGenerator(initialValue = 101001,allocationSize = 1,name = "orderId")
	private Long id;
	
	@Column(nullable = false)
	private Double totalAmount;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus orderStatus;
	
	@CreationTimestamp
	private LocalDateTime creationTime;
	
	@Column(nullable = false)
	private Long mobile;
	
	@Column(nullable = false)
	private String address;
	
	@ManyToOne
	private Customer customer;

	public Long getId() {
		return id;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Long getMobile() {
		return mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
