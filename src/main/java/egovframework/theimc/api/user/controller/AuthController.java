package egovframework.theimc.api.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
  @RequestMapping(value = "/login")
  public String login() {
    return "mbr/loginForm";
  }

  @RequestMapping(value = "/register")
  public String register() {
    return "mbr/registerForm";
  }

  @GetMapping(value = "/forgot")
  public String forgotPassword() {

    return "main/forgot";
  }
}
