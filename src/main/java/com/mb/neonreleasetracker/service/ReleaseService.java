package com.mb.neonreleasetracker.service;

import java.util.Optional;

import com.mb.neonreleasetracker.dto.ReleaseSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.model.Release;

public interface ReleaseService {

	Optional<Release> findOne(final String id);
	Page<Release> findAll(final String searchQuery, final Pageable pageable);
	Page<Release> findAll(final ReleaseSearchDto searchDto, final Pageable pageable);

	Release create(final ReleaseDto release);
	Optional<Release> update(final String id, final ReleaseDto releaseDto);
	void deleteOne(final String id);
}
