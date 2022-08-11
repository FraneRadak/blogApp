package com.radak.database.entities;

import java.beans.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="post")
public class Post {
	 @Id
	 @GeneratedValue(strategy = GenerationType.AUTO)
	 @Column(name = "post_id")
	 private int id;
	 
	 @Column(name = "title", nullable = false)
	 private String title;
	 
	 @Column(name = "body", columnDefinition = "TEXT")
	 private String body;
	 
	 @Column(name = "create_date", nullable = false, updatable = false)
	 private String createDate;
	 
	 @ManyToOne(fetch = FetchType.LAZY, optional = true)
	 @JoinColumn(name = "user_id", referencedColumnName = "user_id")
	 @JsonBackReference
	 private User user;
	 
	 @OneToMany(fetch=FetchType.EAGER,mappedBy="post",cascade=CascadeType.ALL)
	 @JsonManagedReference
	 private Set<Comment> comments=new HashSet<>();
	 
	 @ManyToOne
	 @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
	 private Category category;
	 
	 @ManyToMany(cascade = CascadeType.ALL)
	 @JoinTable(name = "post_user", joinColumns =@JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
	 private Set<User> likes=new HashSet<>();
	 
	 @Column(nullable = true,length=60)
	 private String photoPath;
	 public void addComments(Set<Comment> articles) {
		    this.comments = articles;
		    articles.forEach(article -> article.setPost(this));
		}
	 public boolean isLiked(User user) {
		 return likes.contains(user);
	 }
}
