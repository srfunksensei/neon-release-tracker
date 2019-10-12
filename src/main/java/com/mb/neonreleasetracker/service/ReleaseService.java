package com.mb.neonreleasetracker.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.model.Release;

public interface ReleaseService {

	public Optional<Release> findOne(Long filmId);
	public Page<Release> findAll(String search, Pageable pageable);
		
	public Release create(final ReleaseDto release);
	public Optional<Release> update(final ReleaseDto releaseDto);
	public void deleteOne(final Long releaseId);
}
