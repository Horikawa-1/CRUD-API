package com.raisetech.homework9.controller;

import com.raisetech.homework9.entity.CreateForm;
import com.raisetech.homework9.entity.User;
import com.raisetech.homework9.entity.UpdateForm;
import java.net.URI;
import java.util.List;
import com.raisetech.homework9.service.UserService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/users")
@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public List<UserResponse> getUsers() {
    return userService.findAll().stream().map(UserResponse::new).toList();
  }

  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable("id") int id) throws Exception {
    return new UserResponse(userService.findById(id));
  }

  @PostMapping
  public ResponseEntity<Map<String, String>> CreateUser(@RequestBody @Validated CreateForm form,
      UriComponentsBuilder builder) {

    // 登録処理
    User name = userService.createUser(form);
    URI url = builder.path("/users/" + name.getId()) // id部分は実際に登録された際に発⾏したidを設定する
        .build().toUri();

    return ResponseEntity.created(url).body(Map.of("message", "user successfully created"));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Map<String, String>> updateUser(@PathVariable("id") int id,
      @Validated @RequestBody UpdateForm updateForm) {

    //更新処理
    userService.updateUser(id, updateForm);

    return ResponseEntity.ok(Map.of("message", "name successfully updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") int id) {

    // 削除処理
    userService.deleteUser(id);

    return ResponseEntity.ok(Map.of("message", "user successfully deleted"));
  }
}
