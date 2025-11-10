package egovframework.theimc.api.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.api.user.model.UserDTO;
import egovframework.theimc.api.user.service.UserService;
import egovframework.theimc.common.auth.model.JwtUserInfo;
import egovframework.theimc.common.model.ApiResponse;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

  @Autowired
  private UserService userService;

  @GetMapping("/checkId")
  public ResponseEntity<ApiResponse> checkId(@RequestParam String id) {
    boolean available = userService.isIdAvailable(id);
    return ResponseEntity.ok().body(ApiResponse.success(Map.of("available", available)));
  }

  @GetMapping("/verifyPassword")
  public ResponseEntity<ApiResponse> checkPswd(@RequestParam String password) {
    JwtUserInfo jwtUserInfo = (JwtUserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String id = jwtUserInfo.getName();

    boolean valid = userService.isValidPassword(id, password);
    return ResponseEntity.ok().body(ApiResponse.success(Map.of("valid", valid)));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse> register(@RequestBody UserDTO request) {

    HttpStatus status = userService.register(request);
    if (status == HttpStatus.OK) {
      return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    } else {
      return ResponseEntity.status(status).body(ApiResponse.error(status, "이미 존재하는 아이디입니다."));
    }
  }

  @PostMapping("/profile/edit")
  public ResponseEntity<ApiResponse> update(@RequestBody UserDTO request) {

    HttpStatus status = userService.update(request);
    if (status == HttpStatus.OK) {
      return ResponseEntity.ok(ApiResponse.success("회원정보가 수정되었습니다."));
    } else {
      return ResponseEntity.status(status).body(ApiResponse.error(status, "회원정보 수정에 실패하였습니다."));
    }
  }

}
