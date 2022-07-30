package com.radak.database.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.radak.database.entities.Category;
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
