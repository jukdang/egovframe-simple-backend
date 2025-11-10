package egovframework.theimc.api.user.service.impl;

import javax.transaction.Transactional;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.api.user.model.UserDTO;
import egovframework.theimc.api.user.repository.UserRepository;
import egovframework.theimc.api.user.service.UserService;

@Service("userService")
public class UserServiceImpl extends EgovAbstractServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public boolean isIdAvailable(String id) {
    return !userRepository.existsById(id);
  }

  @Override
  public boolean isValidPassword(String id, String password) {
    User user = userRepository.findById(id);

    if (user != null && !user.getId().equals("")) {
      boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
      return passwordMatch;
    }
    return false;
  }

  @Override
  public UserDTO getUserById(String id) {
    User user = userRepository.findById(id);
    if (user != null) {
      UserDTO userDTO = new UserDTO();
      userDTO.setId(user.getId());
      userDTO.setName(user.getName());
      userDTO.setEmail(user.getEmail());
      userDTO.setTelNo(user.getTelNo());
      return userDTO;
    }
    return null;
  }

  @Transactional
  @Override
  public HttpStatus register(UserDTO request) {

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
    user.setUtztnTrmsAgreYn(request.getUtztnTrmsAgreYn());
    user.setPrvcClctAgreYn(request.getPrvcClctAgreYn());

    userRepository.save(user);

    return HttpStatus.OK;
  }

  @Transactional
  @Override
  public HttpStatus update(UserDTO request) {
    User user = userRepository.findById(request.getId());
    if (user == null) {
      return HttpStatus.NOT_FOUND;
    }
    user.setName(request.getName());
    user.setEmail(request.getEmail());
    user.setTelNo(request.getTelNo());
    if (request.getPassword() != null && !request.getPassword().isEmpty() && !request.getPassword().equals("")) {
      user.setPassword(request.getPassword());
    }
    userRepository.save(user);

    return HttpStatus.OK;
  }
}
