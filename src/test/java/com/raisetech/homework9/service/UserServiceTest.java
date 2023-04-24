package com.raisetech.homework9.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.raisetech.homework9.entity.CreateForm;
import com.raisetech.homework9.entity.UpdateForm;
import com.raisetech.homework9.entity.User;
import com.raisetech.homework9.exception.ResourceNotFoundException;
import com.raisetech.homework9.mapper.UserMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  UserServiceImpl userServiceImpl;

  @Mock
  UserMapper userMapper;

  @Test
  public void 全てのユーザー情報が返されること() {
    doReturn(List.of(new User(1, "Honma"), new User(2, "Nakashima"), new User(3, "Itou"))).when(
        userMapper).findAll();

    List<User> actual = userServiceImpl.findAll();
    assertThat(actual).isEqualTo(
        List.of(new User(1, "Honma"), new User(2, "Nakashima"), new User(3, "Itou")));
    verify(userMapper).findAll();
  }

  @Test
  public void 存在するユーザーのIDを指定したときに正常にユーザーが返されること() throws Exception {
    doReturn(Optional.of(new User(1, "Honma"))).when(userMapper).findById(1);
    //findById(1)実行時、必ずid:1, name:Honma　を返す

    User actual = userServiceImpl.findById(1);
    assertThat(actual).isEqualTo(new User(1, "Honma"));
    verify(userMapper).findById(1);
  }

  @Test
  public void 存在しないユーザーIDを指定したときにResourceNotFoundExceptionがスローされること() {
    doReturn(Optional.empty()).when(userMapper).findById(0);
    assertThatThrownBy(() -> userServiceImpl.findById(0))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("IDが0のレコードはありません。");

    verify(userMapper).findById(0);
  }

  @Test
  public void 新規ユーザーの情報が入力された時に正常にユーザーの登録をすること() throws Exception {
    doNothing().when(userMapper).insertUser(new User(4, "まさのり"));

    User actual = userServiceImpl.createUser(new CreateForm(4, "まさのり"));
    assertThat(actual).isEqualTo(new User(4, "まさのり"));
    verify(userMapper).insertUser(new User(4, "まさのり"));
  }

  @Test
  public void 存在するidに対応するユーザーのnameが更新できていること() throws Exception {
    doReturn(Optional.of(new User(1, "本間"))).when(userMapper).findById(1);
    doNothing().when(userMapper).updateUser(1, "Honma");
    verify(userMapper).updateUser(1, "Honma");
  }

  @Test
  public void 更新処理で存在しないIDを指定されたときに例外をthrowすること() {
    when(userMapper.findById(0)).thenReturn(Optional.empty());
    userServiceImpl.updateUser(0, new UpdateForm());
    assertThatThrownBy(() -> userServiceImpl.updateUser(0, new UpdateForm()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("IDが0のレコードはありません。");
    verify(userMapper).findById(0);
  }

  @Test
  public void 存在するidに対応するユーザー情報が正常に削除できていること() {
    doNothing().when(userMapper).deleteUser(1);
    userServiceImpl.deleteUser(1);
    verify(userMapper).deleteUser(1);
  }

  @Test
  public void 削除処理で存在しないIDを指定されたときに例外をthrowすること() {
    doReturn(Optional.empty()).when(userMapper).findById(0);
    assertThatThrownBy(() -> userServiceImpl.deleteUser(0))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("IDが0のレコードはありません。");
  }
}
