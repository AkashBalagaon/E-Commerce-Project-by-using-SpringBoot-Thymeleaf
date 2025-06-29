package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.dto.ProductDto;
import com.example.demo.dto.Status;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Merchant;
import com.example.demo.entity.Product;
import com.example.demo.helper.AES;
import com.example.demo.helper.CloudinaryHelper;
import com.example.demo.helper.EmailSender;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class MerchantServiceImpl implements MerchantService{

	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CloudinaryHelper cloudinaryHelper;
	
	@Autowired
	EmailSender emailSender;


	@Override
	public String register(UserDto userDto, Model model) {
		model.addAttribute("userDto",userDto);
		return "merchant-register.html";
	}

	@Override
	public String register(UserDto userDto, BindingResult result,HttpSession session) {
		
		if(!userDto.getPassword().equals(userDto.getConfirmPassword()))
		{
			result.rejectValue("confirmPassword", "error.confirmPassword","* Password and Confirm Password not Matching.");
		}
		
		if(adminRepository.existsByEmail(userDto.getEmail()) || customerRepository.existsByEmail(userDto.getEmail())
				|| merchantRepository.existsByEmail(userDto.getEmail()))
		{
			result.rejectValue("email", "error.email","* Email Already Exists.");
		}
		
		if(result.hasErrors())
		{
			return "merchant-register.html";
		}


		int otp=new Random().nextInt(100000,1000000);
		emailSender.sendEmail(userDto, otp);
		
		session.setAttribute("otp", otp);
		session.setAttribute("userDto", userDto);
		session.setAttribute("pass", "Otp Sent SuccessFull.");
		
		return "redirect:/merchant/otp";
	}

	@Override
	public String submitOtp(int otp, HttpSession session) {
		
		int generatedOtp=(int) session.getAttribute("otp");
		
		if(generatedOtp==otp)
		{
			UserDto dto=(UserDto) session.getAttribute("userDto");
			Merchant merchant=new Merchant();
			merchant.setEmail(dto.getEmail());
			merchant.setName(dto.getName());
			merchant.setPassword(AES.encrypt(dto.getPassword()));
			merchantRepository.save(merchant);
			session.setAttribute("pass", "Acccount Created SuccessFilly.");
			session.removeAttribute("otp");
			session.removeAttribute("userDto");
			return "redirect:/";
		}
		else {
			session.setAttribute("fail", "Otp MisMatch.");
			return "redirect:/merchant/otp";
		}
	}

	@Override
	public String loadHome(HttpSession session) {
	
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		if(merchant != null)
		{
			return "merchant-home.html";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String loadAddProduct(ProductDto productDto, Model model, HttpSession session) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			model.addAttribute("productDto",productDto);
			return "add-product.html";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String addProduct(@Valid ProductDto productDto, BindingResult result, HttpSession session) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			if(productDto.getImage().isEmpty())
			{
				result.rejectValue("image", "error.image","* Select One Image.");
			}
			
			if(result.hasErrors())
			{
				return "add-product.html";
			}
		    else {
			    Product product=new Product();
			
			    product.setName(productDto.getName());
			    product.setDescription(productDto.getDescription());
			    product.setCategory(productDto.getCategory());
			    product.setStock(productDto.getStock());
			    product.setPrice(productDto.getPrice());
			    product.setImageUrl(cloudinaryHelper.saveImage(productDto.getImage()));
			    product.setMerchant(merchant);
			    product.setStatus(Status.PENDING);
			
			    productRepository.save(product);
			    session.setAttribute("pass", "Product Added Successfully.");
			    return "redirect:/merchant/home";
		    }
	     }
	     else{
		     session.setAttribute("fail", "Invalid Session , First Login to Access.");
		     return "redirect:/login";
	     }
	}

	@Override
	public String manageProducts(HttpSession session, Model model) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			List<Product> products=productRepository.findByMerchant_id(merchant.getId());
			
			if(products.isEmpty())
			{
				session.setAttribute("fail", "No Products Added Yet.");
				return "redirect:/merchant/home";
			}
			else {
				model.addAttribute("products",products);
				return "manage-products.html";
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String editProduct(Long id, Model model, HttpSession session) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			Product product=productRepository.findById(id).orElseThrow();
			
			ProductDto productDto=new ProductDto();
			
			productDto.setName(product.getName());
			productDto.setDescription(product.getDescription());
			productDto.setPrice(product.getPrice());
			productDto.setStock(product.getStock());
			productDto.setCategory(product.getCategory());
			
			model.addAttribute("productDto",productDto);
			model.addAttribute("id",product.getId());
			model.addAttribute("link",product.getImageUrl());
			
			return "edit-product.html";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First login to Access.");
			return "redirect:/login";
		}
		
	}

	@Override
	public String updateProduct(Long id, ProductDto productDto, BindingResult result, Model model,
			HttpSession session) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			Product product=productRepository.findById(id).orElseThrow();
			
			if(result.hasErrors())
			{
				model.addAttribute("id",id);
				model.addAttribute("link",product.getImageUrl());
				return "edit-product.html";
			}
			
			product.setName(productDto.getName());
			product.setDescription(productDto.getDescription());
			product.setStock(productDto.getStock());
			product.setPrice(productDto.getPrice());
			product.setCategory(productDto.getCategory());
			
			try {
				if(productDto.getImage().getInputStream().available() > 0)
				{
					product.setImageUrl(cloudinaryHelper.saveImage(productDto.getImage()));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			productRepository.save(product);
			session.setAttribute("pass", "Product Updated SuccessFully...");
			return "redirect:/merchant/manage-products";
		}
		else{
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
		
	}

	@Override
	public String deleteById(Long id, HttpSession session) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			productRepository.deleteById(id);
			session.setAttribute("pass", "Product Deleted SuccessFully,.");
			return "redirect:/merchant/manage-products";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
		
	}

	@Override
	public String manageProfile(HttpSession session, Model model) {
		
		Merchant merchant=(Merchant) session.getAttribute("merchant");
		
		if(merchant != null)
		{
			model.addAttribute("name",merchant.getName());
			return "merchant-manage-profile";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
		
	}

	@Override
	public String manageProfile(HttpSession session, UserDto dto) {
		
		Merchant merchant=(Merchant)session.getAttribute("merchant");
		
		if(merchant != null)
		{
			merchant.setName(dto.getName());
			
			if(dto.getPassword().length() > 0)
				merchant.setPassword(AES.encrypt(dto.getPassword()));
			merchantRepository.save(merchant);
			session.setAttribute("pass", "Profile Updated Successfully,..");
			return "redirect:/merchant/home";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
		
	}
}
