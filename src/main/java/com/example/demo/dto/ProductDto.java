package com.example.demo.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

	@Size(min = 3,max = 50,message = "* Name Should be 3~50 Characters.")
	private String name;
	
	@Size(min = 15,max = 100,message = "* Description Should be 15~100 Characters.")
	private String description;
	
	private MultipartFile image;
	
	@Min(value = 100,message = "* Minimum Product Price Should be 100rs.")
	@Max(value = 100000,message = "* Maximum Product Should be 1,00,000rs.")
	private double price;
	
	@Min(value = 1,message = "* Atleast 1 Stock is Required.")
	@Max(value = 100,message = "* At Max 100 Stocks are available.")
	private int stock;
	
	@NotEmpty(message = "Category is Mandatory.")
	private String category;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public MultipartFile getImage() {
		return image;
	}

	public double getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public String getCategory() {
		return category;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
