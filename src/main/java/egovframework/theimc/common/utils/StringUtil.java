
package egovframework.theimc.common.utils;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {

  /**
   * 객체가 null인지 확인하고 null인 경우 "" 로 바꾸는 메서드
   * 
   * @param object 원본 객체
   * @return resultVal 문자열
   */
  public static String isNullToString(Object object) {
    String string = "";

    if (object != null) {
      string = object.toString().trim();
    }

    return string;
  }
}