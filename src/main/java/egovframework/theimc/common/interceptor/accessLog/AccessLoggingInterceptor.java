package egovframework.theimc.common.interceptor.accessLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.theimc.common.auth.model.JwtUserInfo;
import egovframework.theimc.common.interceptor.accessLog.Entity.UserAccessLog;
import egovframework.theimc.common.utils.CryptoUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccessLoggingInterceptor implements HandlerInterceptor {

    private final JdbcTemplate jdbcTemplate;

    public AccessLoggingInterceptor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // AccessLog 어노테이션이 있는지 확인
            AccessLog stat = handlerMethod.getMethodAnnotation(AccessLog.class);
            if (stat != null) {
                // 요청 시작 시간 저장
                request.setAttribute("startTime", System.currentTimeMillis());
                request.setAttribute("statAnnotation", stat);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        if (handler instanceof HandlerMethod && request.getAttribute("startTime") != null) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLog stat = (AccessLog) request.getAttribute("statAnnotation");

            if (stat != null) {
                long startTime = (Long) request.getAttribute("startTime");
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;

                // 현재 로그인한 사용자 정보 가져오기
                String userId = "";
                String userRole = "";
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    userId = ((JwtUserInfo) auth.getPrincipal()).getId();

                } else {
                    userId = (String) auth.getPrincipal();
                }
                userRole = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));

                // JPA로 통계 데이터 저장
                UserAccessLog logEntry = new UserAccessLog();
                logEntry.setSvcNm(stat.svcName());
                logEntry.setCtrlNm(handlerMethod.getBeanType().getSimpleName());
                logEntry.setMthdNm(handlerMethod.getMethod().getName());
                logEntry.setUserId(userId);
                logEntry.setUserRole(userRole);
                logEntry.setProcessTime(executionTime);
                logEntry.setReqUrl(request.getRequestURI());
                logEntry.setReqInfo(stat.description());
                logEntry.setReqParams(convertRequestParamsToJson(request));
                logEntry.setIpAddr(getClientIp(request));
                logEntry.setBrowserInfo(request.getHeader("User-Agent"));
                logEntry.setDeviceType(getDeviceType(request));
                if (ex != null) {
                    logEntry.setErrCd(ex.getClass().getSimpleName());
                    logEntry.setErrMsg(
                            ex.getMessage() != null
                                    ? ex.getMessage().substring(0, Math.min(ex.getMessage().length(), 2000))
                                    : "");
                }

                String sql = "INSERT INTO user_access_log (svc_nm, ctrl_nm, mthd_nm, user_id, user_role, process_time, req_url, req_info, req_params, ip_addr, browser_info, device_type, err_cd, err_msg) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    jdbcTemplate.update(sql,
                            logEntry.getSvcNm(),
                            logEntry.getCtrlNm(),
                            logEntry.getMthdNm(),
                            logEntry.getUserId(),
                            logEntry.getUserRole(),
                            logEntry.getProcessTime(),
                            logEntry.getReqUrl(),
                            logEntry.getReqInfo(),
                            logEntry.getReqParams(),
                            logEntry.getIpAddr(),
                            logEntry.getBrowserInfo(),
                            logEntry.getDeviceType(),
                            logEntry.getErrCd(),
                            logEntry.getErrMsg());
                    log.debug("✅ Access log saved via JdbcTemplate");
                } catch (Exception e) {
                    log.error("❌ Failed to save access log via JdbcTemplate", e);
                }
            }
        }
    }

    /**
     * 요청 파라미터를 JSON 문자열로 변환
     */
    private String convertRequestParamsToJson(HttpServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();

        // 1. GET 및 form-data POST 파라미터 처리
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.isEmpty()) {
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();

                // 민감한 정보 마스킹
                if (key.toLowerCase().contains("pswd")) {
                    paramMap.put(key, "******");
                }
                // sn이나 fileSn 파라미터는 복호화 처리
                else if (key.equals("sn") || key.equals("fileSn")) {
                    if (values.length == 1) {
                        try {
                            // 복호화 처리
                            String decryptedValue = CryptoUtils.decrypt(values[0]);
                            paramMap.put(key, decryptedValue);
                        } catch (Exception e) {
                            if (log.isErrorEnabled()) {
                                String exceptionType = e.getClass().getSimpleName();
                                log.error("[ERROR] StatisticsInterceptor convertRequestParamsToJson1 {}: ",
                                        exceptionType, e);
                            }
                            // 복호화 실패 시 원본 값 사용
                            paramMap.put(key, values[0]);
                        }
                    } else {
                        // 여러 값이 있는 경우 각각 복호화
                        List<String> decryptedValues = new ArrayList<>();
                        for (String value : values) {
                            try {
                                decryptedValues.add(CryptoUtils.decrypt(value));
                            } catch (Exception e) {
                                if (log.isErrorEnabled()) {
                                    String exceptionType = e.getClass().getSimpleName();
                                    log.error("[ERROR] StatisticsInterceptor convertRequestParamsToJson2 {}: ",
                                            exceptionType, e);
                                }
                                // null 값 안전하게 처리
                                decryptedValues.add(value != null ? value : "");
                            }
                        }
                        paramMap.put(key, decryptedValues);
                    }
                } else {
                    paramMap.put(key, values.length == 1 ? values[0] : Arrays.asList(values));
                }
            }
        }

        // 2. JSON 요청 바디 처리 (별도 필터에서 캐싱된 경우)
        Object jsonBody = request.getAttribute("jsonBody");
        if (jsonBody != null) {
            paramMap.put("_jsonBody", jsonBody);
        }

        try {
            // Jackson 라이브러리 사용
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(paramMap);
        } catch (Exception e) {
            // JSON 변환 실패 시 기본 toString 사용
            return paramMap.toString();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")
                || userAgent.contains("ipad")) {
            return "MOBILE";
        } else {
            return "PC";
        }
    }
}