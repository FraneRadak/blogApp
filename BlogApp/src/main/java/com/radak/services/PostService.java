package com.radak.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.radak.database.entities.Category;
import com.radak.database.entities.Post;
import com.radak.database.entities.User;

public interface PostService {
	void add(Post post);
	List<Post> getAll();
	Post getById(int id);
	void delete(Post post);
	boolean isPictureReferenced(String path,int postId);
	public Page<Post> findPaginated(Pageable pageable, int sortId,int filterId);
	void sort(List<Post> posts,int sortId);
	long getNum();
	Post copyPostData(Post post,Post oldPost);
	Post setPostDefaultData(Post post);
	Post getOwnedPost(int postId,User user);
}
