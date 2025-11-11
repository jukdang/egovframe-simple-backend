package egovframework.theimc.api.bbs.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egovframework.theimc.api.bbs.service.BbsService;
import egovframework.theimc.common.model.PageVO;
import egovframework.theimc.common.pagination.PageInfoFactory;
import egovframework.theimc.common.utils.CryptoUtils;
import egovframework.theimc.common.utils.StringUtil;

@Service("BbsService")
public class BbsServiceImpl implements BbsService {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	private BbsMapper bbsMapper;

	// 게시글목록
	@Override
	public EgovMap selectBbsList(PageVO pageVO, HashMap<String, Object> requestMap) throws Exception {
		requestMap.put("size", pageVO.getSize());
		requestMap.put("offset", pageVO.getOffset());
		List<EgovMap> list = bbsMapper.selectBbsList(requestMap);
		CryptoUtils.encryptByKey(list, "sn");

		int totalCnt = bbsMapper.selectBbsTotalCnt(requestMap);
		PaginationInfo paginationInfo = PageInfoFactory.of(pageVO, totalCnt);

		EgovMap result = new EgovMap();
		result.put("list", list);
		result.put("paginationInfo", paginationInfo);

		return result;
	}

	// 팝업게시글목록
	@Override
	@Cacheable(value = "popupBbsCache")
	public List<EgovMap> selectPopupBbsList() throws Exception {
		return bbsMapper.selectPopupBbsList();
	}

	// 게시글건수
	@Override
	public int selectBbsTotalCnt(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.selectBbsTotalCnt(requestMap);
	}

	// 게시글업데이트
	@Override
	public int updateBbs(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.updateBbs(requestMap);
	}

	// 게시글삭제
	@Override
	public int deleteBbs(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.deleteBbs(requestMap);
	}

	// 게시글상세
	@Override
	public EgovMap selectBbsDetail(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.selectBbsDetail(requestMap);
	}

	@Transactional
	@Override
	public int updateBbsWithFiles(HashMap<String, Object> requestMap, List<MultipartFile> uploadFiles) throws Exception {
		int result = bbsMapper.updateBbs(requestMap);
		String getSn = StringUtil.getRequestString(requestMap, "sn");
		int sn;

		// 게시물 번호를 가져옴
		if (getSn == null) { // insert 시
			sn = bbsMapper.selectLastInsertId(); // 새로 생성된 게시물 번호를 가져옴
		} else { // update 시
			sn = Integer.parseInt(getSn);
		}

		String[] deletedFiles = (String[]) requestMap.get("deletedFiles");
		if (deletedFiles != null) {
			List<String> deleteFiles = new ArrayList<>();
			for (String encryptedFileSn : deletedFiles) {
				String fileSn = CryptoUtils.decrypt(encryptedFileSn); // 파일 인덱스 복호화
				HashMap<String, Object> requestMap2 = new HashMap<String, Object>();
				requestMap2.put("sn", fileSn);
				EgovMap deleteFileDetail = bbsMapper.selectUploadFileDetail(requestMap2);
				deleteFiles.add(deleteFileDetail.get("fileNm").toString());
				deleteUploadedFiles(deleteFiles);
				bbsMapper.deleteUploadFileInfo(requestMap2); // 파일 정보 삭제
			}
		}
		// 파일 저장 처리
		if (uploadFiles != null && !uploadFiles.isEmpty()) {
			List<String> savedFiles = new ArrayList<>(); // 저장된 파일명을 추적
			try {
				result = saveFiles(sn, uploadFiles, savedFiles);
			} catch (Exception e) {
				// 파일 저장 실패 시 신규 게시물과 저장된 파일 삭제
				if (getSn == null) {
					requestMap.put("boardSn", sn);
					bbsMapper.deleteBbs(requestMap); // 게시물 삭제
					bbsMapper.deleteUploadFileInfo(requestMap); // 게시물 전체 파일 정보 삭제
				}
				deleteUploadedFiles(savedFiles); // 저장된 파일 삭제
				result = 0;
			}
		}
		return result; // 게시물과 파일 저장 성공
	}

	private void deleteUploadedFiles(List<String> savedFiles) throws Exception {
		for (String modifiedfileNm : savedFiles) {
			String filePath = Paths.get(uploadDir, modifiedfileNm).toString();
			Files.deleteIfExists(Paths.get(filePath));
		}
	}

	private int saveFiles(int sn, List<MultipartFile> uploadFiles, List<String> savedFiles) throws Exception {
		int successCount = 0;
		int uploadFilesSize = uploadFiles.size();

		for (MultipartFile file : uploadFiles) {
			if (!file.isEmpty()) {
				String originalFileNm = file.getOriginalFilename();
				String modifiedFileNm = sn + "_" + UUID.randomUUID().toString() + "_" + originalFileNm;
				String filePath = Paths.get(uploadDir, modifiedFileNm).toString();
				long fileSize = file.getSize();
				String fileExtension = getFileExtension(originalFileNm);

				// try-with-resources를 사용하여 스트림 자동 닫기
				try (java.io.InputStream inputStream = file.getInputStream()) {
					Files.copy(inputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

					// 저장된 파일명을 리스트에 추가
					savedFiles.add(modifiedFileNm);

					// DB에 파일 정보 저장
					saveFileToDatabase(sn, originalFileNm, modifiedFileNm, filePath, fileSize, fileExtension);
					successCount++;
				}
			}
		}

		// 모든 파일이 성공적으로 업로드되었는지 확인
		return (successCount == uploadFilesSize) ? 1 : 0;
	}

	// 파일 확장자를 추출하는 헬퍼 메서드
	private String getFileExtension(String fileNm) {
		if (fileNm == null || fileNm.isEmpty()) {
			return "";
		}
		int lastDotIndex = fileNm.lastIndexOf('.');
		if (lastDotIndex == -1 || lastDotIndex == fileNm.length() - 1) {
			return "";
		}
		return fileNm.substring(lastDotIndex + 1).toLowerCase();
	}

	private void saveFileToDatabase(int sn, String originalFileNm, String modifiedfileNm, String filePath, long fileSize,
			String fileExtension) throws Exception {
		HashMap<String, Object> fileMap = new HashMap<>();
		fileMap.put("boardSn", sn);
		fileMap.put("originalFileNm", originalFileNm);
		fileMap.put("fileNm", modifiedfileNm);
		fileMap.put("filePath", filePath);
		fileMap.put("fileSize", fileSize);
		fileMap.put("fileExtension", fileExtension);

		int result = bbsMapper.insertUploadFileInfo(fileMap);
		if (result <= 0) {
			throw new RuntimeException("파일 정보 DB 저장 실패");
		}
	}

	// 파일목록
	@Override
	public List<EgovMap> selectUploadFileList(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.selectUploadFileList(requestMap);
	}

	// 파일상세
	@Override
	public EgovMap selectUploadFileDetail(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.selectUploadFileDetail(requestMap);
	}

	// 조회수 업데이트
	@Override
	public int updateViewsCnt(HashMap<String, Object> requestMap) throws Exception {
		return bbsMapper.updateViewsCnt(requestMap);
	}
}
