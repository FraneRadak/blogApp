package com.radak.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radak.database.entities.Category;
import com.radak.database.repositories.CategoryRepository;
import com.radak.services.CategoryService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
@Service
public class CategoryServiceImpl implements CategoryService  {
	@Autowired
	private  CategoryRepository categoryRepository;

	@Override
	public void addCategory(Category category) {
		categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Category getById(int id) {
		Optional <Category> optional=categoryRepository.findById(id);
		Category category=null;
		if (optional.isPresent()) {
			category=optional.get();
		}
		return category;
	}

	@Override
	public void deleteById(int id) {
	categoryRepository.deleteById(id);
	}
}
