package egovframework.theimc.common.auth.jwt;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import egovframework.theimc.common.auth.model.JwtUserInfo;
import egovframework.theimc.common.utils.StringUtil;

/**
 * fileName : JwtAuthenticationFilter
 * author : crlee
 * date : 2023/06/11
 * description :
 * ===========================================================
 * DATE AUTHOR NOTE
 * -----------------------------------------------------------
 * 2023/06/11 crlee 최초 생성
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public static final String HEADER_STRING = "JWT_TOKEN";
    public String[] whiteList;

    public JwtAuthenticationFilter(String[] whiteList) {
        this.whiteList = whiteList;
    }

    @Override // 로그인 이후 HttpServletRequest 요청할 때마다 실행(스프링의 AOP기능)
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String path = req.getRequestURI();

        boolean isWhitelisted = Arrays.stream(whiteList)
                .anyMatch(pattern -> {
                    if (pattern.endsWith("/**")) {
                        String prefix = pattern.substring(0, pattern.length() - 3);
                        return path.startsWith(prefix);
                    } else {
                        return path.equals(pattern);
                    }
                });

        if (isWhitelisted) {
            chain.doFilter(req, res);
            return;
        }

        // step 1. request header에서 토큰을 가져온다.
        String token = null;
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        String jwtToken = StringUtil.isNullToString(token);

        // step 2. 토큰에 내용이 있는지 확인해서 id값을 가져옴
        // Exception 핸들링 추가처리 (토큰 유효성, 토큰 변조 여부, 토큰 만료여부)
        // 내부적으로 parse하는 과정에서 해당 여부들이 검증됨
        try {
            JwtUserInfo userInfo = jwtTokenUtil.getUserInfoFromToken(jwtToken);
            // logger.debug("===>>> id = " + userInfo.getId());
            // logger.debug("jwtToken validated");
            logger.debug("===>>>jwtToken validated: userInfo.getName() = " + userInfo.getName());

            String role = userInfo.getRole();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userInfo, null, Arrays.asList(new SimpleGrantedAuthority(role)));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // logger.debug("authentication ===>>> " + authentication);
        } catch (InvalidJwtException e) {
            SecurityContextHolder.getContext().setAuthentication(
                    new AnonymousAuthenticationToken(
                            "anonymous", "anonymousUser",
                            Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
            logger.debug("Invalid JWT token: Anonymous Authentication");
            // logger.debug(e.getMessage());
        }

        chain.doFilter(req, res);
    }

}
