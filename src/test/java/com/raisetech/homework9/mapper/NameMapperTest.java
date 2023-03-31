package com.raisetech.homework9.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;

@DBRider
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NameMapperTest {

  @Autowired
  NameMapper nameMapper;

  @Test
  @DataSet(value = "datasets/users.yml")
  @Transactional
  void すべてのユーザーが取得できること() {
    List<User> users = nameMapper.findAll();
    assertThat(users)
        .hasSize(3)
        .contains(
            new User(1, "Honma"),
            new User(2, "Nakashima"),
            new User(3, "Itou")
        );
  }
}