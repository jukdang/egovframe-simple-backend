package egovframework.theimc.api.user.service.impl;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.api.user.model.RegisterRequest;
import egovframework.theimc.api.user.repository.UserRepository;
import egovframework.theimc.api.user.service.UserService;
import egovframework.theimc.common.model.ResultVO;

@Service("userService")
public class UserServiceImpl extends EgovAbstractServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public ResultVO register(RegisterRequest request) {
    ResultVO result = new ResultVO();

    if (userRepository.existsById(request.getId())) {
      result.setResultCode(409);
      result.setResultMessage("이미 존재하는 아이디입니다.");
      return result;
    }

    String encodedPassword = passwordEncoder.encode(request.getPassword());

    User user = new User();
    user.setId(request.getId());
    user.setPassword(encodedPassword);
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setTelNo(request.getTelNo());
    user.setRole("ROLE_USER");

    userRepository.save(user);

    result.setResultCode(200);
    result.setResultMessage("회원가입이 완료되었습니다.");
    return result;
  }

  @Override
  public ResultVO update(RegisterRequest request) {
    ResultVO result = new ResultVO();

    // do something

    return result;
  }

}
