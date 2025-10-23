package egovframework.theimc.api.test;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.common.auth.model.JwtUserInfo;

@RestController
@RequestMapping("/test")
public class TestController {
  @GetMapping("/user")
  public String testUser(Authentication authentication) {
    JwtUserInfo principal = (JwtUserInfo) authentication.getPrincipal();
    return principal.getId() + " : " + principal.getName() + " : " + principal.getRole();
  }

  @GetMapping("/admin")
  public String testAdmin(Authentication authentication) {
    JwtUserInfo principal = (JwtUserInfo) authentication.getPrincipal();
    return principal.getId() + " : " + principal.getName() + " : " + principal.getRole();
  }

  @GetMapping("/default")
  public String testDefault() {
    return "default";
  }

}
