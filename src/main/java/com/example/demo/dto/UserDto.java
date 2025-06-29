package com.example.demo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	
	@Size(min=5,max = 15,message = "* Name Should be 5 ~ 15 Characters")
	private String name;
	
	@NotEmpty(message = "* Email is Required")
	@Email(message = "* Check Email Format")
	private String email;
	
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",message = "* Password Should Contain atleast 8 Characters,One UpperCase,One LowerCassae,One Number and One Special Characters.")
	private String password;
	
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",message = "* Password Should Contain atleast 8 Characters,One UpperCase,One LowerCassae,One Number and One Special Characters.")
	private String confirmPassword;
	
	@AssertTrue(message = "* Check Terms and Conditions in Order to Proceed")
	private boolean terms;
	
	
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public boolean isTerms() {
		return terms;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public void setTerms(boolean terms) {
		this.terms = terms;
	}
	
	
	

}
