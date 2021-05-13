package com.mb.neonreleasetracker.controller;

import com.mb.neonreleasetracker.assembler.resource.release.ReleaseResourceAssemblerSupport;
import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.model.Release;
import com.mb.neonreleasetracker.model.ReleaseStatus;
import com.mb.neonreleasetracker.service.ReleaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.HateoasAwareSpringDataWebConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReleaseController.class)
@Import(HateoasAwareSpringDataWebConfiguration.class)
public class ReleaseControllerTest {

	@TestConfiguration
	static class ReleaseControllerTestContextConfiguration {
		@Bean
		public ReleaseResourceAssemblerSupport releaseResourceAssembler() {
			return new ReleaseResourceAssemblerSupport();
		}
	}

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ReleaseService releaseServiceMock;

	@Test
	public void givenAnyFilter_whenGetAllReleases_thenReturnEmptyJsonArray() throws Exception {
		final Page<Release> noReleases = new PageImpl<>(new ArrayList<Release>());

		when(releaseServiceMock.findAll(anyString(), any(Pageable.class))).thenReturn(noReleases);

		mvc.perform(get("/releases") //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(0)));

		verify(releaseServiceMock, times(1)).findAll(anyString(), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnyFilter_whenGetAllReleases_thenReturnJsonArray() throws Exception {
		final Release release1 = createRelease(1L), release2 = createRelease(2L);

		final List<Release> releases = Collections.unmodifiableList(Arrays.asList(release1, release2));
		final Page<Release> releasePages = new PageImpl<>(releases);

		when(releaseServiceMock.findAll(anyString(), any(Pageable.class))).thenReturn(releasePages);

		mvc.perform(get("/releases") //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(releases.size())));

		verify(releaseServiceMock, times(1)).findAll(anyString(), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnyFilter_whenGetAllReleases_thenReturnJsonArrayPaginatedSize() throws Exception {
		final Release release1 = createRelease(1L), release2 = createRelease(2L);

		final List<Release> releases = Collections.unmodifiableList(Arrays.asList(release1, release2));
		final Page<Release> releasePages = new PageImpl<>(releases);

		when(releaseServiceMock.findAll(anyString(), any(Pageable.class))).thenReturn(releasePages);

		final int pageSize = 1;

		mvc.perform(get("/releases") //
				.param("size", "" + pageSize) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(releases.size()))) //
				.andExpect(
						jsonPath("$._embedded.releaseResourceList[0].releaseId", equalTo(release1.getId().intValue())));

		verify(releaseServiceMock, times(1)).findAll(anyString(), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenId_whenGetRelease_thenReturnOk() throws Exception {
		final Release release = createRelease(1L);

		when(releaseServiceMock.findOne(any(Long.class))).thenReturn(Optional.of(release));

		mvc.perform(get("/releases/" + release.getId()) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.releaseId", equalTo(release.getId().intValue()))) //
				.andExpect(jsonPath("$.title", equalTo(release.getTitle()))) //
				.andExpect(jsonPath("$.description", equalTo(release.getDescription()))) //
				.andExpect(jsonPath("$.status", equalTo(release.getStatus().toString())));

		verify(releaseServiceMock, times(1)).findOne(any(Long.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenId_whenGetRelease_thenReturnNotFound() throws Exception {
		when(releaseServiceMock.findOne(any(Long.class))).thenReturn(Optional.empty());

		mvc.perform(get("/releases/1") //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isNotFound());

		verify(releaseServiceMock, times(1)).findOne(any(Long.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenCreateRelease_thenReturnCreated() throws Exception {
		final Release release = createRelease(1L);
		when(releaseServiceMock.create(any(ReleaseDto.class))).thenReturn(release);

		final String jsonDto = "{\"title\" : \"milan12\",\"description\" : \"d2\", \"status\": \""
				+ ReleaseStatus.IN_DEVELOPMENT.toString() + "\"}";

		mvc.perform(post("/releases/create") //
				.content(jsonDto) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isCreated()) //
				.andExpect(jsonPath("$.releaseId", equalTo(release.getId().intValue()))) //
				.andExpect(jsonPath("$.title", equalTo(release.getTitle()))) //
				.andExpect(jsonPath("$.description", equalTo(release.getDescription()))) //
				.andExpect(jsonPath("$.status", equalTo(release.getStatus().toString())));

		verify(releaseServiceMock, times(1)).create(any(ReleaseDto.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenUpdateRelease_thenReturnOk() throws Exception {
		final Release release = createRelease(1L);
		when(releaseServiceMock.update(anyLong(), Mockito.any(ReleaseDto.class))).thenReturn(Optional.of(release));

		final String jsonDto = "{\"releaseId\" : " + release.getId()
				+ ",\"title\" : \"milan12\",\"description\" : \"d2\", \"status\": \""
				+ ReleaseStatus.IN_DEVELOPMENT.toString() + "\"}";
		mvc.perform(put("/releases/update") //
				.content(jsonDto) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isOk());

		verify(releaseServiceMock, times(1)).update(anyLong(), any(ReleaseDto.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenUpdateRelease_thenReturnNotFound() throws Exception {
		when(releaseServiceMock.update(anyLong(), Mockito.any(ReleaseDto.class))).thenReturn(Optional.empty());

		final String jsonDto = "{\"title\" : \"milan12\",\"description\" : \"d2\"}";
		mvc.perform(put("/releases/update") //
				.content(jsonDto) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andExpect(status().isNotFound());

		verify(releaseServiceMock, times(1)).update(anyLong(), any(ReleaseDto.class));
		verifyNoMoreInteractions(releaseServiceMock);
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
