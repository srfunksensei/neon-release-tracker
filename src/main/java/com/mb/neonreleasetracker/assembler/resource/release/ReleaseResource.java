package com.mb.neonreleasetracker.assembler.resource.release;

import java.time.LocalDate;

import org.springframework.hateoas.ResourceSupport;

import com.mb.neonreleasetracker.model.ReleaseStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReleaseResource extends ResourceSupport {
	private String releaseId;
	private String title;
	private String description;
	private ReleaseStatus status;
	
	private LocalDate createdDate;
	private LocalDate updatedDate;
	private LocalDate releaseDate;
}
