
package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.dto.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String category;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false)
	private Double price;
	
	@Column(nullable = false)
	private String imageUrl;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Column(nullable = false)
	private Integer stock;
	
	@UpdateTimestamp
	private LocalDateTime createdTime;
	
	private String reason;
	
	@ManyToOne
	Merchant merchant;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public Double getPrice() {
		return price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Status getStatus() {
		return status;
	}

	public Integer getStock() {
		return stock;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public String getReason() {
		return reason;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	
}
