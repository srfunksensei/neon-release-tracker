package com.mb.neonreleasetracker.service;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.repository.ReleaseRepository;

@RunWith(MockitoJUnitRunner.class)
public class ReleaseServiceImplTest {

	@Mock
	private ReleaseRepository releaseRepositoryMock;

	private ReleaseService releaseService;

	@Before
	public void setup() {
		releaseService = new ReleaseServiceImpl(releaseRepositoryMock);
	}

	@Test
	public void testFindOne_noReleaseFound() {
		final Long id = 1L;
		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.empty());

		final Optional<Release> actual = releaseService.findOne(id);
		Assert.assertEquals(Optional.empty(), actual);
	}

	@Test
	public void testFindOne() {
		final Long id = 1L;
		final Release release = createRelease(id);

		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.of(release));

		final Optional<Release> actualOpt = releaseService.findOne(id);
		final Release actual = actualOpt.get();

		Assert.assertNotNull(actual);
		Assert.assertEquals(id, actual.getId());
		Assert.assertEquals(release.getTitle(), actual.getTitle());
		Assert.assertEquals(release.getDescription(), actual.getDescription());
		Assert.assertEquals(release.getStatus(), actual.getStatus());
		Assert.assertEquals(release.getCreatedDate(), actual.getCreatedDate());
		Assert.assertEquals(release.getUpdatedDate(), actual.getUpdatedDate());
		Assert.assertEquals(release.getReleaseDate(), actual.getReleaseDate());
	}

	@Test
	public void testCreate_releaseDateNull() {
		final Long id = 1L;
		final Release release = createRelease(id);
		
		when(releaseRepositoryMock.save(Mockito.any(Release.class))).thenReturn(release);
		
		final ReleaseDto dto = new ReleaseDto(Optional.empty(), release.getTitle(), release.getDescription(), null, null);

		final Release actual = releaseService.create(dto);
		Assert.assertNotNull(actual.getId());
		Assert.assertEquals(ReleaseStatus.CREATED, actual.getStatus());
		Assert.assertNotNull(actual.getCreatedDate());
		Assert.assertNotNull(actual.getUpdatedDate());
		Assert.assertNull(actual.getReleaseDate());
	}

	@Test
	public void testCreate_releaseDateNotNull() {
		final Long id = 1L;
		final LocalDate twoWeeksFromNow = LocalDate.now().plusWeeks(2);
		final Release release = createRelease(id);
		release.setReleaseDate(twoWeeksFromNow);
		
		when(releaseRepositoryMock.save(Mockito.any(Release.class))).thenReturn(release);

		final ReleaseDto dto = new ReleaseDto(Optional.empty(), release.getTitle(), release.getDescription(), null, twoWeeksFromNow);

		final Release actual = releaseService.create(dto);
		Assert.assertNotNull(actual.getId());
		Assert.assertEquals(release.getTitle(), actual.getTitle());
		Assert.assertEquals(release.getDescription(), actual.getDescription());
		Assert.assertEquals(ReleaseStatus.CREATED, actual.getStatus());
		Assert.assertNotNull(actual.getCreatedDate());
		Assert.assertNotNull(actual.getUpdatedDate());
		Assert.assertEquals(twoWeeksFromNow, actual.getReleaseDate());
	}

	@Test
	public void testUpdate_noReleaseIdProvided() {
		final String title = "title", description = "description";

		final ReleaseDto dto = new ReleaseDto(Optional.empty(), title, description, null, null);

		final Optional<Release> actual = releaseService.update(dto);
		Assert.assertEquals(Optional.empty(), actual);
	}

	@Test
	public void testUpdate_noReleaseFound() {
		final Long id = 1L;
		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.empty());

		final String title = "title", description = "description";

		final ReleaseDto dto = new ReleaseDto(Optional.of(id), title, description, null, null);

		final Optional<Release> actual = releaseService.update(dto);
		Assert.assertEquals(Optional.empty(), actual);
	}

	@Test
	public void testFullUpdate() {
		final Long id = 1L;
		final Release release = createRelease(id);

		when(releaseRepositoryMock.findById(id)).thenReturn(Optional.of(release));
		
		final String title = "titleNew", description = "descriptionNew";
		final Release releaseUpdated = createRelease(id);
		releaseUpdated.setTitle(title);
		releaseUpdated.setDescription(description);
		releaseUpdated.setStatus(ReleaseStatus.IN_DEVELOPMENT);
		when(releaseRepositoryMock.save(Mockito.any(Release.class))).thenReturn(releaseUpdated);

		final ReleaseDto dto = new ReleaseDto(Optional.of(id), title, description,
				ReleaseStatus.IN_DEVELOPMENT, null);

		final Optional<Release> actualOpt = releaseService.update(dto);
		final Release actual = actualOpt.get();

		Assert.assertNotNull(actual);
		Assert.assertEquals(id, actual.getId());
		Assert.assertEquals(title, actual.getTitle());
		Assert.assertEquals(description, actual.getDescription());
		Assert.assertEquals(ReleaseStatus.IN_DEVELOPMENT, actual.getStatus());
		Assert.assertEquals(release.getCreatedDate(), actual.getCreatedDate());
		Assert.assertNotNull(actual.getUpdatedDate());
		Assert.assertNull(actual.getReleaseDate());
	}

	private Release createRelease(final Long id) {
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
