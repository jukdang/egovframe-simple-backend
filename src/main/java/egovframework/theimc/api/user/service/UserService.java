package egovframework.theimc.api.user.service;

import org.springframework.http.HttpStatus;

import egovframework.theimc.api.user.model.UserDTO;

public interface UserService {

  boolean isIdAvailable(String id);

  boolean isValidPassword(String id, String password);

  UserDTO getUserById(String id);

  HttpStatus register(UserDTO request);

  HttpStatus update(UserDTO request);
}
