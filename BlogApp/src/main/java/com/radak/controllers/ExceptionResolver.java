package com.radak.controllers;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.exceptions.UserRegistrationException;
import com.radak.exceptions.YourAccountIsBlocked;
@ControllerAdvice
@Component
public class ExceptionResolver {
	@ExceptionHandler(value = { SomethingWentWrongException.class })
	public String handleSomethingWentWrongException(SomethingWentWrongException exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "SomethingWentWrong";
	}
	@ExceptionHandler(value = { OutOfAuthorities.class })
	public String handleOutOfAuthorities(OutOfAuthorities exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "OutOfAuthorities";
	}
	@ExceptionHandler(value = { YourAccountIsBlocked.class })
	public String youAreBlocked(YourAccountIsBlocked exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "Youareblocked";
	}
	@ExceptionHandler(value = { UserRegistrationException.class })
	public String invalidRegisterData(UserRegistrationException exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "invalidRegistration";
	}
	
}
