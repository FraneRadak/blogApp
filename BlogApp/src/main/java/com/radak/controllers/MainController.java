package com.radak.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
public class MainController {
	  @GetMapping("/login")
	    public String login(){
	        return "login";
	    }
	    @GetMapping("/")
	    public String home(){
	        return "redirect:/home/";
	    }
}
