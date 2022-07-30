package com.radak.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.radak.database.entities.Category;
import com.radak.database.entities.Post;
import com.radak.database.repositories.CategoryRepository;
import com.radak.database.repositories.PostRepository;
import com.radak.services.CategoryService;
import com.radak.services.PostService;

import lombok.AllArgsConstructor;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private CategoryService categoryService;

	@Override
	public void add(Post post) {
		postRepository.save(post);
	}

	@Override
	public List<Post> getAll() {
		return postRepository.findAll();
	}

	@Override
	public Post getById(int id) {
		Optional<Post> optional = postRepository.findById(id);
		Post post = null;
		if (optional.isPresent()) {
			post = optional.get();
		}
		return post;
	}

	@Override
	public void delete(Post post) {
		postRepository.delete(post);
	}

	@Override
	public void sort(List<Post> posts, int sortId) {

		switch (sortId) {
		case 1:
			Collections.sort(posts, new Comparator<Post>() {
				DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm a");

				public int compare(Post post1, Post post2) {
					try {
						return dateFormat.parse(post2.getCreateDate())
								.compareTo(dateFormat.parse(post1.getCreateDate()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return 1;
				}
			});
			break;
		case 2:
			Collections.sort(posts, new Comparator<Post>() {
				DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy HH:mm a");

				public int compare(Post post1, Post post2) {
					try {
						return dateFormat.parse(post1.getCreateDate())
								.compareTo(dateFormat.parse(post2.getCreateDate()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return 1;
				}
			});
			break;
		case 3:
			Collections.sort(posts, new Comparator<Post>() {
				public int compare(Post post1, Post post2) {
					return post1.getLikes().size() > post2.getLikes().size() ? 1 : 0;
				}
			});
			break;
		}
	}

	@Override
	public boolean isPictureReferenced(String path, int postId) {
		List<Post> posts = postRepository.findAll();
		return posts.stream().filter(post -> (post.getId() != postId))
				.filter(post -> (post.getPhotoPath().equals(path))).count() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<Post> findPaginated(Pageable pageable,int sortId,int filterId) {
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Post> posts = new ArrayList<>();
		Page<Post> postPage = null;
		if (filterId ==0) {
			if(sortId==1) {
			postPage = postRepository.findAllByOrderByCreateDateDesc(pageable);
			}
			else if (sortId==2) {
				postPage = postRepository.findAllByOrderByCreateDate(pageable);
			}
		} 
		else {
			var category = categoryService.getById(filterId);
			postPage = postRepository.findByCategory(category,pageable);
		}
		for (Post post : postPage) {
			posts.add(post);
			System.out.println(post.getCategory().getName());
		}
		Page<Post> postPage2 = new PageImpl<Post>(posts, PageRequest.of(currentPage, pageSize), postRepository.count());
		return postPage2;
	}

	@Override
	public long getNum() {
		return postRepository.count();
	}
};