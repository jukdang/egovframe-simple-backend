package egovframework.theimc.common.auth.api;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.common.auth.jwt.JwtTokenUtil;
import egovframework.theimc.common.auth.model.LoginRequest;
import egovframework.theimc.common.auth.model.LoginResponse;
import egovframework.theimc.common.auth.service.LoginService;
import egovframework.theimc.common.interceptor.accessLog.AccessLog;
import egovframework.theimc.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class LoginRestController {

  @Autowired
  private LoginService loginService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @AccessLog(svcName = "로그인", description = "로그인 API 요청")
  @PostMapping(value = "/login")
  public ResponseEntity<ApiResponse> actionLoginJWT(@RequestBody LoginRequest loginVO, HttpServletRequest request,
      HttpServletResponse response, ModelMap model) throws Exception {

    LoginResponse loginResponse = loginService.actionLogin(loginVO);

    if (loginResponse != null && loginResponse.getId() != null && !loginResponse.getId().equals("")) {

      log.debug("===>>> loginResponse.getName() = " + loginResponse.getName());
      log.debug("===>>> loginResponse.getId() = " + loginResponse.getId());
      log.debug("===>>> loginResponse.getRole() = " + loginResponse.getRole());

      String token = loginResponse.getRefreshToken();
      ResponseCookie cookie = ResponseCookie.from("JWT_REFRESH_TOKEN", token)
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(jwtTokenUtil.getValidityFromToken(token))
          .sameSite("Strict") // None, Lax, Strict
          .build();

      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      EgovMap data = new EgovMap();
      data.put("accessToken", loginResponse.getAccessToken());
      return ResponseEntity.ok().body(ApiResponse.success("로그인 성공", data));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."));
    }

  }

  @PostMapping(value = "/logout")
  public ResponseEntity<ApiResponse> actionLogoutJSON(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    ApiResponse res = new ApiResponse();
    res.setCode(200);
    res.setMessage("로그아웃 되었습니다.");

    ResponseCookie cookie = ResponseCookie.from("JWT_REFRESH_TOKEN", null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0) // 0 means delete the cookie
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/refresh")
  public ResponseEntity<ApiResponse> refresh(
      @CookieValue(name = "JWT_REFRESH_TOKEN", required = false) String refreshToken) throws Exception {

    if (refreshToken == null || refreshToken.isBlank()) {

      String guestUUID = UUID.randomUUID().toString();
      User guest = User.builder()
          .id("guest_" + guestUUID)
          .name("guest_" + guestUUID)
          .role("ROLE_ANONYMOUS")
          .build();
      String guestToken = jwtTokenUtil.generateAccessToken(guest);

      EgovMap data = new EgovMap();
      data.put("accessToken", guestToken);
      return ResponseEntity.ok().body(ApiResponse.success("Guest 사용자", data));
    }

    LoginResponse loginResponse = loginService.refreshLogin(refreshToken);
    if (loginResponse == null || loginResponse.getId() == null || loginResponse.getId().isBlank()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Invalid token"));
    }
    EgovMap data = new EgovMap();
    data.put("accessToken", loginResponse.getAccessToken());
    return ResponseEntity.ok().body(ApiResponse.success("토큰 재발급 성공", data));
  }

}