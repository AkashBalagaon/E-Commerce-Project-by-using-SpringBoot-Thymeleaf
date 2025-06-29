package com.example.demo.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.dto.OrderStatus;
import com.example.demo.dto.PaymentStatus;
import com.example.demo.dto.Status;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Customer;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Orders;
import com.example.demo.entity.Payment;
import com.example.demo.entity.Product;
import com.example.demo.helper.AES;
import com.example.demo.helper.EmailSender;
import com.example.demo.helper.RazorPayHelper;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerServiceImpl implements CustomerService{

    
	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	MerchantRepository merchantRepository;
	
	@Autowired
	EmailSender emailSender;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	OrderItemRepository itemRepository;
	
	@Autowired
	RazorPayHelper payHelper;
	
	@Value("${razor-pay.api.key}")
	String key;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	PaymentRepository paymentRepository;


	@Override
	public String register(UserDto userDto, Model model) {
		
		model.addAttribute("userDto",userDto);
		return "customer-register.html";
	}

	@Override
	public String register(UserDto userDto, BindingResult result,HttpSession session) {
		
		if(!userDto.getPassword().equals(userDto.getConfirmPassword()))
		{
			result.rejectValue("confirmPassword", "error.confirmPassword","* Password and Confirm Password Not Matching.");
		}
		
		if(adminRepository.existsByEmail(userDto.getEmail()) || customerRepository.existsByEmail(userDto.getEmail())
				|| merchantRepository.existsByEmail(userDto.getEmail()))
		{
			result.rejectValue("email", "error.email","* Email Already Exists.");
		}
		
		if(result.hasErrors())
		{
			return "customer-register.html";
		}
		
		int otp=new Random().nextInt(100000,1000000);
		emailSender.sendEmail(userDto, otp);
		
		session.setAttribute("otp", otp);
		session.setAttribute("userDto", userDto);
		session.setAttribute("pass", "Otp Sent Success");
		
		return "redirect:/customer/otp";
	}

	@Override
	public String submitOtp(int otp, HttpSession session) {
		int generatedOtp=(int) session.getAttribute("otp");
		
		if(generatedOtp==otp)
		{
			UserDto dto=(UserDto) session.getAttribute("userDto");
			Customer customer=new Customer();
			customer.setEmail(dto.getEmail());
			customer.setName(dto.getName());
			customer.setPassword(AES.encrypt(dto.getPassword()));
			customerRepository.save(customer);
			session.setAttribute("pass", "Account Created SuccessFully.");
			session.removeAttribute("otp");
			session.removeAttribute("userDto");
			return "redirect:/";
		}
		else {
			session.setAttribute("fail", "Otp MisMatch");
			return "redirect:/customer/otp";
		}
	}

	@Override
	public String loadHome(HttpSession session) {
	
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			return "customer-home.html";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access");
			return "redirect:/login";
		}
	}

	@Override
	public String viewProducts(HttpSession session, Model model, String category, String sort, String search) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			List<Product> products=null;
			
			if(category == null && search == null && sort == null)
			{
				products=productRepository.findByStatus(Status.APPROVED,Sort.by("name").ascending());
			}
				
				if(sort != null)
				{
					if(sort.equals("newest"))
					{
						products=productRepository.findByStatus(Status.APPROVED,Sort.by("createdTime").descending());
					}
					else {
						String[] split=sort.split("_");
						if(split[1].equals("asc"))
							products=productRepository.findByStatus(Status.APPROVED,Sort.by(split[0]).ascending());
						else 
							products=productRepository.findByStatus(Status.APPROVED,Sort.by(split[0]).descending());
					}
				}
				
				if(category != null)
				{
					products=productRepository.findByStatusAndCategory(Status.APPROVED,category,Sort.by("name").ascending());
				}
				
				if(search != null)
				{
					products=productRepository.findByStatusAndNameLike(Status.APPROVED,"%"+search+"%",Sort.by("name").ascending());
				}
				
				if(products.isEmpty())
				{
					session.setAttribute("fail", "No Products Added Yet.");
					return "redirect:/customer/home";
				}
				else {
					model.addAttribute("products",products);
					return "view-products.html";
				}
			}
			else {
				session.setAttribute("fail", "Invalid Session , First Login to Access.");
				return "redirect:/login";
			}
	}

	@Override
	public String addToCart(Long id, HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			Product product=productRepository.findById(id).orElseThrow();
			if(product.getStock() > 0)
			{
				Cart cart=cartRepository.findByCustomer(customer);
				if(cart == null)
				{
					cart=new Cart();
					cart.setCustomer(customer);
					cartRepository.save(cart);
				}
				List<OrderItem> items=itemRepository.findByCart(cart);
				if(items.isEmpty())
				{
					OrderItem item=new OrderItem();
					item.setQuantity(1);
					item.setPrice(product.getPrice());
					item.setProduct(product);
					item.setCart(cart);
					itemRepository.save(item);
					session.setAttribute("pass", product.getName() +" added to Cart SuccessFully.");
				}
				else {
					boolean flag=true;
					for(OrderItem item: items)
					{
						if(item.getProduct() == product)
						{
							if(product.getStock() > item.getQuantity())
							{
								item.setQuantity(item.getQuantity() +1);
								itemRepository.save(item);
								session.setAttribute("pass", product.getName() + " was already in cart increased quantity Successfully.");
							}
							else {
								session.setAttribute("fail", product.getName() + " is already present and out of Stock.");
							}
							flag=false;
							break;
						}
					}
					
					if(flag)
					{
						OrderItem item=new OrderItem();
						item.setQuantity(1);
						item.setPrice(product.getPrice());
						item.setProduct(product);
						item.setCart(cart);
						itemRepository.save(item);
						session.setAttribute("pass", product.getName() +" Added to Cart SuccessFully.");
					}
				}
			}
			return "redirect:/customer/products";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}	
	}

	@Override
	public String viewCart(HttpSession session, Model model) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			Cart cart=cartRepository.findByCustomer(customer);
			if(cart == null)
			{
				session.setAttribute("fail", "No Items in Cart.");
				return "redirect:/customer/home";
			}
			else {
				List<OrderItem> items=itemRepository.findByCart(cart);
				if(items.isEmpty())
				{
					session.setAttribute("fail", "No Items in Cart.");
					return "redirect:/customer/home";
				}
				else {
					model.addAttribute("items",items);
					model.addAttribute("total",items.stream().mapToDouble(x->x.getPrice() * x.getQuantity()).sum());
					return "view-cart.html";
				}
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String increaseQuantity(Long id, HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		if(customer != null)
		{
			OrderItem item=itemRepository.findById(id).orElseThrow();
			if(item.getQuantity() < item.getProduct().getStock())
			{
				item.setQuantity(item.getQuantity() + 1);
				itemRepository.save(item);
				session.setAttribute("pass", "Quantity Increased SuccessFully.");
				return "redirect:/customer/cart";
			}
			else {
				session.setAttribute("fail", "Out of Stocks.");
				return "redirect:/customer/cart";
			}
		}
		else {
		    session.setAttribute("fail", "Invalid Session , First Login to Access.");
		    return "redirect:/login";
	  }
	}

	@Override
	public String decreaseQuantity(Long id, HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			OrderItem item=itemRepository.findById(id).orElseThrow();
			
			if(item.getQuantity() > 1)
			{
				item.setQuantity(item.getQuantity() -1);
				itemRepository.save(item);
				session.setAttribute("pass", "Quantity Decreased SuccessFully.");
				return "redirect:/customer/cart";
			}
			else {
				itemRepository.delete(item);
				session.setAttribute("pass", "Quantity Decreased SuccessFully.");
				return "redirect:/customer/cart";
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String proceedPayment(HttpSession session, Model model) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			if(customer.getAddress() == null || customer.getMobile() == null)
			{
				session.setAttribute("fail", "First add Address and Mobile Number in Manage Profile.");
				return "redirect:/customer/manage-profile";
			}
			else {
				Cart cart=cartRepository.findByCustomer(customer);
				if(cart == null)
				{
					session.setAttribute("fail", "No Items in Cart.");
					return "redirect:/customer/home";
				}
				else {
					List<OrderItem> items=itemRepository.findByCart(cart);
					if(items.isEmpty())
					{
						session.setAttribute("fail", "No Items in Cart.");
						return "redirect:/customer/home";
					}
					else {
						double amount=items.stream().mapToDouble(x->x.getPrice() * x.getQuantity()).sum();
						String orderId=payHelper.createPayment(amount);
						
						Orders order=new Orders();
						order.setCustomer(customer);
						order.setOrderStatus(OrderStatus.PLACED);
						order.setPaymentStatus(PaymentStatus.PENDING);
						order.setTotalAmount(amount);
						order.setAddress(customer.getAddress());
						order.setMobile(customer.getMobile());
						orderRepository.save(order);
						
						model.addAttribute("address",customer.getAddress());
						model.addAttribute("mobile",customer.getMobile());
						model.addAttribute("key",key);
						model.addAttribute("amount",amount*100);
						model.addAttribute("orderId",orderId);
						model.addAttribute("url","/customer/payment/"+order.getId());
						
						return "payment.html";
					}
				}
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
		
	}

	@Override
	public String confirmPayment(Long id, String paymentId, HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			Orders order = orderRepository.findById(id).orElseThrow();
			
			if(paymentId != null)
			{
				order.setPaymentStatus(PaymentStatus.PAID);
			}
			else {
				order.setPaymentStatus(PaymentStatus.FAILED);
			}
			
			orderRepository.save(order);
			Payment payment=new Payment();
			
			payment.setAmount(order.getTotalAmount());
			payment.setOrders(order);
			payment.setPaymentId(paymentId);
			payment.setStatus(PaymentStatus.PAID);
			paymentRepository.save(payment);
			
			Cart cart=cartRepository.findByCustomer(customer);
			
			List<OrderItem> items=itemRepository.findByCart(cart);
			for(OrderItem item : items)
			{
				item.setCart(null);
				item.setOrders(order);
				itemRepository.save(item);
				Product product=item.getProduct();
				product.setStock(product.getStock() - item.getQuantity());
				productRepository.save(product);
			}
			session.setAttribute("pass", "Payment SuccessFully and Order Placed.");
			return "redirect:/customer/home";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String manageProfile(HttpSession session, Model model) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		if(customer != null)
		{
			model.addAttribute("name",customer.getName());
			model.addAttribute("address",customer.getAddress());
			model.addAttribute("mobile",customer.getMobile());
			return "customer-manage-profile";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String manageProfile(HttpSession session, UserDto dto, Long mobile, String address) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			if (dto.getPassword().length() > 0 && !dto.getPassword().matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$"))
			{
				session.setAttribute("fail", "Password is Not Strong Enough Should be 8 Characters with One UpperCase , LowerCase,Special Characters and one digits.");
				return "redirect:/customer/manage-profile";
			}
			else {
				customer.setMobile(mobile);
				customer.setAddress(address);
				customer.setName(dto.getName());
				
				if(dto.getPassword().length() > 0)
				{
					customer.setPassword(AES.encrypt(dto.getPassword()));
				}
				customerRepository.save(customer);
				session.setAttribute("pass", "Profile Updated SuccessFully.");
				return "redirect:/customer/home";
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String orderHistory(HttpSession session, Model model) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			List<Orders> orders=orderRepository.findByCustomer(customer);
			
			if(orders.isEmpty())
			{
				session.setAttribute("fail", "No Orders Yet.");
				return "redirect:/customer/home";
			}
			else{
				model.addAttribute("orders",orders);
				return "order-history.html";
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String loadTrackOrder(HttpSession session) {
		
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			return "track-order.html";
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}

	@Override
	public String trackOrders(Long orderId, HttpSession session, Model model) {
	
		Customer customer=(Customer) session.getAttribute("customer");
		
		if(customer != null)
		{
			System.out.println("Order Id : "+orderId);
			Orders order=orderRepository.findById(orderId).orElse(null);
			
			if(order == null)
			{
				session.setAttribute("fail", "Invalid OrderId.");
				return "redirect:/customer/track-orders";
			}
			
			if(order.getCustomer().getId() == customer.getId())
			{
				model.addAttribute("order",order);
				return "track-order.html";
			}
			else {
				session.setAttribute("fail", "Invalid Order Id.");
				return "redirect:/customer/track-orders";
			}
		}
		else {
			session.setAttribute("fail", "Invalid Session , First Login to Access.");
			return "redirect:/login";
		}
	}
	
	
}
