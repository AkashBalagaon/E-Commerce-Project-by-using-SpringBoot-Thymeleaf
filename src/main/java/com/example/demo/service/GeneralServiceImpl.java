package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Admin;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Merchant;
import com.example.demo.helper.AES;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.MerchantRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class GeneralServiceImpl implements GeneralService{

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	AdminRepository adminRepository;

	@Override
	public String login(String email, String password, HttpSession session) {
		
		session.removeAttribute("admin");
		session.removeAttribute("merchant");
		session.removeAttribute("customer");
		
		Admin admin=adminRepository.findByEmail(email);
		Customer customer=customerRepository.findByEmail(email);
		Merchant merchant=merchantRepository.findByEmail(email);
		
		if(admin==null && merchant==null && customer==null)
		{
			session.setAttribute("fail", "Invalid Email");
			return "redirect:/login";
		}
		
		if(admin != null)
		{
			if(AES.decrypt(admin.getPassword()).equals(password))
			{
				session.setAttribute("admin", admin);
				session.setAttribute("pass","Login Successfully.");
				return "redirect:/admin/home";
			}
			else {
				session.setAttribute("fail", "Invalid Password");
				return "redirect:/login";
			}
		}
		
		if(merchant != null)
		{
			if(AES.decrypt(merchant.getPassword()).equals(password))
			{
				session.setAttribute("merchant", merchant);
				session.setAttribute("pass","Login SuccessFully.");
				return "redirect:/merchant/home";
			}
			else {
				session.setAttribute("fail", "Invalid Password.");
				return "redirect:/login";
			}
		}
		
		if(customer != null)
		{
			if(AES.decrypt(customer.getPassword()).equals(password))
			{
				session.setAttribute("customer", customer);
				session.setAttribute("pass", "Login SuccessFully.");
				return "redirect:/customer/home";
			}
			else {
				session.setAttribute("fail", "Invalid Password");
				return "redirect:/login";
			}
		}
		return "redirect:/login";
	}

	@Override
	public String logout(HttpSession session) {
		
		session.removeAttribute("admin");
		session.removeAttribute("merchant");
		session.removeAttribute("customer");
		session.setAttribute("pass", "Logut SuccessFully.");
		return "redirect:/";
	}
	
	
}
