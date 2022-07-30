package com.radak.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radak.database.entities.Category;
import com.radak.database.entities.Comment;
import com.radak.database.repositories.CommentRepository;
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
		List<Comment> comments=new ArrayList();
		for (Comment comment:commentRepository.findAll()) {
			System.out.println(username+"-"+comment.getAuthor());
			if(comment.getAuthor().equals(username)) {
				System.out.println("found");
				comments.add(comment);
			}
		}
		return comments;
	}
}
