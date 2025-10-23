package egovframework.theimc.common.auth.service.impl;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.api.user.repository.UserRepository;
import egovframework.theimc.common.auth.jwt.JwtTokenUtil;
import egovframework.theimc.common.auth.model.LoginRequest;
import egovframework.theimc.common.auth.model.LoginResponse;
import egovframework.theimc.common.auth.service.LoginService;

@Service("loginService")
public class LoginServiceImpl extends EgovAbstractServiceImpl implements LoginService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  JwtTokenUtil jwtTokenUtil;

  /**
   * 일반 로그인을 처리한다
   * 
   * @param vo LoginRequest
   * @return LoginRequest
   * @exception Exception
   */
  @Override
  public LoginResponse actionLogin(LoginRequest vo) throws Exception {

    User user = userRepository.findById(vo.getId());

    if (user != null && !user.getId().equals("")) {
      boolean passwordMatch = passwordEncoder.matches(vo.getPassword(), user.getPassword());
      if (passwordMatch) {
        String jwtToken = jwtTokenUtil.generateToken(user, vo.isRememberMe());
        return new LoginResponse(user, jwtToken);
      }
    }

    return new LoginResponse();
  }

  // /**
  // * 아이디를 찾는다.
  // *
  // * @param vo LoginVO
  // * @return LoginVO
  // * @exception Exception
  // */
  // @Override
  // public LoginRequest searchId(LoginRequest vo) throws Exception {

  // // 1. 이름, 이메일주소가 DB와 일치하는 사용자 ID를 조회한다.
  // LoginRequest loginVO = loginDAO.searchId(vo);

  // // 2. 결과를 리턴한다.
  // if (loginVO != null && !loginVO.getId().equals("")) {
  // return loginVO;
  // } else {
  // loginVO = new LoginRequest();
  // }

  // return loginVO;
  // }

  // /**
  // * 비밀번호를 찾는다.
  // *
  // * @param vo LoginVO
  // * @return boolean
  // * @exception Exception
  // */
  // @Override
  // public boolean searchPassword(LoginRequest vo) throws Exception {

  // boolean result = true;

  // // 1. 아이디, 이름, 이메일주소, 비밀번호 힌트, 비밀번호 정답이 DB와 일치하는 사용자 Password를 조회한다.
  // LoginRequest loginVO = loginDAO.searchPassword(vo);
  // if (loginVO == null || loginVO.getPassword() == null ||
  // loginVO.getPassword().equals("")) {
  // return false;
  // }

  // // 2. 임시 비밀번호를 생성한다.(영+영+숫+영+영+숫=6자리)
  // String newpassword = "";
  // for (int i = 1; i <= 6; i++) {
  // // 영자
  // if (i % 3 != 0) {
  // newpassword += EgovStringUtil.getRandomStr('a', 'z');
  // // 숫자
  // } else {
  // newpassword += EgovNumberUtil.getRandomNum(0, 9);
  // }
  // }

  // // 3. 임시 비밀번호를 암호화하여 DB에 저장한다.
  // LoginRequest pwVO = new LoginRequest();
  // String enpassword = EgovFileScrty.encryptPassword(newpassword, vo.getId());
  // pwVO.setId(vo.getId());
  // pwVO.setPassword(enpassword);
  // pwVO.setUserSe(vo.getUserSe());
  // loginDAO.updatePassword(pwVO);

  // return result;
  // }
}
