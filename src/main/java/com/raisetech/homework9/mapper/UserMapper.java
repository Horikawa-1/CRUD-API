package com.raisetech.homework9.mapper;


import java.util.List;
import java.util.Optional;
import com.raisetech.homework9.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper    // MyBatisのMapperである⽬印として@Mapperアノテーションを付与する
public interface UserMapper {     // classではなくinterfaceで定義する

  @Select("SELECT * FROM users")
  List<User> findAll();

  @Select("SELECT * FROM users WHERE id = #{id}")
  Optional<User> findById(int id);

  @Options(useGeneratedKeys = true, keyColumn = "id")
  @Insert("INSERT INTO users (name) VALUES (#{name})")
  void insertUser(User name);

  @Update("UPDATE users SET name=#{name} WHERE id = #{id}")
  void updateUser(int id, String name);

  @Delete("DELETE FROM users WHERE id = #{id}")
  void deleteUser(int id);
}
