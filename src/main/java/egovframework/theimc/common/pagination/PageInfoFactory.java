package egovframework.theimc.common.pagination;

import java.util.HashMap;

import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.data.domain.Page;

import egovframework.theimc.common.model.PageVO;

/**
 * 페이징 처리를 위한 유틸리티 클래스
 * 전자정부프레임워크 PaginationInfo의 반복적인 설정 작업을 간소화
 *
 * @author 개발자
 * @since 2025.08.12
 */
/**
 * 페이징 처리를 위한 유틸리티 클래스
 * 전자정부프레임워크 PaginationInfo 객체 생성을 지원
 *
 * @author cskang
 * @since 2025.11.06
 */
public class PageInfoFactory {

    /** default record count per page */
    private static final int DEFAULT_SIZE = 10;

    /** default pagination size */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 페이지 정보를 설정하고 Map에 페이징 파라미터를 추가 (모든 파라미터 지정)
     * 
     * @param page     현재 페이지 번호
     * @param size     페이지당 레코드 수
     * @param totalCnt 전체 레코드 수
     * @return 설정된 PaginationInfo 객체
     */
    public static PaginationInfo of(int page, int size, int totalCnt) {

        PaginationInfo paginationInfo = new PaginationInfo();

        // Required Fields 설정
        paginationInfo.setCurrentPageNo(page);
        paginationInfo.setRecordCountPerPage(size);
        paginationInfo.setPageSize(DEFAULT_PAGE_SIZE);
        paginationInfo.setTotalRecordCount(totalCnt);

        return paginationInfo;
    }

    /**
     * 페이지 정보를 설정하고 Map에 페이징 파라미터를 추가 (모든 파라미터 지정)
     * 
     * @param pageVO   페이지 정보 객체
     * @param totalCnt 전체 레코드 수
     * @return 설정된 PaginationInfo 객체
     */
    public static PaginationInfo of(PageVO pageVO, int totalCnt) {

        PaginationInfo paginationInfo = new PaginationInfo();

        // Required Fields 설정
        paginationInfo.setCurrentPageNo(pageVO.getPage());
        paginationInfo.setRecordCountPerPage(pageVO.getSize());
        paginationInfo.setPageSize(DEFAULT_PAGE_SIZE);
        paginationInfo.setTotalRecordCount(totalCnt);

        return paginationInfo;
    }

    /**
     * 페이지 정보를 설정하고 Map에 페이징 파라미터를 추가 (모든 파라미터 지정)
     * 
     * @param page     현재 페이지 번호
     * @param size     페이지당 레코드 수
     * @param totalCnt 전체 레코드 수
     * @param pageSize 페이지 리스트 크기
     * @return 설정된 PaginationInfo 객체
     */
    public static PaginationInfo of(int page, int size, int totalCnt, int pageSize) {

        PaginationInfo paginationInfo = new PaginationInfo();

        // Required Fields 설정
        paginationInfo.setCurrentPageNo(page);
        paginationInfo.setRecordCountPerPage(size);
        paginationInfo.setPageSize(pageSize);
        paginationInfo.setTotalRecordCount(totalCnt);

        return paginationInfo;
    }

    /**
     * 페이지 정보를 설정하고 Map에 페이징 파라미터를 추가 (모든 파라미터 지정)
     * 
     * @param pageVO   페이지 정보 객체
     * @param pageSize 페이지 리스트 크기
     * @param totalCnt 전체 레코드 수
     * @return 설정된 PaginationInfo 객체
     */
    public static PaginationInfo of(PageVO pageVO, int totalCnt, int pageSize) {

        PaginationInfo paginationInfo = new PaginationInfo();

        // Required Fields 설정
        paginationInfo.setCurrentPageNo(pageVO.getPage());
        paginationInfo.setRecordCountPerPage(pageVO.getSize());
        paginationInfo.setPageSize(pageSize);
        paginationInfo.setTotalRecordCount(totalCnt);

        return paginationInfo;
    }

    /**
     * Spring Data Page 객체에서 PaginationInfo 생성
     * 
     * @param page Spring Data Page 객체
     * @return 생성된 PaginationInfo 객체
     */
    public static <T> PaginationInfo fromPage(Page<T> page) {
        PaginationInfo paginationInfo = new PaginationInfo();

        paginationInfo.setCurrentPageNo(page.getNumber() + 1); // Spring Page는 0부터 시작
        paginationInfo.setRecordCountPerPage(page.getPageable().getPageSize());
        paginationInfo.setPageSize(DEFAULT_PAGE_SIZE); // 기본 페이지 리스트 크기
        paginationInfo.setTotalRecordCount((int) page.getTotalElements());

        return paginationInfo;
    }

    /**
     * Spring Data Page 객체에서 PaginationInfo 생성
     * 
     * @param page     Spring Data Page 객체
     * @param pageSize 페이지 리스트 크기
     * @return 생성된 PaginationInfo 객체
     */
    public static <T> PaginationInfo fromPage(Page<T> page, int pageSize) {
        PaginationInfo paginationInfo = new PaginationInfo();

        paginationInfo.setCurrentPageNo(page.getNumber() + 1); // Spring Page는 0부터 시작
        paginationInfo.setRecordCountPerPage(page.getPageable().getPageSize());
        paginationInfo.setPageSize(pageSize); // 기본 페이지 리스트 크기
        paginationInfo.setTotalRecordCount((int) page.getTotalElements());

        return paginationInfo;
    }

    /*
     * requestMap에서 페이징 정보 추출하여 PaginationInfo 생성
     * 
     * @param requestMap 요청 파라미터 맵
     * requestMap에는 "page", "size", "totalCnt" 키가 포함되어야 함
     * 
     * @return 생성된 PaginationInfo 객체
     */
    public static PaginationInfo fromMap(HashMap<String, Object> requestMap) {

        // requestMap에서 페이징 정보 추출, null이면 default 값 사용
        int currentPageNo = requestMap.get("page") != null ? (int) requestMap.get("page") : 1;
        int totalRecordCount = requestMap.get("totalCnt") != null ? (int) requestMap.get("totalCnt") : 0;
        int recordCountPerPage = requestMap.get("size") != null ? (int) requestMap.get("size") : DEFAULT_SIZE;
        int pageSize = requestMap.get("pageSize") != null ? (int) requestMap.get("pageSize") : DEFAULT_PAGE_SIZE;

        PaginationInfo paginationInfo = new PaginationInfo();

        // Required Fields 설정
        paginationInfo.setCurrentPageNo(currentPageNo);
        paginationInfo.setRecordCountPerPage(recordCountPerPage);
        paginationInfo.setPageSize(pageSize);
        paginationInfo.setTotalRecordCount(totalRecordCount);

        return paginationInfo;
    }

}