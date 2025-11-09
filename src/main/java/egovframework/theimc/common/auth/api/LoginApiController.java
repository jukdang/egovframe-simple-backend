package egovframework.theimc.common.auth.api;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import egovframework.theimc.common.auth.jwt.JwtTokenUtil;
import egovframework.theimc.common.auth.model.LoginRequest;
import egovframework.theimc.common.auth.model.LoginResponse;
import egovframework.theimc.common.auth.service.LoginService;
import egovframework.theimc.common.interceptor.accessLog.AccessLog;
import egovframework.theimc.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginApiController {

  @Autowired
  private LoginService loginService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @AccessLog(svcName = "로그인", description = "로그인 API 요청")
  @PostMapping(value = "/login")
  public LoginResponse actionLoginJWT(@RequestBody LoginRequest loginVO, HttpServletRequest request,
      HttpServletResponse response, ModelMap model) throws Exception {

    LoginResponse loginResponse = loginService.actionLogin(loginVO);

    if (loginResponse != null && loginResponse.getId() != null && !loginResponse.getId().equals("")) {

      log.debug("===>>> loginResponse.getName() = " + loginResponse.getName());
      log.debug("===>>> loginResponse.getId() = " + loginResponse.getId());
      log.debug("===>>> loginResponse.getRole() = " + loginResponse.getRole());

      String token = loginResponse.getToken();
      ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", token)
          .httpOnly(true)
          .secure(true)
          .path("/")
          .maxAge(jwtTokenUtil.getValidityFromToken(token)) // 1 hour
          .sameSite("Strict") // None, Lax, Strict
          .build();

      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    return loginResponse;
  }

  @PostMapping(value = "/logout")
  public ResponseEntity<ApiResponse> actionLogoutJSON(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    ApiResponse res = new ApiResponse();
    res.setCode(200);
    res.setMessage("로그아웃 되었습니다.");

    ResponseCookie cookie = ResponseCookie.from("JWT_TOKEN", null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0) // 0 means delete the cookie
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> me(@CookieValue(name = "JWT_TOKEN", required = false) String token) {
    ApiResponse res = new ApiResponse();

    if (token == null || token.isBlank()) {
      res.setCode(HttpStatus.UNAUTHORIZED.value());
      res.setMessage("Invalid token");
      return ResponseEntity.ok().body(res);
    }
    HashMap<String, Object> data = new HashMap<>();
    data.put("id", jwtTokenUtil.getUserIdFromToken(token));
    data.put("exp", jwtTokenUtil.getExpFromToken(token));
    data.put("role", jwtTokenUtil.getRoleFromToken(token));

    res.setCode(HttpStatus.OK.value());
    res.setMessage("success");
    res.setData(data);

    return ResponseEntity.ok().body(res);
  }

}