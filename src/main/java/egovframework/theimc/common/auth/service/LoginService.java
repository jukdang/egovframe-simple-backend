package egovframework.theimc.common.auth.service;

import egovframework.theimc.api.user.entity.User;
import egovframework.theimc.common.auth.model.LoginRequest;
import egovframework.theimc.common.auth.model.LoginResponse;

public interface LoginService {
  /**
   * 일반 로그인을 처리한다
   * 
   * @return LoginRequest
   *
   * @param vo LoginRequest
   * @exception Exception Exception
   */
  public LoginResponse actionLogin(LoginRequest vo) throws Exception;

  // /**
  // * 아이디를 찾는다.
  // *
  // * @return LoginVO
  // *
  // * @param vo LoginVO
  // * @exception Exception Exception
  // */
  // public LoginRequest searchId(LoginRequest vo) throws Exception;

  // /**
  // * 비밀번호를 찾는다.
  // *
  // * @return boolean
  // *
  // * @param vo LoginVO
  // * @exception Exception Exception
  // */
  // public boolean searchPassword(LoginRequest vo) throws Exception;
}
