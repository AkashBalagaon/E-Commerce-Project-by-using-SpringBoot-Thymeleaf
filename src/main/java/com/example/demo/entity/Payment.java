package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.dto.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private Long id;
	
	@Column(nullable = false)
	private Double amount;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	@CreationTimestamp
	private LocalDateTime createdTime;
	
	@Column(nullable = false,unique = true)
	private String paymentId;
	
	@OneToOne
	Orders orders;
	
	
	
	public Long getId() {
		return id;
	}
	public Double getAmount() {
		return amount;
	}
	public PaymentStatus getStatus() {
		return status;
	}
	public LocalDateTime getCreatedTime() {
		return createdTime;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public Orders getOrders() {
		return orders;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public void setStatus(PaymentStatus status) {
		this.status = status;
	}
	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public void setOrders(Orders orders) {
		this.orders = orders;
	}
	
	
}
