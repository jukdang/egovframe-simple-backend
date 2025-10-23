package egovframework.theimc.api.user.service;

import egovframework.theimc.api.user.model.RegisterRequest;
import egovframework.theimc.common.model.ResultVO;

public interface UserService {

  ResultVO register(RegisterRequest request);

  ResultVO update(RegisterRequest request);
}
