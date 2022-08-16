package com.radak.database.entities;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.JoinColumn;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="user")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	private int id;
	@Column(name="email",unique=true)
	private String email;
	@Column(name="password",nullable=false)
	private String password;
	@Column(name = "username", nullable = false, unique = true)
    private String username;
	@Column(name = "first_name")
    private String firstName;
	@Column(name="last_name")
	private String lastName;
	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns =@JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles=new HashSet<>();
	@OneToMany(fetch=FetchType.EAGER,mappedBy="user",cascade=CascadeType.ALL)
	@JsonManagedReference
	private Set<Post> posts=new HashSet<>();
	@Column(name="review")
	private int review;
	public void addPosts(Set<Post> articles) {
	    this.posts = articles;
	    articles.forEach(article -> article.setUser(this));
	}
	public void replacePosts(Post post) {
		for (Post p:this.getPosts()) {
			if(p.getId()==post.getId()) {
				posts.remove(p);
				posts.add(post);
			}
		}
	}
	public void deletePost(Post post) {
		posts.remove(post);
		post.setUser(null);
	}
	public boolean isAdmin() {
		for (Role r:roles) {
			if (r.getName().equals("Admin")) {
				return true;
			}
		}
		return false;
	}
}
