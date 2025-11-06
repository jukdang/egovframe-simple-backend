package egovframework.theimc.api.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.api.user.model.RegisterRequest;
import egovframework.theimc.api.user.service.UserService;
import egovframework.theimc.common.model.ApiResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {

    HttpStatus status = userService.register(request);
    if (status == HttpStatus.OK) {
      return ResponseEntity.ok(new ApiResponse<>(status, "회원가입이 완료되었습니다."));
    } else {
      return ResponseEntity.status(status).body(new ApiResponse<>(status, "이미 존재하는 아이디입니다."));
    }
  }

  @PostMapping("/update")
  public ResponseEntity<ApiResponse> update(@RequestBody RegisterRequest request) {
    // TODO: process POST request

    HttpStatus status = userService.update(request);
    if (status == HttpStatus.OK) {
      return ResponseEntity.ok(new ApiResponse<>(status, "회원정보가 수정되었습니다."));
    } else {
      return ResponseEntity.status(status).body(new ApiResponse<>(status, "회원정보 수정에 실패하였습니다."));
    }
  }

}
