package egovframework.theimc.api.bbs.service.impl;

import java.util.HashMap;
import java.util.List;

import org.egovframe.rte.psl.dataaccess.mapper.Mapper;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;

@Mapper
public interface BbsMapper {
	// 게시글목록
	List<EgovMap> selectBbsList(HashMap<String, Object> requestMap) throws Exception;

	// 팝업게시글목록
	List<EgovMap> selectPopupBbsList() throws Exception;

	// 게시글건수
	int selectBbsTotalCnt(HashMap<String, Object> requestMap) throws Exception;

	// 게시글업데이트
	int updateBbs(HashMap<String, Object> requestMap) throws Exception;

	// 게시글업데이트번호
	int selectLastInsertId() throws Exception;

	// 게시글삭제
	int deleteBbs(HashMap<String, Object> requestMap) throws Exception;

	// 게시글상세
	EgovMap selectBbsDetail(HashMap<String, Object> requestMap) throws Exception;

	// 파일업로드
	int insertUploadFileInfo(HashMap<String, Object> requestMap) throws Exception;

	// 파일정보삭제
	int deleteUploadFileInfo(HashMap<String, Object> requestMap) throws Exception;

	// 파일목록
	List<EgovMap> selectUploadFileList(HashMap<String, Object> requestMap) throws Exception;

	// 파일상세
	EgovMap selectUploadFileDetail(HashMap<String, Object> requestMap) throws Exception;

	// 조회수 업데이트
	int updateViewsCnt(HashMap<String, Object> requestMap) throws Exception;
}
