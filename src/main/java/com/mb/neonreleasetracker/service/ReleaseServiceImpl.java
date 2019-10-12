package com.mb.neonreleasetracker.service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.repository.ReleaseRepository;
import com.mb.neonreleasetracker.repository.specification.ReleaseSpecificationsBuilder;
import com.mb.neonreleasetracker.util.SearchOperation;
import com.mb.neonreleasetracker.util.SpecSearchCriteria;

@Service
public class ReleaseServiceImpl implements ReleaseService {

	private static final Pattern pattern = Pattern.compile("(\\w+?)(:|<|>|~|!)(\\w+?|([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))),");
	
	private static final ModelMapper modelMapper = new ModelMapper();

	private ReleaseRepository releaseRepository;

	@Autowired
	public ReleaseServiceImpl(final ReleaseRepository releaseRepository) {
		this.releaseRepository = releaseRepository;
		
		final Converter<Optional<Long>, Long> optToLong = new AbstractConverter<Optional<Long>, Long>() {
			@Override
			protected Long convert(Optional<Long> arg0) {
				return arg0.isPresent() ? arg0.get() : null;
			}
		};
		modelMapper.addConverter(optToLong);
		
		Converter<ReleaseStatus, ReleaseStatus> statusConverter = new Converter<ReleaseStatus, ReleaseStatus>()
	    {
	        public ReleaseStatus convert(MappingContext<ReleaseStatus, ReleaseStatus> context)
	        {
	            final ReleaseStatus s = context.getSource();
	            return s != null ? s : context.getDestination();
	        }
	    };
	    modelMapper.addConverter(statusConverter);
	}
	
	public Optional<Release> findOne(Long releaseId) {
		return releaseRepository.findById(releaseId);
	}

	public Page<Release> findAll(String search, Pageable pageable) {
		final ReleaseSpecificationsBuilder builder = new ReleaseSpecificationsBuilder();
		final Matcher matcher = pattern.matcher(search + ",");
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

	public Release create(final ReleaseDto releaseDto) {
		final Release entity = modelMapper.map(releaseDto, Release.class);
		entity.setCreatedDate(LocalDate.now());
		entity.setStatus(ReleaseStatus.CREATED);

		return releaseRepository.save(entity);
	}

	public Optional<Release> update(final ReleaseDto releaseDto) {
		final Optional<Long> releaseIdOpt = releaseDto.getReleaseId();

		if (!releaseIdOpt.isPresent()) {
			return Optional.empty();
		}

		final Optional<Release> releaseOpt = releaseRepository.findById(releaseIdOpt.get());
		if (!releaseOpt.isPresent()) {
			return Optional.empty();
		}

		final Release release = releaseOpt.get();
		modelMapper.map(releaseDto, release);

		return Optional.of(releaseRepository.save(release));
	}

	public void deleteOne(final Long releaseId) {
		if (releaseRepository.existsById(releaseId)) {
			releaseRepository.deleteById(releaseId);
		}
	}
}
