package com.mb.neonreleasetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mb.neonreleasetracker.assembler.resource.release.ReleaseResourceAssemblerSupport;
import com.mb.neonreleasetracker.dto.ReleaseDto;
import com.mb.neonreleasetracker.dto.ReleaseSearchDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReleaseController.class)
@Import(HateoasAwareSpringDataWebConfiguration.class)
public class ReleaseControllerTest {

	private final ObjectMapper mapper = new ObjectMapper();

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
	public void givenAnyCustomSearchFilter_whenGetAllReleases_thenReturnEmptyJsonArray() throws Exception {
		final Page<Release> noReleases = new PageImpl<>(new ArrayList<>());

		when(releaseServiceMock.findAll(anyString(), any(Pageable.class))).thenReturn(noReleases);

		mvc.perform(get("/releases/custom-search") //
				.param("query", "")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(0)));

		verify(releaseServiceMock, times(1)).findAll(anyString(), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnyCustomSearchFilter_whenGetAllReleases_thenReturnJsonArray() throws Exception {
		final Release release1 = createRelease("1"), release2 = createRelease("2");

		final List<Release> releases = Collections.unmodifiableList(Arrays.asList(release1, release2));
		final Page<Release> releasePages = new PageImpl<>(releases);

		when(releaseServiceMock.findAll(anyString(), any(Pageable.class))).thenReturn(releasePages);

		mvc.perform(get("/releases/custom-search") //
				.param("query", "")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(releases.size())));

		verify(releaseServiceMock, times(1)).findAll(anyString(), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnyCustomSearchFilter_whenGetAllReleases_thenReturnJsonArrayPaginatedSize() throws Exception {
		final Release release1 = createRelease("1"), release2 = createRelease("2");

		final List<Release> releases = Collections.unmodifiableList(Arrays.asList(release1, release2));
		final Page<Release> releasePages = new PageImpl<>(releases);

		when(releaseServiceMock.findAll(anyString(), any(Pageable.class))).thenReturn(releasePages);

		final int pageSize = 1;

		mvc.perform(get("/releases/custom-search") //
				.param("size", "" + pageSize) //
				.param("query", "")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(releases.size()))) //
				.andExpect(
						jsonPath("$._embedded.releaseResourceList[0].releaseId", equalTo(release1.getId())));

		verify(releaseServiceMock, times(1)).findAll(anyString(), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnySearchFilter_whenGetAllReleases_thenReturnEmptyJsonArray() throws Exception {
		final Page<Release> noReleases = new PageImpl<>(new ArrayList<>());

		when(releaseServiceMock.findAll(any(ReleaseSearchDto.class), any(Pageable.class))).thenReturn(noReleases);

		mvc.perform(get("/releases") //
				.param("title", "v0.0.34")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(0)));

		verify(releaseServiceMock, times(1)).findAll(any(ReleaseSearchDto.class), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnySearchFilter_whenGetAllReleases_thenReturnJsonArray() throws Exception {
		final Release release1 = createRelease("1"), release2 = createRelease("2");

		final List<Release> releases = Collections.unmodifiableList(Arrays.asList(release1, release2));
		final Page<Release> releasePages = new PageImpl<>(releases);

		when(releaseServiceMock.findAll(any(ReleaseSearchDto.class), any(Pageable.class))).thenReturn(releasePages);

		mvc.perform(get("/releases") //
				.param("title", "v0.0.34")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(releases.size())));

		verify(releaseServiceMock, times(1)).findAll(any(ReleaseSearchDto.class), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenAnySearchFilter_whenGetAllReleases_thenReturnJsonArrayPaginatedSize() throws Exception {
		final Release release1 = createRelease("1"), release2 = createRelease("2");

		final List<Release> releases = Collections.unmodifiableList(Arrays.asList(release1, release2));
		final Page<Release> releasePages = new PageImpl<>(releases);

		when(releaseServiceMock.findAll(any(ReleaseSearchDto.class), any(Pageable.class))).thenReturn(releasePages);

		final int pageSize = 1;

		mvc.perform(get("/releases") //
				.param("size", "" + pageSize) //
				.param("title", "v0.0.34")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.page.totalElements", equalTo(releases.size()))) //
				.andExpect(
						jsonPath("$._embedded.releaseResourceList[0].releaseId", equalTo(release1.getId())));

		verify(releaseServiceMock, times(1)).findAll(any(ReleaseSearchDto.class), any(Pageable.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenId_whenGetRelease_thenReturnOk() throws Exception {
		final Release release = createRelease("1");

		when(releaseServiceMock.findOne(anyString())).thenReturn(Optional.of(release));

		mvc.perform(get("/releases/" + release.getId()) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.releaseId", equalTo(release.getId()))) //
				.andExpect(jsonPath("$.title", equalTo(release.getTitle()))) //
				.andExpect(jsonPath("$.description", equalTo(release.getDescription()))) //
				.andExpect(jsonPath("$.status", equalTo(release.getStatus().toString())));

		verify(releaseServiceMock, times(1)).findOne(anyString());
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenId_whenGetRelease_thenReturnNotFound() throws Exception {
		when(releaseServiceMock.findOne(anyString())).thenReturn(Optional.empty());

		mvc.perform(get("/releases/not-existing-id") //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andDo(print()) //
				.andExpect(status().isNotFound());

		verify(releaseServiceMock, times(1)).findOne(anyString());
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenCreateRelease_thenReturnCreated() throws Exception {
		final Release release = createRelease("1");
		when(releaseServiceMock.create(any(ReleaseDto.class))).thenReturn(release);

		final ReleaseDto object = ReleaseDto.builder()
				.title("milan12")
				.description("d2")
				.status(ReleaseStatus.IN_DEVELOPMENT)
				.build();
		final byte[] jsonDto = mapper.writeValueAsBytes(object);

		mvc.perform(post("/releases") //
				.content(jsonDto) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andDo(print()) //
				.andExpect(status().isCreated()) //
				.andExpect(jsonPath("$.releaseId", equalTo(release.getId()))) //
				.andExpect(jsonPath("$.title", equalTo(release.getTitle()))) //
				.andExpect(jsonPath("$.description", equalTo(release.getDescription()))) //
				.andExpect(jsonPath("$.status", equalTo(release.getStatus().toString())));

		verify(releaseServiceMock, times(1)).create(any(ReleaseDto.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenUpdateRelease_thenReturnOk() throws Exception {
		final Release release = createRelease("1");
		when(releaseServiceMock.update(anyString(), Mockito.any(ReleaseDto.class))).thenReturn(Optional.of(release));

		final ReleaseDto object = ReleaseDto.builder()
				.title("milan12")
				.description("d2")
				.status(ReleaseStatus.IN_DEVELOPMENT)
				.build();
		final byte[] jsonDto = mapper.writeValueAsBytes(object);

		mvc.perform(put("/releases/" + release.getId()) //
				.content(jsonDto) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andDo(print()) //
				.andExpect(status().isOk());

		verify(releaseServiceMock, times(1)).update(anyString(), any(ReleaseDto.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenUpdateRelease_thenReturnNotFound() throws Exception {
		when(releaseServiceMock.update(anyString(), Mockito.any(ReleaseDto.class))).thenReturn(Optional.empty());

		final ReleaseDto object = ReleaseDto.builder()
				.title("milan12")
				.description("d2")
				.status(ReleaseStatus.IN_DEVELOPMENT)
				.build();
		final byte[] jsonDto = mapper.writeValueAsBytes(object);

		mvc.perform(put("/releases/not-existing-id") //
				.content(jsonDto) //
				.contentType(MediaType.APPLICATION_JSON)) //
				.andDo(print()) //
				.andExpect(status().isNotFound());

		verify(releaseServiceMock, times(1)).update(anyString(), any(ReleaseDto.class));
		verifyNoMoreInteractions(releaseServiceMock);
	}

	@Test
	public void givenRelease_whenDeleteRelease_thenReturnNoContent() throws Exception {
		doNothing().when(releaseServiceMock).deleteOne(anyString());

		mvc.perform(delete("/releases/1"))//
				.andDo(print()) //
				.andExpect(status().isOk());

		verify(releaseServiceMock, times(1)).deleteOne(anyString());
		verifyNoMoreInteractions(releaseServiceMock);
	}

//	@Test
//	public void givenRelease_whenDeleteRelease_thenReturnNotFound() throws Exception {
//		doNothing().when(releaseServiceMock).deleteOne(anyString());
//
//		mvc.perform(delete("/releases/not-existing-id"))//
//				.andDo(print()) //
//				.andExpect(status().isOk());
//
//		verify(releaseServiceMock, times(1)).deleteOne(anyString());
//		verifyNoMoreInteractions(releaseServiceMock);
//	}

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
