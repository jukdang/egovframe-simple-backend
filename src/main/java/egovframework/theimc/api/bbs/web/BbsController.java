package egovframework.theimc.api.bbs.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egovframework.theimc.api.bbs.service.BbsService;
import egovframework.theimc.common.model.ApiResponse;
import egovframework.theimc.common.model.PageVO;
import egovframework.theimc.common.utils.CryptoUtils;
import egovframework.theimc.common.utils.StringUtil;

/**
 * 게시물 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping(value = "/api/bbs")
public class BbsController {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	private EgovPropertyService propertiesService;

	@Autowired
	private BbsService bbsService;

	@RequestMapping("/bbsList")
	public ResponseEntity<ApiResponse> getBbsList(@ModelAttribute PageVO pageVO,
			@RequestParam HashMap<String, Object> requestMap) throws Exception {
		EgovMap data = bbsService.selectBbsList(pageVO, requestMap);
		return ResponseEntity.ok().body(ApiResponse.success(data));
	}

	/**
	 * 게시물 상세
	 * 
	 * @return 게시물 상세 페이지
	 */
	// @CustomStat(value = "게시물상세", description = "일반사용자 - 게시물 상세 조회")
	@GetMapping(value = "/bbsView")
	public String bbsDetail(
			@RequestParam HashMap<String, Object> requestMap, ModelMap model, HttpServletRequest request)
			throws Exception {

		String ctgry = requestMap.containsKey("ctgry") ? requestMap.get("ctgry").toString() : "all";

		CryptoUtils.decryptByKey(requestMap, "sn");

		// 게시물 상세 정보 조회
		EgovMap board = bbsService.selectBbsDetail(requestMap);
		CryptoUtils.encryptByKey(board, "sn");
		if (board == null || (board != null && !ctgry.equals(board.get("ctgry")))) {
			// 게시물이 존재하지 않는 경우 목록으로 리다이렉트
			return "redirect:/bbs/bbsList";
		}

		// 첨부파일 목록 조회
		List<EgovMap> fileList = bbsService.selectUploadFileList(requestMap);
		CryptoUtils.encryptByKey(fileList, "sn");

		// 조회수 증가 처리
		bbsService.updateViewsCnt(requestMap);

		model.addAttribute("board", board);
		model.addAttribute("fileList", fileList);

		return "bbs/bbsDetail";
	}

	/**
	 * 파일 다운로드
	 * 
	 * @return 파일
	 */
	// @CustomStat(value = "게시물파일다운로드", description = "일반사용자 - 게시물 파일다운로드")
	@RequestMapping(value = "/downloadFile")
	public ResponseEntity<byte[]> downloadFile(@RequestParam HashMap<String, Object> requestMap,
			HttpServletRequest request) throws Exception {
		requestMap.put("sn", CryptoUtils.decrypt(StringUtil.getRequestString(requestMap, "fileSn")));
		EgovMap result = bbsService.selectUploadFileDetail(requestMap);
		String fileNm = result.get("fileNm").toString();
		String originalFileNm = result.get("originalFileNm").toString();
		String filePath = Paths.get(uploadDir, fileNm).toString();
		File file = new File(filePath);

		if (!file.exists()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		try (FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			IOUtils.copy(fis, baos); // Apache Commons IO 사용하여 파일 데이터를 바이트 배열로 읽어옴

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			// 한글 파일명 처리
			String encodedFileName = URLEncoder.encode(originalFileNm, "UTF-8").replaceAll("\\+", "%20");
			headers.setContentDispositionFormData("attachment", encodedFileName);
			headers.setContentLength(file.length());

			return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
		}
	}
}