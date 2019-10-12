package com.mb.neonreleasetracker.dto;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mb.neonreleasetracker.model.ReleaseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReleaseDto {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Optional<Long> releaseId;
	
	@NotNull
	@NotBlank(message = "Title is mandatory")
	private String title;
	
	private String description;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ReleaseStatus status;
	
	@JsonIgnore
    private final LocalDate updatedDate = LocalDate.now();
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDate releaseDate;
}
