package test.project.storage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import test.project.storage.model.File;
import test.project.storage.service.FileService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTests {
    private final String[] listIDs = new String[4];
    @Autowired
    private MockMvc mvc;
    @Autowired
    private FileService fileService;

    @Before
    public void setup() {
        File file1 = new File();
        file1.setName("TEST_FILE_1.PDF");
        file1.setSize(25L);
        file1 = fileService.create(file1);
        file1.setTags(new String[]{"TEST_TAG_1", "TEST_TAG_2"});
        fileService.update(file1);

        File file2 = new File();
        file2.setName("TEST_FILE_2.PDF");
        file2.setSize(30L);
        file2 = fileService.create(file2);
        file2.setTags(new String[]{"TEST_TAG_1", "TEST_TAG_3"});
        fileService.update(file2);

        File file3 = new File();
        file3.setName("TEST_FILE_3.PDF");
        file3.setSize(400L);
        file3 = fileService.create(file3);
        file3.setTags(new String[]{"TEST_TAG_1", "TEST_TAG_4"});
        fileService.update(file3);

        File file4 = new File();
        file4.setName("TEST_FILE_4.PDF");
        file4.setSize(458L);
        file4 = fileService.create(file4);
        file4.setTags(new String[]{"TEST_TAG_3", "TEST_TAG_4"});
        fileService.update(file4);

        listIDs[0] = file1.getId();
        listIDs[1] = file2.getId();
        listIDs[2] = file3.getId();
        listIDs[3] = file4.getId();
    }

    @After
    public void deleteTestEntity() {
        for (String id: listIDs) {
            fileService.delete(id);
        }
    }

    @Test
    public void contextLoadsTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(get("/file")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void contextLoadsWithFilteredTagsTest() throws Exception {
        MockHttpServletResponse response = mvc.perform(get("/file")
                .param("tags", "TEST_TAG_1", "TEST_TAG_4")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void creatingAndDeletingFileOK() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/file")
                .content("{\"name\":\"file.txt\",\"size\":56}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE));

        String[] responseBody = response.getContentAsString()
                .replaceAll("[\"{}]", "")
                .split(":");
        fileService.delete(responseBody[1]);
    }

    @Test
    public void createdFileWithoutAllFields() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/file")
                .content("{\"name\":\"file.txt\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void createdFileWithNegativeSize() throws Exception {
        MockHttpServletResponse response = mvc.perform(post("/file")
                .content("{\"name\":\"file.txt\",\"size\":-56}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void deleteFileFailed() throws Exception {
        MockHttpServletResponse response = mvc.perform(delete("/file/eeeee111111111")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentType().equals(MediaType.APPLICATION_JSON_VALUE));
    }
}