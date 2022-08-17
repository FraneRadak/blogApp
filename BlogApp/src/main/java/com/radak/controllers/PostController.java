package com.radak.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.radak.database.entities.Post;
import com.radak.database.entities.User;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.services.CategoryService;
import com.radak.services.CommentService;
import com.radak.services.PostService;
import com.radak.services.UserService;
import com.radak.utils.FileUploadUtil;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {
	private PostService postService;
	private UserService userService;
	private CategoryService categoryService;
	private CommentService commentService;

	@GetMapping("/create")
	public String createPost(Model model) {
		var post = new Post();
		var categories = categoryService.getAllCategories();
		model.addAttribute("post", post);
		model.addAttribute("categories", categories);
		return "addPost";
	}

	@GetMapping("/share/{postId}")
	public String sharePost(@PathVariable("postId") int postId) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var newPost = new Post();
		var oldPost = postService.getById(postId);
		if (oldPost == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		newPost.setTitle(oldPost.getTitle());
		newPost.setBody(oldPost.getBody());
		newPost.setPhotoPath(oldPost.getPhotoPath());
		newPost.setCategory(oldPost.getCategory());
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar = Calendar.getInstance();
		newPost.setCreateDate(formatter.format(calendar.getTime()));
		String username = ((UserDetails) principal).getUsername();
		User user = null;
		try {
			user = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var posts = user.getPosts();
		posts.add(newPost);
		user.addPosts(posts);
		userService.add(user);
		return "redirect:/home/";
	}

	@PostMapping("/save")
	public String add(@ModelAttribute("post") Post post, @RequestParam("image") MultipartFile multipartFile,
			BindingResult result) {
		String uploadDir = "photos";
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String fullFilePath = "/photos/" + fileName;
			post.setPhotoPath(fullFilePath);
			try {
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			post.setPhotoPath("none");
		}
		String username = ((UserDetails) principal).getUsername();
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar = Calendar.getInstance();
		post.setCreateDate(formatter.format(calendar.getTime()));
		User user = null;
		try {
			user = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var posts = user.getPosts();
		posts.add(post);
		user.addPosts(posts);
		userService.add(user);
		return "redirect:/home/";
	}

	@GetMapping("/")
	public String getAll(Model model) {
		return "allPosts";
	}

	@GetMapping("/{id}")
	public String postDetails(@PathVariable("id") int postId, Model model) {
		var post = postService.getById(postId);
		if (post == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		model.addAttribute("post", post);
		return "postDetails";
	}

	@GetMapping("/like/{id}")
	public String like(@PathVariable("id") int postId) {
		var post = postService.getById(postId);
		if (post == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		User user = null;
		try {
			user = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		post.getLikes().add(user);
		postService.add(post);
		// model.addAttribute("post", post);
		return "redirect:/home/";
	}

	@GetMapping("/dislike/{id}")
	public String dislike(@PathVariable("id") int postId) {
		var post = postService.getById(postId);
		if (post == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		User user = null;
		try {
			user = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		post.getLikes().remove(user);
		postService.add(post);
		return "redirect:/home/";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") int postId) {
		var post = postService.getById(postId);
		if (post == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		User currentUser = null;
		try {
			currentUser = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		if (!post.isOwnedBy(currentUser)) {
			throw new OutOfAuthorities("You dont have enough authorities to do this");
		}
		post.getLikes().clear();
		var user = post.getUser();
		user.deletePost(post);
		if (!post.getPhotoPath().equals("none")) {
			if (!postService.isPictureReferenced(post.getPhotoPath(), postId)) {
				String path = post.getPhotoPath();
				Path fileToDeletePath = Paths.get(path.substring(1));
				try {
					Files.delete(fileToDeletePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		postService.delete(post);
		return "redirect:/home/";
	}

	@GetMapping("/update/{id}")
	public String updatePost(@PathVariable("id") int postId, Model model) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		User user = null;
		try {
			user = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var post = postService.getById(postId);
		if (post == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		if (!post.isOwnedBy(user)) {
			throw new OutOfAuthorities("You dont have enough authorities to do this");
		}
		var categories=categoryService.getAllCategories();
		model.addAttribute("post", post);
		return "updatePost";
	}

	@PostMapping("/saveupdated")
	public String addUpdated(@ModelAttribute("post") Post post, @RequestParam("image") MultipartFile multipartFile,
			BindingResult result) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		User user = null;
		try {
			user = userService.findByUsername(username);
		} catch (IndexOutOfBoundsException e) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var oldPost = postService.getById(post.getId());
		if (oldPost == null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		if (!oldPost.isOwnedBy(user)) {
			throw new OutOfAuthorities("You dont have enough authorities to do this");
		}
		oldPost.setBody(post.getBody());
		oldPost.setTitle(post.getTitle());
		String uploadDir = "photos";
		if(!multipartFile.isEmpty()) {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String fullFilePath = "/photos/" + fileName;
		oldPost.setPhotoPath(fullFilePath);
		try {
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		else {
			oldPost.setPhotoPath("none");
		}
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar = Calendar.getInstance();
		user.replacePosts(oldPost);
		userService.add(user);
		return "redirect:/home/";
	}

	@ExceptionHandler(value = { OutOfAuthorities.class })
	public String handleOutOfAuthorities(OutOfAuthorities exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "OutOfAuthorities";
	}
	@ExceptionHandler(value = { SomethingWentWrongException.class })
	public String handleSomethingWentWrongException(SomethingWentWrongException exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "SomethingWentWrong";
	}
}
