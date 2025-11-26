package egovframework.theimc.common.auth.model;

import egovframework.theimc.api.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResponse {

  private String id;
  private String name;
  private String role;

  private String accessToken;
  private String refreshToken;

  private String resultCode;
  private String resultMessage;

  public LoginResponse(User user, String accessToken, String refreshToken) {
    this.id = user.getId();
    this.name = user.getName();
    this.role = user.getRole();

    this.accessToken = accessToken;
    this.refreshToken = refreshToken;

    this.resultCode = "200";
    this.resultMessage = "로그인 성공";
  }

  public LoginResponse() {
    this.id = "";
    this.name = "";
    this.role = "";

    this.accessToken = "";
    this.refreshToken = "";

    this.resultCode = "401";
    this.resultMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
  }
}
