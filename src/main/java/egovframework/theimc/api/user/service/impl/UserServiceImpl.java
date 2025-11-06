package egovframework.theimc.api.user.service.impl;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.api.user.model.RegisterRequest;
import egovframework.theimc.api.user.repository.UserRepository;
import egovframework.theimc.api.user.service.UserService;

@Service("userService")
public class UserServiceImpl extends EgovAbstractServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public HttpStatus register(RegisterRequest request) {

    if (userRepository.existsById(request.getId())) {
      return HttpStatus.CONFLICT;
    }

    User user = new User();
    user.setId(request.getId());
    user.setPassword(request.getPassword());
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setTelNo(request.getTelNo());
    user.setRole("ROLE_USER");

    userRepository.save(user);

    return HttpStatus.OK;
  }

  @Override
  public HttpStatus update(RegisterRequest request) {
    return HttpStatus.OK;
  }
}
