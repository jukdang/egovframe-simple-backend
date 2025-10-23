package egovframework.theimc.api.operation;

import java.util.HashMap;
import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;

@Mapper
public interface ReportMapper {

	/**
	 * 운전습관 리포트 - 운전원별 현황
	 * 
	 * @param ownrNm 운수회사
	 * @param rptYm  보고서연월
	 * @return 검색된 운전원 목록
	 */
	List<EgovMap> selectDriverList(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * 운전습관 리포트 - 상세 리포트 데이터
	 * 
	 * @param ownrNm  운수회사
	 * @param rptYm   보고서연월
	 * @param oprtrId 운전원아이디
	 * @return 검색된 통계 데이터
	 */
	List<EgovMap> selectDriverReport(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * 운전습관 리포트 - 주행 성능 분석
	 * 
	 * @param ownrNm  운수회사
	 * @param rptYm   보고서연월
	 * @param oprtrId 운전원아이디
	 * @return 검색된 통계 데이터
	 */
	List<EgovMap> selectDriverReportData1(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * 운전습관 리포트 - 위험운행행태 현황
	 * 
	 * @param ownrNm  운수회사
	 * @param rptYm   보고서연월
	 * @param oprtrId 운전원아이디
	 * @return 검색된 통계 데이터
	 */
	List<EgovMap> selectDriverReportData2(HashMap<String, Object> requestMap) throws Exception;

	/**
	 * 운전습관 리포트 - 요일별 평균 위험운행행태
	 * 
	 * @param ownrNm  운수회사
	 * @param rptYm   보고서연월
	 * @param oprtrId 운전원아이디
	 * @return 검색된 통계 데이터
	 */
	List<EgovMap> selectDriverReportData3(HashMap<String, Object> requestMap) throws Exception;

}
