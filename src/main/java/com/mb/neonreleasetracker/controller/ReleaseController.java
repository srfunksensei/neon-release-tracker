package com.mb.neonreleasetracker.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mb.neonreleasetracker.assembler.resource.release.ReleaseResource;
import com.mb.neonreleasetracker.assembler.resource.release.ReleaseResourceAssemblerSupport;
import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.service.ReleaseService;

@RestController
@RequestMapping(value = "/releases", produces = "application/hal+json")
public class ReleaseController {
	
	private ReleaseService releaseService;
	
	private ReleaseResourceAssemblerSupport releaseResourceAssembler;
	private PagedResourcesAssembler<Release> pagedAssembler;
	
	@Autowired
	public ReleaseController(final ReleaseService releaseService, 
			final ReleaseResourceAssemblerSupport releaseResourceAssembler,
			final PagedResourcesAssembler<Release> pagedAssembler) {
		this.releaseService = releaseService;
		this.releaseResourceAssembler = releaseResourceAssembler;
		this.pagedAssembler = pagedAssembler;
	}

	@GetMapping
	public HttpEntity<PagedResources<ReleaseResource>> findAll( //
			@RequestParam(value = "search", required = false) String search, //
			Pageable pageable) {
		final Page<Release> releases = releaseService.findAll(search, pageable);
		return new ResponseEntity<>(pagedAssembler.toResource(releases, releaseResourceAssembler), HttpStatus.OK);
	}
	
	@GetMapping(value = "/{releaseId}")
	public ResponseEntity<ReleaseResource> findOne(@PathVariable String releaseId) {
		final Optional<ReleaseResource> resourceOpt = releaseService.findOne(releaseId).map(releaseResourceAssembler::toResource);
		return resourceOpt //
				.map(ResponseEntity::ok) //
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping(value = "/create")
	public ResponseEntity<ReleaseResource> create(@Valid @RequestBody ReleaseDto releaseDto) {
		final Release release = releaseService.create(releaseDto);
		return new ResponseEntity<>(releaseResourceAssembler.toResource(release), HttpStatus.CREATED);
	}

	@PutMapping(value = "/update")
	public ResponseEntity<ReleaseResource> update(@Valid @RequestBody ReleaseDto releaseDto) {
		final Optional<Release> releaseOpt = releaseService.update(releaseDto.getReleaseId().get(), releaseDto);
		
		if (!releaseOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		return new ResponseEntity<>(releaseResourceAssembler.toResource(releaseOpt.get()), HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{releaseId}")
	public void deleteOne(@PathVariable String releaseId) {
		releaseService.deleteOne(releaseId);
	}
}
