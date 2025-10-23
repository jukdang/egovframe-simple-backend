package egovframework.theimc.common.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import egovframework.theimc.common.interceptor.accessLog.AccessLoggingInterceptor;

@Configuration
public class EgovConfigInterceptor implements WebMvcConfigurer {

  private final AccessLoggingInterceptor accessLoggingInterceptor;

  public EgovConfigInterceptor(AccessLoggingInterceptor accessLoggingInterceptor) {
    this.accessLoggingInterceptor = accessLoggingInterceptor;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName("language");
    return interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(accessLoggingInterceptor);
    registry.addInterceptor(localeChangeInterceptor());
  }
}
