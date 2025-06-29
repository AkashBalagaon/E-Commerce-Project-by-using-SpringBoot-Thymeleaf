package com.example.demo.helper;

import com.example.demo.controller.AdminController;
import com.example.demo.controller.CustomerController;
import com.example.demo.dto.UserDto;

import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.context.Context;


import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;

@Component
public class EmailSender {

	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	TemplateEngine engine;
	
	@Value("${spring.mail.username}")
	String from;
	
	
	public void sendEmail(UserDto userDto,int otp)
	{ 
		Context context=new Context();
		context.setVariable("otp", otp);
		context.setVariable("name", userDto.getName());
		
		String text=engine.process("email-template.html", context);
		
		try {
			MimeMessage message=mailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(message);
			
			helper.setFrom(from,"Ecommerce Application");
			helper.setTo(userDto.getEmail());
			helper.setSubject("OTP Verification");
			helper.setText(text,true);
			
			mailSender.send(message);
		}
		catch(Exception e)
		{
			System.err.println("OTP Sending to Email Failed but the otp is:" +otp);
		}
	}
}
