package org.myongoingscalendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/*@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainInitApplication.class)
@AutoConfigureMockMvc*/
public class ApiTests {
/*
    @Autowired
    private MockMvc mockMvc;


    @Test
    public void returnTitlesListTest() throws Exception {
        mockMvc.perform(
                post("/api/title/list")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
*/

   /* @Test
    public void returnTitleDataTest() throws Exception {
        mockMvc.perform(
                post("/api/title/4779")
                        .contentType(APPLICATION_JSON)
                        .content("{\"timezone\":\"Asia/Tokyo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").exists());
    }*/
}
