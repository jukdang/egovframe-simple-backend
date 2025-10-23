package egovframework.theimc.api.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.api.user.model.RegisterRequest;
import egovframework.theimc.api.user.service.UserService;
import egovframework.theimc.common.model.ResultVO;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<ResultVO> register(@RequestBody RegisterRequest request) {

    ResultVO resultVO = userService.register(request);
    HttpStatus status = resultVO.getResultCode() == 200 ? HttpStatus.CREATED : HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(resultVO);
  }

  @PostMapping("/update")
  public ResponseEntity<ResultVO> update(@RequestBody RegisterRequest request) {
    // TODO: process POST request

    ResultVO resultVO = userService.update(request);
    HttpStatus status = resultVO.getResultCode() == 200 ? HttpStatus.OK : HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(resultVO);
  }

}
