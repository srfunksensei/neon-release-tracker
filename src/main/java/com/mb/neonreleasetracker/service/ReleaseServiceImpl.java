package com.mb.neonreleasetracker.service;

import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.dto.ReleaseSearchDto;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.repository.ReleaseRepository;
import com.mb.neonreleasetracker.repository.specification.CustomReleaseSpecificationsBuilder;
import com.mb.neonreleasetracker.repository.specification.ReleaseSpecificationBuilder;
import com.mb.neonreleasetracker.util.SearchOperation;
import com.mb.neonreleasetracker.util.SpecSearchCriteria;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReleaseServiceImpl implements ReleaseService {

	private static final Pattern CUSTOM_SEARCH_PATTERN = Pattern.compile("(\\w+?)([:<>~!])(\\w+?|([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))),");
	
	private static final ModelMapper MODEL_MAPPER = new ModelMapper();

	private final ReleaseRepository releaseRepository;

	@Autowired
	public ReleaseServiceImpl(final ReleaseRepository releaseRepository) {
		this.releaseRepository = releaseRepository;
		
		final Converter<Optional<Long>, Long> optToLong = new AbstractConverter<Optional<Long>, Long>() {
			@Override
			protected Long convert(Optional<Long> arg0) {
				return arg0.orElse(null);
			}
		};
		MODEL_MAPPER.addConverter(optToLong);
		
		final Converter<ReleaseStatus, ReleaseStatus> statusConverter = context -> {
			final ReleaseStatus status = context.getSource();
			return status != null ? status : context.getDestination();
		};
	    MODEL_MAPPER.addConverter(statusConverter);
	}

	@Override
	public Optional<Release> findOne(final String id) {
		return releaseRepository.findById(id);
	}

	@Override
	public Page<Release> findAll(final String searchQuery, final Pageable pageable) {
		final CustomReleaseSpecificationsBuilder builder = new CustomReleaseSpecificationsBuilder();

		final Matcher matcher = CUSTOM_SEARCH_PATTERN.matcher(searchQuery + ",");
		while (matcher.find()) {
			final String key = matcher.group(1);
			final SearchOperation op = SearchOperation.getSimpleOperation(matcher.group(2).charAt(0));
			Object value;

			if (key.equals("status")) {
				value = ReleaseStatus.valueOf(matcher.group(3));
			} else {
				value = matcher.group(3);
			}

			final SpecSearchCriteria criteria = new SpecSearchCriteria(key, op, value);
			builder.with(criteria);
		}

		return releaseRepository.findAll(builder.build(), pageable);
	}

	@Override
	public Page<Release> findAll(final ReleaseSearchDto searchDto, final Pageable pageable) {
		return releaseRepository.findAll(ReleaseSpecificationBuilder.build(searchDto), pageable);
	}

	@Override
	public Release create(final ReleaseDto releaseDto) {
		final Release entity = MODEL_MAPPER.map(releaseDto, Release.class);
		entity.setStatus(ReleaseStatus.CREATED);

		return releaseRepository.save(entity);
	}

	@Override
	public Optional<Release> update(final String id, final ReleaseDto releaseDto) {
		final Optional<String> releaseIdOpt = releaseDto.getReleaseId();

		if (!releaseIdOpt.isPresent()) {
			return Optional.empty();
		}

		final Optional<Release> releaseOpt = releaseRepository.findById(releaseIdOpt.get());
		if (!releaseOpt.isPresent()) {
			return Optional.empty();
		}

		final Release release = releaseOpt.get();
		MODEL_MAPPER.map(releaseDto, release);

		return Optional.of(releaseRepository.save(release));
	}

	@Override
	public void deleteOne(final String releaseId) {
		if (releaseRepository.existsById(releaseId)) {
			releaseRepository.deleteById(releaseId);
		}
	}
}
