package egovframework.theimc.api.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
  private String id;
  private String password;
  private String name;
  private String email;
  private String telNo;
}
