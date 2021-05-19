package com.mb.neonreleasetracker.service;

import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.dto.ReleaseSearchDto;
import com.mb.neonreleasetracker.exception.ResourceNotFoundException;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.repository.ReleaseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReleaseServiceImplTest {

	@Mock
	private ReleaseRepository releaseRepositoryMock;

	private ReleaseService releaseService;

	@BeforeEach
	public void setup() {
		releaseService = new ReleaseServiceImpl(releaseRepositoryMock);
	}

	@Test
	public void findOne_noReleaseFound() {
		final String id = "1";
		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.empty());

		final Optional<Release> actual = releaseService.findOne(id);
		Assertions.assertEquals(Optional.empty(), actual);
	}

	@Test
	public void findOne() {
		final String id = "1";
		final Release release = createRelease(id);

		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.of(release));

		final Optional<Release> actualOpt = releaseService.findOne(id);
		Assertions.assertTrue(actualOpt.isPresent());

		final Release actual = actualOpt.get();
		Assertions.assertNotNull(actual);
		Assertions.assertEquals(id, actual.getId());
		Assertions.assertEquals(release.getTitle(), actual.getTitle());
		Assertions.assertEquals(release.getDescription(), actual.getDescription());
		Assertions.assertEquals(release.getStatus(), actual.getStatus());
		Assertions.assertEquals(release.getCreatedDate(), actual.getCreatedDate());
		Assertions.assertEquals(release.getUpdatedDate(), actual.getUpdatedDate());
		Assertions.assertEquals(release.getReleaseDate(), actual.getReleaseDate());
	}

	@Test
	public void create_releaseDateNull() {
		final String id = "1";
		final Release release = createRelease(id);
		
		when(releaseRepositoryMock.save(Mockito.any(Release.class))).thenReturn(release);
		
		final ReleaseDto dto = new ReleaseDto(release.getTitle(), release.getDescription(), null, null);

		final Release actual = releaseService.create(dto);
		Assertions.assertNotNull(actual.getId());
		Assertions.assertEquals(ReleaseStatus.CREATED, actual.getStatus());
		Assertions.assertNotNull(actual.getCreatedDate());
		Assertions.assertNotNull(actual.getUpdatedDate());
		Assertions.assertNull(actual.getReleaseDate());
	}

	@Test
	public void create_releaseDateNotNull() {
		final String id = "1";
		final LocalDate twoWeeksFromNow = LocalDate.now().plusWeeks(2);
		final Release release = createRelease(id);
		release.setReleaseDate(twoWeeksFromNow);
		
		when(releaseRepositoryMock.save(Mockito.any(Release.class))).thenReturn(release);

		final ReleaseDto dto = new ReleaseDto(release.getTitle(), release.getDescription(), null, twoWeeksFromNow);

		final Release actual = releaseService.create(dto);
		Assertions.assertNotNull(actual.getId());
		Assertions.assertEquals(release.getTitle(), actual.getTitle());
		Assertions.assertEquals(release.getDescription(), actual.getDescription());
		Assertions.assertEquals(ReleaseStatus.CREATED, actual.getStatus());
		Assertions.assertNotNull(actual.getCreatedDate());
		Assertions.assertNotNull(actual.getUpdatedDate());
		Assertions.assertEquals(twoWeeksFromNow, actual.getReleaseDate());
	}

	@Test
	public void update_noReleaseIdProvided() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final String title = "title", description = "description";

			final ReleaseDto dto = new ReleaseDto(title, description, null, null);

			releaseService.update(null, dto);
		});
	}

	@Test
	public void update_noReleaseFound() {
		final String id = "1";
		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.empty());

		final String title = "title", description = "description";

		final ReleaseDto dto = new ReleaseDto(title, description, null, null);

		final Optional<Release> actual = releaseService.update(id, dto);
		Assertions.assertEquals(Optional.empty(), actual);
	}

	@Test
	public void update() {
		final String id = "1";
		final Release release = createRelease(id);

		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.of(release));
		
		final String title = "titleNew", description = "descriptionNew";
		final Release releaseUpdated = createRelease(id);
		releaseUpdated.setTitle(title);
		releaseUpdated.setDescription(description);
		releaseUpdated.setStatus(ReleaseStatus.IN_DEVELOPMENT);
		when(releaseRepositoryMock.save(Mockito.any(Release.class))).thenReturn(releaseUpdated);

		final ReleaseDto dto = new ReleaseDto(title, description,
				ReleaseStatus.IN_DEVELOPMENT, null);

		final Optional<Release> actualOpt = releaseService.update(id, dto);
		Assertions.assertTrue(actualOpt.isPresent());

		final Release actual = actualOpt.get();
		Assertions.assertNotNull(actual);
		Assertions.assertEquals(id, actual.getId());
		Assertions.assertEquals(title, actual.getTitle());
		Assertions.assertEquals(description, actual.getDescription());
		Assertions.assertEquals(ReleaseStatus.IN_DEVELOPMENT, actual.getStatus());
		Assertions.assertEquals(release.getCreatedDate(), actual.getCreatedDate());
		Assertions.assertNotNull(actual.getUpdatedDate());
		Assertions.assertNull(actual.getReleaseDate());
	}

	@Test
	public void delete_noReleaseWithProvidedId() {
		when(releaseRepositoryMock.existsById(anyString())).thenReturn(false);

		Assertions.assertThrows(ResourceNotFoundException.class, () -> releaseService.deleteOne("not-existing-id"));
	}

	@Test
	public void delete() {
		when(releaseRepositoryMock.existsById(anyString())).thenReturn(true);

		releaseService.deleteOne("1");
	}

	@Test
	public void findAll_searchString() {
		when(releaseRepositoryMock.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<Release>()));

		final Page<Release> result = releaseService.findAll("status:ON_DEV, title:v0.0.35", Pageable.unpaged());
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	public void findAll_searchDto() {
		when(releaseRepositoryMock.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<Release>()));

		final ReleaseSearchDto searchDto = ReleaseSearchDto.builder()
				.createdDateBefore(LocalDate.now().minusDays(1))
				.createdDateAfter(LocalDate.now().minusDays(5))
				.description("test")
				.title("v0.0.24")
				.inStatus(Stream.of(ReleaseStatus.DONE).collect(Collectors.toList()))
				.build();

		final Page<Release> result = releaseService.findAll(searchDto, Pageable.unpaged());
		Assertions.assertNotNull(result);
		Assertions.assertTrue(result.isEmpty());
	}

	private Release createRelease(final String id) {
		final Release release = new Release();
		release.setId(id);
		release.setCreatedDate(LocalDate.now());
		release.setUpdatedDate(LocalDate.now());
		release.setTitle("title");
		release.setDescription("description");
		release.setStatus(ReleaseStatus.CREATED);
		release.setReleaseDate(null);
		return release;
	}

}
