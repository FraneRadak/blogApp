package com.radak.controllers;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.radak.database.entities.Comment;
import com.radak.database.entities.Post;
import com.radak.database.entities.User;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.exceptions.YourAccountIsBlocked;
import com.radak.service.impl.Util;
import com.radak.services.CategoryService;
import com.radak.services.CommentService;
import com.radak.services.PostService;
import com.radak.services.UserService;
import com.radak.utils.StateReminder;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class HomeController {
	private PostService postService;
	private CategoryService categoryService;
	private UserService userService;
	private CommentService commentService;
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String home(Model model,@RequestParam("page") Optional<Integer> page,@RequestParam("size") Optional<Integer> size,@RequestParam("sort") Optional<String> sortId,@RequestParam("filter") Optional<String> filterId) {
		var stateReminder=new StateReminder();
		stateReminder.setState(sortId, filterId);
		int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);
        Page<Post> postPage=postService.findPaginated(PageRequest.of(currentPage-1, pageSize),stateReminder.getSort(),stateReminder.getFilter());
		var comment=new Comment();
		var post=new Post();
		var categories=categoryService.getAllCategories();
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var user=userService.getValidUser(((UserDetails)principal).getUsername());
		model.addAttribute("currentUser",user);
		model.addAttribute("posts", postPage);
		model.addAttribute("comment", comment);
		model.addAttribute("post", post);
		model.addAttribute("categories", categories);
		int totalPages=postPage.getTotalPages();
		if (totalPages>0) {
			var pageNumbers=Util.getPageNumbers(totalPages);
	        model.addAttribute("pageNumbers", pageNumbers);
		}
		return "home.html";
	}
	
	@GetMapping("/mycomments")
	public String getMyComments(Model model) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.getValidUser(username);
		var myComments=commentService.findMyComments(username);
		model.addAttribute("comments", myComments);
		model.addAttribute("currentUser", user);
		return "mycomments";
	}
	@GetMapping("/appCorner")
	public String appCorner(Model model) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.getValidUser(username);
		model.addAttribute("numOfPosts", postService.getNum());
		model.addAttribute("currentUser", user);
		model.addAttribute("appSum", userService.getAppSum());
		model.addAttribute("numOfUsers",userService.numOfUsers());
		model.addAttribute("averageMark", userService.getAverageMark());
		return "appCorner";
	}
	@PostMapping("/leavereview")
	public String setReview(Model model,@RequestParam("mark") String mark) {
		int intMark=Integer.parseInt(mark);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.getValidUser(username);
		user.setReview(intMark);
		userService.add(user);
		return "redirect:/appCorner";
	}
}
