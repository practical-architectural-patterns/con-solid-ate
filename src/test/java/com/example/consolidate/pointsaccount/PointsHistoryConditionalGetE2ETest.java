package com.example.consolidate.pointsaccount;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PointsHistoryConditionalGetE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_Return304_When_ETagMatchesCurrentHistory() throws Exception {
        String pointsUrl = pointsUrlForNewParticipant("email@gmail.com");

        String eTag = getETagFor200(pointsUrl);

        mockMvc.perform(get(pointsUrl).header("If-None-Match", eTag))
                .andExpect(status().isNotModified());
    }

    @Test
    void should_Return200WithUpdatedETag_When_ETagDoesNotMatchCurrentHistory() throws Exception {
        String pointsUrl = pointsUrlForNewParticipant("email@gmail.com");

        addPoints(pointsUrl);
        String staleETag = getETagFor200(pointsUrl);
        addPoints(pointsUrl);

        mockMvc.perform(get(pointsUrl).header("If-None-Match", staleETag))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", org.hamcrest.Matchers.not(staleETag)))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void should_Return200ThenReturn304ThenReturn200WithNewETag_When_FullCachingLifecycle() throws Exception {
        String pointsUrl = pointsUrlForNewParticipant("email@gmail.com");

        String eTag = getETagFor200(pointsUrl);

        mockMvc.perform(get(pointsUrl).header("If-None-Match", eTag))
                .andExpect(status().isNotModified());

        addPoints(pointsUrl);

        mockMvc.perform(get(pointsUrl).header("If-None-Match", eTag))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", org.hamcrest.Matchers.not(eTag)));
    }

    private String pointsUrlForNewParticipant(String email) throws Exception {
        String body = mockMvc.perform(post("/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"fullName","contactMail":"%s"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        var matcher = Pattern.compile("\"pid\":(\\d+)").matcher(body);
        assertThat(matcher.find()).isTrue();
        return "/participants/" + matcher.group(1) + "/points-account/points";
    }

    private String getETagFor200(String url) throws Exception {
        return mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"))
                .andReturn().getResponse().getHeader("ETag");
    }

    private void addPoints(String url) throws Exception {
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":100,"reason":"pr"}
                                """))
                .andExpect(status().isOk());
    }
}
