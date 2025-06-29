package com.example.demo.service;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.dto.UserDto;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

public interface CustomerService {

	String register(UserDto userDto, Model model);

	String register(@Valid UserDto userDto, BindingResult result, HttpSession session);

	String submitOtp(int otp, HttpSession session);

	String loadHome(HttpSession session);

	String viewProducts(HttpSession session, Model model, String category, String sort, String search);

	String addToCart(Long id, HttpSession session);

	String viewCart(HttpSession session, Model model);

	String increaseQuantity(Long id, HttpSession session);

	String decreaseQuantity(Long id, HttpSession session);

	String proceedPayment(HttpSession session, Model model);

	String confirmPayment(Long id, String paymentId, HttpSession session);

	String manageProfile(HttpSession session, Model model);

	String manageProfile(HttpSession session, UserDto dto, Long mobile, String address);

	String orderHistory(HttpSession session, Model model);

	String loadTrackOrder(HttpSession session);

	String trackOrders(Long orderId, HttpSession session, Model model);

}
