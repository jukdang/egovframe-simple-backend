package egovframework.theimc.api.operation;

import java.util.HashMap;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;

public interface ReportService {

	/**
  	 * 운전습관 리포트 - 운전원별 현황
  	 */
	EgovMap selectDriverList(HashMap<String, Object> requestMap) throws Exception;

	 /**
	  * 운전습관 리포트 - 상세 리포트 데이터
	  */
	EgovMap selectDriverReport(HashMap<String, Object> requestMap) throws Exception;
 
}