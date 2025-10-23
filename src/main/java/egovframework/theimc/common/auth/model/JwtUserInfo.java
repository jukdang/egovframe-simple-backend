package egovframework.theimc.common.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtUserInfo {
  private String id;
  private String name;
  private String role;
}
