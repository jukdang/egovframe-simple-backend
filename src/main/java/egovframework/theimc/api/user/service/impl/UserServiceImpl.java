package egovframework.theimc.api.user.service.impl;

import javax.transaction.Transactional;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.api.user.model.UserDTO;
import egovframework.theimc.api.user.service.UserService;
import egovframework.theimc.common.utils.CryptoUtils;

@Service("userService")
public class UserServiceImpl extends EgovAbstractServiceImpl implements UserService {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public boolean isIdAvailable(String id) {
    return !userMapper.existUserById(id);
  }

  @Override
  public boolean isValidPassword(String id, String password) {
    User user = userMapper.selectUserById(id);

    if (user != null && !user.getId().equals("")) {
      boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
      return passwordMatch;
    }
    return false;
  }

  @Override
  public UserDTO getUserById(String id) {
    User user = userMapper.selectUserById(id);
    if (user != null) {
      UserDTO userDTO = new UserDTO();
      userDTO.setId(user.getId());
      userDTO.setName(CryptoUtils.decrypt(user.getName()));
      userDTO.setEmail(CryptoUtils.decrypt(user.getEmail()));
      userDTO.setTelNo(CryptoUtils.decrypt(user.getTelNo()));
      return userDTO;
    }
    return null;
  }

  @Transactional
  @Override
  public HttpStatus register(UserDTO request) {

    if (userMapper.existUserById(request.getId())) {
      return HttpStatus.CONFLICT;
    }

    User user = new User();
    user.setId(request.getId());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setName(CryptoUtils.encrypt(request.getName()));
    user.setEmail(CryptoUtils.encrypt(request.getEmail()));
    user.setTelNo(CryptoUtils.encrypt(request.getTelNo()));
    user.setRole("ROLE_USER");
    user.setUtztnTrmsAgreYn(request.getUtztnTrmsAgreYn());
    user.setPrvcClctAgreYn(request.getPrvcClctAgreYn());

    // userRepository.save(user);

    userMapper.insertUser(user);

    return HttpStatus.OK;
  }

  @Transactional
  @Override
  public HttpStatus update(UserDTO request) {
    User user = userMapper.selectUserById(request.getId());
    if (user == null) {
      return HttpStatus.NOT_FOUND;
    }
    user.setName(CryptoUtils.encrypt(request.getName()));
    user.setEmail(CryptoUtils.encrypt(request.getEmail()));
    user.setTelNo(CryptoUtils.encrypt(request.getTelNo()));
    if (request.getPassword() != null && !request.getPassword().isEmpty() && !request.getPassword().equals("")) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    // userRepository.save(user);
    userMapper.updateUser(user);

    return HttpStatus.OK;
  }

}
