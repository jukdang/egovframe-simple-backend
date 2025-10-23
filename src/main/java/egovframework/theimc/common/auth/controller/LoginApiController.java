package egovframework.theimc.common.auth.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.common.auth.model.LoginRequest;
import egovframework.theimc.common.auth.model.LoginResponse;
import egovframework.theimc.common.auth.service.LoginService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginApiController {

  /** LoginService */
  @Resource(name = "loginService")
  private LoginService loginService;

  @PostMapping(value = "/login")
  public LoginResponse actionLoginJWT(@RequestBody LoginRequest loginVO, HttpServletRequest request,
      ModelMap model) throws Exception {

    LoginResponse loginResponse = loginService.actionLogin(loginVO);

    if (loginResponse != null && loginResponse.getId() != null && !loginResponse.getId().equals("")) {

      log.debug("===>>> loginResponse.getName() = " + loginResponse.getName());
      log.debug("===>>> loginResponse.getId() = " + loginResponse.getId());
      log.debug("===>>> loginResponse.getRole() = " + loginResponse.getRole());
    }

    return loginResponse;
  }

  // @GetMapping(value = "/logout")
  // public ResultVO actionLogoutJSON(HttpServletRequest request,
  // HttpServletResponse response) throws Exception {

  // ResultVO resultVO = new ResultVO();
  // resultVO.setResultCode(200);
  // resultVO.setResultMessage("로그아웃 되었습니다.");

  // return resultVO;
  // }

}
