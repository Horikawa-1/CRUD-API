package com.raisetech.homework9.integrationtest;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRestApiIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  @DataSet(value = "users.yml")
  @Transactional
  void ユーザーが取得できること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    JSONAssert.assertEquals("[" +
        " {" +
        " \"id\": 1," +
        " \"name\": \"Honma\"" +
        " }," +
        " {" +
        " \"id\": 2," +
        " \"name\": \"Nakashima\"" +
        " }," +
        " {" +
        " \"id\": 3," +
        " \"name\": \"Itou\"" +
        " }" +
        "]", response, JSONCompareMode.STRICT);
  }
}