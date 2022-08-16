package com.radak.database.entities;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "comment")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "comment_id")
	private int id;

	@Column(name = "body", columnDefinition = "TEXT")
	private String body;

	@Column(name = "create_date", nullable = false, updatable = false)
	private String createDate;

	@ManyToOne
	@JoinColumn(name = "post_id", referencedColumnName = "post_id")
	@JsonBackReference
	private Post post;

	String author;
	public boolean isOwnedBy(User user) {
		return (this.getAuthor().equals(user.getUsername()) || user.isAdmin());
	}
}
