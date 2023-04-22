package com.raisetech.homework9.integrationtest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
@DBRider
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRestApiIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  @DataSet(value = "datasets/users.yml")
  void ユーザーが全件取得できること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
             [
                {
                   "id":1,
                   "name":"Honma"
                },
                {
                   "id":2,
                   "name":"Nakashima"
                },
                {
                   "id":3,
                   "name":"Itou"
                }
             ]
            """
        , response, JSONCompareMode.LENIENT);
  }

  @Test
  @DataSet(value = "datasets/empty.yml")
  void テーブルに何も登録されてない時_空のListとステータスコード200が返されること() throws Exception {
    String responce = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
        []
        """, responce, JSONCompareMode.STRICT);
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  void 指定されたidのユーザーが存在するとき指定されたユーザー情報が返されること() throws Exception {
    String responce = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 3))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
        [
          {
            "id": 3,
            "name": "Itou"
          }
        ]
        """, responce, JSONCompareMode.STRICT
    );
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  void 存在しないIDのユーザー情報を取得しようとすると例外がスローされてステータスコード404を返すこと() throws Exception {
    String responce = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 0))
        .andExpect(status().isNotFound())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
               [
                 {
                   "path": "/users/0",
                   "error": "Not Found",
                   "message": "IDが0のレコードはありません。",
                   "timestamp": "2023-04-18T14:48:04.911608700+09:00[Asia/Tokyo]",
                   "status": "404"
                 }
               ]
            """, responce,
        JSONCompareMode.LENIENT);
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  @ExpectedDataSet(value = "datasets/expectedAfterInsertUser.yml", ignoreCols = "id")
  @Transactional
  void 新規ユーザーが登録されること() throws Exception {
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name" : "まさのり",
                    "id" : "4"
                }
                """))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    assertThat(response.getContentAsString()).isEqualTo("user successfully created");
  }

  @Test
  void 登録処理でnameが21文字以上のときエラーメッセージが返されること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":"123456789012345678901",
                    "id":"4"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                 "type": "about:blank",
                 "title": "Bad Request",
                 "status": 400,
                 "detail": "Invalid request content.",
                 "instance": "/users"
            }
                    """, response,
        JSONCompareMode.LENIENT);
  }

  @Test
  void 登録処理でnameが空文字だったとき登録せずエラーメッセージが返されること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":"",
                    "id":"4"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                "type": "about:blank",
                "title": "Bad Request",
                "status": 400,
                "detail": "Invalid request content.",
                "instance": "/users"
            }
                                """, response,
        new CustomComparator(JSONCompareMode.LENIENT));
  }

  @Test
  void 登録処理でnameがnullだったとき登録せずエラーメッセージが返されること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":null,
                    "id":"4"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                "type": "about:blank",
                "title": "Bad Request",
                "status": 400,
                "detail": "Invalid request content.",
                "instance": "/users"
            }
                    """, response,
        new CustomComparator(JSONCompareMode.LENIENT));
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  @ExpectedDataSet(value = "datasets/expectedAfterUpdateUser.yml")
  @Transactional
  void 指定したユーザーが存在するとき更新できること() throws Exception {
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name" : "まさのり",
                    "id" : "2"
                }
                """))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    assertThat(response.getContentAsString()).isEqualTo("name successfully updated");
  }


  @Test
  @DataSet(value = "datasets/users.yml")
  void 更新時に指定したIDのユーザーが存在しない場合404エラーとなりエラーのレスポンスを返すこと() throws Exception {
    String responce = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 0)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":"まさのり",
                    "id":"0"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                "path": "/users/0",
                "error": "Not Found",
                "message": "IDが0のレコードはありません。",
                "timestamp": "2023-04-18T14:50:45.392249100+09:00[Asia/Tokyo]",
                "status": "404"
            }
                                """, responce,
        new CustomComparator(JSONCompareMode.LENIENT,
            new Customization("timestamp", (o1, o2) -> true)));

  }

  @Test
  void 更新処理でnameが21文字以上のとき更新せずエラーメッセージが返されること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":"123456789012345678901",
                    "id":"4"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                    "type": "about:blank",
                    "title": "Bad Request",
                    "status": 400,
                    "detail": "Invalid request content.",
                    "instance": "/users/2"
            }
            """, response,
        new CustomComparator(JSONCompareMode.LENIENT));
  }

  @Test
  void 更新処理でnameがnullだったとき更新せずエラーメッセージが返されること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":null,
                    "id":"2"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                    "type": "about:blank",
                    "title": "Bad Request",
                    "status": 400,
                    "detail": "Invalid request content.",
                    "instance": "/users/2"
            }
                    """, response,
        new CustomComparator(JSONCompareMode.LENIENT));
  }

  @Test
  void 更新処理でnameが空文字だったとき更新せずエラーメッセージが返されること() throws Exception {
    String response = mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", 2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content("""
                {
                    "name":"",
                    "id":"2"
                }
                """))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                    "type": "about:blank",
                    "title": "Bad Request",
                    "status": 400,
                    "detail": "Invalid request content.",
                    "instance": "/users/2"
            }
                    """, response,
        new CustomComparator(JSONCompareMode.LENIENT));
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  @ExpectedDataSet(value = "datasets/expectedAfterDeleteUser.yml")
  void 指定したユーザー情報が削除できること() throws Exception {
    MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 2)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    assertThat(response.getContentAsString()).isEqualTo("user successfully deleted");
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  void 削除時に指定したIDのユーザーが存在しない場合404エラーとなりエラーのレスポンスを返すこと() throws Exception {
    String responce = mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 0))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    JSONAssert.assertEquals("""
            {
                "path": "/users/0",
                "error": "Not Found",
                "message": "IDが0のレコードはありません。",
                "timestamp": "2023-04-18T14:51:37.677934400+09:00[Asia/Tokyo]",
                "status": "404"
            }
                                """, responce,
        new CustomComparator(JSONCompareMode.LENIENT,
            new Customization("timestamp", (o1, o2) -> true)));
  }
}
