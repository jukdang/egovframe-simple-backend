package egovframework.theimc.common.auth.jwt;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.theimc.common.model.ApiResponse;


@Component
public class JwtAccessAuthenticationEntryPoint implements AuthenticationEntryPoint {


    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
     boolean isApiRequest = isApiRequest(request);
        if (!isApiRequest) {
            // 비API 요청인 경우 기본 동작 수행 (예: 로그인 페이지로 리다이렉트)
            response.sendRedirect(
                    "/login?redirect=" + URLEncoder.encode(request.getRequestURI(), StandardCharsets.UTF_8));

        } else {
            // API 요청인 경우 JSON 응답 반환
            ApiResponse res = ApiResponse.builder()
                    .code(401)
                    .message("인가된 사용자가 아닙니다.")
                    .build();
            ObjectMapper mapper = new ObjectMapper();

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(res);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON.toString());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(jsonInString);
        }
    }
    
        // API 요청 판단 로직
    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String xhr = request.getHeader("X-Requested-With");

        return (accept != null && accept.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xhr);
    }
}