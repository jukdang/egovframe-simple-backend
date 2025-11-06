package egovframework.theimc.api.user.service;

import org.springframework.http.HttpStatus;

import egovframework.theimc.api.user.model.RegisterRequest;

public interface UserService {
  HttpStatus register(RegisterRequest request);

  HttpStatus update(RegisterRequest request);
}
