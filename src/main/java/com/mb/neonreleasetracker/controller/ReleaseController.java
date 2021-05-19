package com.mb.neonreleasetracker.controller;

import com.mb.neonreleasetracker.assembler.resource.release.ReleaseModel;
import com.mb.neonreleasetracker.assembler.resource.release.ReleaseResourceAssemblerSupport;
import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.dto.ReleaseSearchDto;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.service.ReleaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/releases", produces = "application/hal+json")
public class ReleaseController {

	private final ReleaseService releaseService;

	private final ReleaseResourceAssemblerSupport releaseResourceAssembler;
	private final PagedResourcesAssembler<Release> pagedAssembler;

	@GetMapping("/custom-search")
	public HttpEntity<PagedModel<ReleaseModel>> customSearch( //
															  @RequestParam(value = "query", required = false) final String query, //
															  final Pageable pageable) {
		final Page<Release> releases = releaseService.findAll(query, pageable);
		return new ResponseEntity<>(pagedAssembler.toModel(releases, releaseResourceAssembler), HttpStatus.OK);
	}

	@GetMapping
	public HttpEntity<PagedModel<ReleaseModel>> search( //
                                                            @RequestParam(value = "title", required = false) final String title, //
                                                            @RequestParam(value = "description", required = false) final String description, //

                                                            @RequestParam(name = "createdDateAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateAfter, //
                                                            @RequestParam(name = "createdDateBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateBefore, //

                                                            @RequestParam(name = "updatedDateAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedDateAfter, //
                                                            @RequestParam(name = "updatedDateBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate updatedDateBefore, //

                                                            @RequestParam(name = "releaseDateAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateAfter, //
                                                            @RequestParam(name = "releaseDateBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateBefore, //

                                                            @RequestParam(value = "inStatus", required = false) List<ReleaseStatus> inStatus, //
                                                            @RequestParam(value = "notInStatus", required = false) List<ReleaseStatus> notInStatus, //

                                                            final Pageable pageable) {
		final ReleaseSearchDto searchDto = ReleaseSearchDto.builder()
				.title(title)
				.description(description)
				.createdDateAfter(createdDateAfter)
				.createdDateBefore(createdDateBefore)
				.updatedDateAfter(updatedDateAfter)
				.updatedDateBefore(updatedDateBefore)
				.releaseDateAfter(releaseDateAfter)
				.releaseDateBefore(releaseDateBefore)
				.inStatus(inStatus)
				.notInStatus(notInStatus)
				.build();

		final Page<Release> releases = releaseService.findAll(searchDto, pageable);
		return new ResponseEntity<>(pagedAssembler.toModel(releases, releaseResourceAssembler), HttpStatus.OK);
	}

	@GetMapping(value = "/{releaseId}")
	public ResponseEntity<ReleaseModel> findOne(@PathVariable final String releaseId) {
		final Optional<ReleaseModel> resourceOpt = releaseService.findOne(releaseId).map(releaseResourceAssembler::toModel);
		return resourceOpt //
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ReleaseModel> create(@Valid @RequestBody final ReleaseDto releaseDto) {
		final Release release = releaseService.create(releaseDto);
		return new ResponseEntity<>(releaseResourceAssembler.toModel(release), HttpStatus.CREATED);
	}

	@PutMapping(value = "/{releaseId}")
	public ResponseEntity<ReleaseModel> update(@PathVariable final String releaseId, @Valid @RequestBody final ReleaseDto releaseDto) {
		final Optional<Release> releaseOpt = releaseService.update(releaseId, releaseDto);

		return releaseOpt.map(release -> new ResponseEntity<>(releaseResourceAssembler.toModel(release), HttpStatus.OK)).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping(value = "/{releaseId}")
	public void deleteOne(@PathVariable final String releaseId) {
		releaseService.deleteOne(releaseId);
	}
}
