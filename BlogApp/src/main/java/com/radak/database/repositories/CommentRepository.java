package com.radak.database.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.radak.database.entities.Comment;
//@Repository
//@Component
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<Comment> findByAuthor(String author);
}
