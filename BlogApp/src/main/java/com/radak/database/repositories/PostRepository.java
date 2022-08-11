package com.radak.database.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.radak.database.entities.Category;
import com.radak.database.entities.Post;
//@Repository
//@Component
@Transactional
public interface PostRepository extends JpaRepository<Post, Integer>{
	Page<Post> findAllByOrderByCreateDateDesc(Pageable pageable);
	//Page<Post> findAllCategory(Pageable pageable,Category category);
	Page<Post> findAllByOrderByCreateDate(Pageable pageable);
	Page<Post> findByCategory(Category category,Pageable pageable);
}
