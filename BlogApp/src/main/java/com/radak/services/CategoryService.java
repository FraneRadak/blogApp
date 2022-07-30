package com.radak.services;

import java.util.List;

import com.radak.database.entities.Category;

public interface CategoryService {
	void addCategory(Category category);
	List<Category> getAllCategories();
	Category getById(int id);
	void deleteById(int id);
}
