package com.radak.services;

import java.util.List;
import java.util.stream.Stream;

import com.radak.database.entities.Comment;
import com.radak.database.entities.User;

public interface CommentService {
	Comment getById(int id);
	void save(Comment comment);
	void deletRelatedComments(int postId);
	void deleteComment(Comment comment);
	List<Comment>findMyComments(String username);
	Comment setDefaultCommentData(User user,Comment comment);
	Comment getValidComment(int commentId,User user);
}
