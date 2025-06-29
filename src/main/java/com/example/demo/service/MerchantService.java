package com.example.demo.service;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.dto.ProductDto;
import com.example.demo.dto.UserDto;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface MerchantService {

	String register(UserDto userDto, Model model);

	String register(@Valid UserDto userDto, BindingResult result, HttpSession session);

	String submitOtp(int otp, HttpSession session);

	String loadHome(HttpSession session);

	String loadAddProduct(ProductDto productDto, Model model, HttpSession session);

	String addProduct(@Valid ProductDto productDto, BindingResult result, HttpSession session);

	String manageProducts(HttpSession session, Model model);

	String editProduct(Long id, Model model, HttpSession session);

	String updateProduct(Long id, @Valid ProductDto produDto, BindingResult result, Model model, HttpSession session);

	String deleteById(Long id, HttpSession session);

	String manageProfile(HttpSession session, Model model);

	String manageProfile(HttpSession session, UserDto dto);

}
