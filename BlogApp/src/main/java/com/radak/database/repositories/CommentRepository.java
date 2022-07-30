package com.radak.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.radak.database.entities.Comment;
@Repository
@Component
public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
