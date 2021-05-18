package com.mb.neonreleasetracker.exception;

import com.mb.neonreleasetracker.service.ReleaseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExceptionMapperTest {

    @MockBean
    private ReleaseService releaseServiceMock;

    private MockMvc mvc;

    @Autowired
    protected WebApplicationContext context;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void handleResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("not-existing-id")).when(releaseServiceMock).deleteOne(anyString());

        mvc.perform(delete("/releases/not-existing-id"))//
                .andDo(print()) //
                .andExpect(status().isNotFound());

        verify(releaseServiceMock, times(1)).deleteOne(anyString());
        verifyNoMoreInteractions(releaseServiceMock);
    }
}
