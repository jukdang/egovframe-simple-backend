package egovframework.theimc.api.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import egovframework.theimc.api.user.model.UserDTO;
import egovframework.theimc.api.user.service.UserService;
import egovframework.theimc.common.auth.model.JwtUserInfo;

@Controller
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/profile/verifyPassword")
  public String verifyPassword() {
    return "mbr/verifyPasswordForm";
  }

  @GetMapping("/profile/edit")
  public String profileUpdateForm(Model model) {
    JwtUserInfo jwtUserInfo = (JwtUserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String userId = jwtUserInfo.getName();
    UserDTO user = userService.getUserById(userId);

    model.addAttribute("user", user);

    return "mbr/profileUpdateForm";
  }

}
