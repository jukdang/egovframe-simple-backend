package egovframework.theimc.api.operation;

import java.util.HashMap;
import java.util.List;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends EgovAbstractServiceImpl implements ReportService {

	@Autowired
	private ReportMapper reportMapper;

	/**
	 * 운전습관 리포트 - 운전원별 현황
	 */
	@Override
	public EgovMap selectDriverList(HashMap<String, Object> requestMap) throws Exception {
		List<EgovMap> list = reportMapper.selectDriverList(requestMap);
		EgovMap resultMap = new EgovMap();
		resultMap.put("list", list);
		return resultMap;
	}

	/**
	 * 운전습관 리포트 - 상세 리포트 데이터
	 */
	@Override
	public EgovMap selectDriverReport(HashMap<String, Object> requestMap) throws Exception {
		List<EgovMap> data1 = reportMapper.selectDriverReportData1(requestMap);
		List<EgovMap> data2 = reportMapper.selectDriverReportData2(requestMap);
		List<EgovMap> data3 = reportMapper.selectDriverReportData3(requestMap);
		List<EgovMap> list = reportMapper.selectDriverReport(requestMap);

		EgovMap resultMap = new EgovMap();

		resultMap.put("data1", data1);
		resultMap.put("data2", data2);
		resultMap.put("data3", data3);
		resultMap.put("list", list);

		return resultMap;
	}

}
