package com.example.consolidate.pointsaccount;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PointsAccountApiE2eTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PointsAccountRepository pointsAccountRepository;

    @Test
    void shouldReturn304WhenHistoryUnchangedAnd200AfterPointsAdded() throws Exception {
        long participantPid = 42L;
        pointsAccountRepository.save(new PointsAccount(participantPid));

        String firstEtag = mockMvc.perform(get("/participants/{id}/points-account/points", participantPid))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.ETAG);

        mockMvc.perform(get("/participants/{id}/points-account/points", participantPid)
                        .header(HttpHeaders.IF_NONE_MATCH, firstEtag))
                .andExpect(status().isNotModified())
                .andExpect(header().string(HttpHeaders.ETAG, firstEtag));

        mockMvc.perform(post("/participants/{id}/points-account/points", participantPid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10,\"reason\":\"bonus\"}"))
                .andExpect(status().isOk());

        String secondEtag = mockMvc.perform(get("/participants/{id}/points-account/points", participantPid)
                        .header(HttpHeaders.IF_NONE_MATCH, firstEtag))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ETAG))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.ETAG);

        assertThat(secondEtag).isNotEqualTo(firstEtag);
    }
}
