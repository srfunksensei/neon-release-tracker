package com.mb.neonreleasetracker.assembler.resource.release;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseResource extends ResourceSupport {
	private String releaseId;
	private String title;
	private String description;
	private ReleaseStatus status;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate createdDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate updatedDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate releaseDate;
}
