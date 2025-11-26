package egovframework.theimc.api.user.service.impl;

import org.apache.ibatis.annotations.Mapper;

import egovframework.theimc.api.user.entity.User;

@Mapper
public interface UserMapper {
  User selectUserById(String id);

  boolean existUserById(String id);

  int insertUser(User user);

  int updateUser(User user);

}
