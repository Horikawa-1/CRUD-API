package com.raisetech.homework9.mapper;


import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import com.raisetech.homework9.entity.User;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMapperTest {

  @Autowired
  UserMapper userMapper;

  @Test
  @DataSet(value = "datasets/users.yml")
  void すべてのユーザーが取得できること() {
    List<User> users = userMapper.findAll();
    assertThat(users)
        .hasSize(3)
        .contains(
            new User(1, "Honma"),
            new User(2, "Nakashima"),
            new User(3, "Itou")
        );
  }

  @Test
  @DataSet(value = "datasets/empty.yml")
  void ユーザーが存在しない場合に空のListが取得できること() {
    List<User> users = userMapper.findAll();
    assertThat(users).isEmpty();
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  void 引数のidに対応したユーザーを取得できること() {
    Optional<User> users = userMapper.findById(1);
    assertThat(users).contains(new User(1, "Honma"));
  }

  @Test
  @DataSet(value = "datasets/users.yml")
  void 引数のidに対応したユーザーが存在しない時_空のOptionalを取得すること() {
    Optional<User> users = userMapper.findById(0);
    assertThat(users).isEmpty();
  }

  @Test
  @DataSet(value = "users.yml")
  @ExpectedDataSet(value = "expectedAfterInsertUser.yml", ignoreCols = "id")
  @Transactional
  void 新規ユーザーが登録できること() {
    userMapper.insertUser(new User(4, "まさのり"));
  }

  @Test
  @DataSet(value = "users.yml")
  @ExpectedDataSet(value = "datasets/expectedAfterUpdateUser.yml")
  @Transactional
  public void 指定されたidのユーザーが存在する時ユーザー情報が正常に更新されること() {
    userMapper.updateUser(2, "まさのり");
  }

  @Test
  @DataSet(value = "users.yml")
  @ExpectedDataSet(value = "datasets/expectedAfterDeleteAnime.yml")
  @Transactional
  public void 指定されたidが存在する時ユーザー情報が削除されること() {
    userMapper.deleteUser(2);
  }
}
