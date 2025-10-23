package egovframework.theimc.common.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

@Slf4j
@Getter
@Setter
public class StatusMsg {

	// 상태
	private String status;
	// 메세지
	private String message;
	// 코드
	private int code;
	// 데이터
	private Object data;

	// 성공 응답 메서드
	public static ResponseEntity<StatusMsg> ok() {
		StatusMsg resVO = new StatusMsg();
		resVO.setStatus("SUCCESS");
		resVO.setCode(200);
		return new ResponseEntity<>(resVO, HttpStatus.OK);
	}

	// 성공 응답 메서드
	public static ResponseEntity<StatusMsg> ok(Object objVO) {
		try {
			StatusMsg resVO = new StatusMsg();
			resVO.setStatus("SUCCESS");
			resVO.setMessage("");
			resVO.setCode(200);
			resVO.setData(objVO);
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		} catch (IllegalStateException e) {
			log.error("IllegalStateException : ", e);
			return handleException("Illegal state exception", e);
		}
	}

	// 성공 응답 메서드
	public static ResponseEntity<StatusMsg> ok(Object objVO, String message) {
		try {
			StatusMsg resVO = new StatusMsg();
			resVO.setStatus("SUCCESS");
			resVO.setMessage(message);
			resVO.setCode(200);
			if(objVO != null) resVO.setData(objVO);
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		} catch (IllegalStateException e) {
			log.error("IllegalStateException : ", e);
			return handleException("Illegal state exception", e);
		}
	}

	// 사용자 실패 응답 메서드
	public static ResponseEntity<StatusMsg> fail(String message) {
		StatusMsg resVO = new StatusMsg();
		resVO.setStatus("FAIL");
		resVO.setMessage(message);
		resVO.setCode(200);
		return new ResponseEntity<>(resVO, HttpStatus.OK);
	}

	// 실패 응답 메서드
	public static ResponseEntity<StatusMsg> error(String message, Exception e) {
		return handleException(message, e);
	}

	// Exception 세분화
	private static ResponseEntity<StatusMsg> handleException(String message, Exception e) {
		StatusMsg resVO = new StatusMsg();
		resVO.setStatus("FAIL");
		resVO.setMessage(message);
		resVO.setCode(500);

		if (e instanceof SQLException) {
			log.error("PSQLException : ", e);
			return new ResponseEntity<>(resVO, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			log.error("Exception: ", e);
			return new ResponseEntity<>(resVO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
