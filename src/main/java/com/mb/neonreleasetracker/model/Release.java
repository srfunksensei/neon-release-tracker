package com.mb.neonreleasetracker.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "release", indexes = { @Index(columnList = "title", name = "release_title_idx") })
@Getter
@Setter
@NoArgsConstructor
public class Release {
	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "created_date", updatable = false, nullable = false)
	@CreatedDate
	private LocalDate createdDate;

	@Column(name = "updated_date", nullable = false)
	@LastModifiedDate
	private LocalDate updatedDate;
	
	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "release_date")
	private LocalDate releaseDate;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private ReleaseStatus status;
}
