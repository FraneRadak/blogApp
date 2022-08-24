package com.radak.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radak.database.entities.Category;
import com.radak.database.entities.Comment;
import com.radak.database.entities.User;
import com.radak.database.repositories.CommentRepository;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.services.CommentService;

import lombok.AllArgsConstructor;
@Service
public class CommentServiceImpl implements CommentService{
	@Autowired
	private CommentRepository commentRepository;

	@Override
	public Comment getById(int id) {
		Optional <Comment> optional=commentRepository.findById(id);
		Comment comment=null;
		if (optional.isPresent()) {
			comment=optional.get();
		}
		else {
			throw new SomethingWentWrongException("Comment is not found,please try again later");
		}
		return comment;
	}
	@Override
	public void save(Comment comment) {
		commentRepository.save(comment);
	}
	@Override
	public void deletRelatedComments(int postId) {
	var comments=commentRepository.findAll();
	for(Comment comment:comments) {
		if(comment.getPost().getId()==postId) {
			System.out.println("deleted");
			commentRepository.delete(comment);
		}
	}
		
	}

	@Override
	public void deleteComment(Comment comment) {
		commentRepository.delete(comment);
		
	}

	@Override
	public List<Comment> findMyComments(String username) {
		return commentRepository.findByAuthor(username);
	}

	@Override
	public Comment setDefaultCommentData(User user,Comment comment) {
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar=Calendar.getInstance();
		comment.setCreateDate(formatter.format(calendar.getTime()));
		comment.setAuthor(user.getUsername());
		return comment;
	}

	@Override
	public Comment getValidComment(int commentId,User user) {
		var comment=this.getById(commentId);
		if(!comment.isOwnedBy(user)) {
			throw new OutOfAuthorities("You dont have enough authorities to do this");
		}
		return comment;
	}
}
