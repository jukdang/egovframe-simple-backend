package egovframework.theimc.common.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import egovframework.theimc.common.auth.jwt.JwtAuthenticationEntryPoint;
import egovframework.theimc.common.auth.jwt.JwtAuthenticationFilter;
import egovframework.theimc.common.security.encoder.KisaSHA256PasswordEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private UserDetailsService userDetailsService;

  @Value("${security.password.encoder.id}")
  private String idForEncode;

  private String[] AUTH_ADMIN_WHITELIST = {
      "/admin/**",
      "/test/admin"
  };

  private String[] AUTH_USER_WHITELIST = {
      "/mypage/**",
      "/test/user"
  };

  // 인증 예외 List
  private String[] AUTH_WHITELIST = {
      "/",
      "/auth/**",
      "/api/**",
      "/test/default",
      "/ws/**"
  };

  @Value("${Globals.Allow.Origin}")
  private String[] ORIGINS_WHITELIST; // 콤마로 구분된 값을 배열로 주입

  @Bean
  protected CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH"));
    configuration.setAllowedOrigins(Arrays.asList(ORIGINS_WHITELIST));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  // PasswordEncoder 빈 설정 (기존과 동일)
  @Bean
  public static PasswordEncoder passwordEncoder(
      @Value("${security.password.encoder.id}") String idForEncode) {

    // 지원할 인코더 맵 생성
    Map<String, PasswordEncoder> encoders = new HashMap<>();

    // 다양한 인코더 추가
    encoders.put("bcrypt", new BCryptPasswordEncoder());
    encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());

    // 기존 SHA-512 인코더 대신 KISA SHA-256 인코더 추가
    encoders.put("kisa-sha256", new KisaSHA256PasswordEncoder(10000));
    // null ID를 위한 인코더 추가 (기존 비밀번호 지원)
    encoders.put("null", new BCryptPasswordEncoder());

    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
    // null prefix를 가진 비밀번호를 위한 기본 인코더 설정 (기존 비밀번호가 BCrypt라고 가정)
    delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

    return delegatingPasswordEncoder;
  }

  @Bean
  public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
    return new JwtAuthenticationFilter(AUTH_WHITELIST);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth)
      throws UsernameNotFoundException, IllegalArgumentException, GeneralSecurityException {
    try {
      auth.userDetailsService(userDetailsService)
          .passwordEncoder(passwordEncoder(idForEncode));
    } catch (UsernameNotFoundException e) {
      if (log.isErrorEnabled()) {
        log.error("[ERROR] SecurityConfig configureGlobal UsernameNotFoundException: ", e);
      }
    } catch (IllegalArgumentException e) {
      if (log.isErrorEnabled()) {
        log.error("[ERROR] SecurityConfig configureGlobal IllegalArgumentException: ", e);
      }
    } catch (Exception e) {
      if (log.isErrorEnabled()) {
        log.error("[ERROR] SecurityConfig configureGlobal Exception: ", e);
      }
    }
  }

  @Bean
  protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .antMatchers(AUTH_WHITELIST).permitAll()
            .antMatchers(AUTH_ADMIN_WHITELIST).hasRole("ADMIN") // 관리자 페이지는 ADMIN만 접근
            .antMatchers(AUTH_USER_WHITELIST).hasAnyRole("ADMIN", "USER") // 마이페이지는 ADMIN, USER 모두 접근
            .anyRequest().authenticated())
        .sessionManagement(
            (sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(withDefaults())
        .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
            .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
        .build();
  }
}
