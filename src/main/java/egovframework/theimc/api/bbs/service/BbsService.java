package egovframework.theimc.api.bbs.service;

import java.util.HashMap;
import java.util.List;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.web.multipart.MultipartFile;

import egovframework.theimc.common.model.PageVO;

public interface BbsService {
	// 게시글목록
	EgovMap selectBbsList(PageVO pageVO, HashMap<String, Object> requestMap) throws Exception;

	// 팝업게시글목록
	List<EgovMap> selectPopupBbsList() throws Exception;

	// 게시글목록건수
	int selectBbsTotalCnt(HashMap<String, Object> requestMap) throws Exception;

	// 게시글업데이트
	int updateBbs(HashMap<String, Object> requestMap) throws Exception;

	// 게시글삭제
	int deleteBbs(HashMap<String, Object> requestMap) throws Exception;

	// 게시글상세
	EgovMap selectBbsDetail(HashMap<String, Object> requestMap) throws Exception;

	// 파일업로드
	int updateBbsWithFiles(HashMap<String, Object> requestMap, List<MultipartFile> uploadFiles) throws Exception;

	// 파일목록
	List<EgovMap> selectUploadFileList(HashMap<String, Object> requestMap) throws Exception;

	// 파일상세
	EgovMap selectUploadFileDetail(HashMap<String, Object> requestMap) throws Exception;

	// 조회수 업데이트
	int updateViewsCnt(HashMap<String, Object> requestMap) throws Exception;
}
