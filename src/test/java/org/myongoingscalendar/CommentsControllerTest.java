package org.myongoingscalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainInitApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("prod")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)*/
public class CommentsControllerTest {

/*    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNTQiLCJpYXQiOjE1Mjg5MDUxNzd9.w7SDrAvi5FMdjMCqkWYWpCdJedUeDlJTSRDpN00ymH4";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void returnTitlesListTest() throws Exception {
        mockMvc.perform(
                post("/api/title/list")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void addCommentTest() throws Exception {
        mockMvc.perform(
                post("/api/comments/add")
                        .header("authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\n" +
                                        "  \"text\": \"test\",\n" +
                                        "  \"tid\": \"4152\"\n" +
                                        "}"
                        ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status.code").value(11011));
    }*/
/*

    @Test
    public void getCommentsTest() throws Exception  {

    }


    @Test
    public void addEmotionTest() throws Exception {
    }


    @Test
    public void addReportTest() throws Exception {
    }*/
}

