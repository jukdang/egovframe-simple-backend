package egovframework.theimc.common.config;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import egovframework.theimc.common.pagination.EgovPaginationDialect;

@Configuration
@Import({
		EgovConfigAspect.class,
		EgovConfigCommon.class,
		EgovConfigIdGeneration.class,
		EgovConfigMapper.class,
		EgovConfigProperties.class,
		EgovConfigTransaction.class,
		EgovConfigValidation.class
})
public class EgovConfigWeb implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;

	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Value("${spring.thymeleaf.cache}")
	private boolean cacheable;

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("classpath:/templates/thymeleaf/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(cacheable);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		// add custom tag
		templateEngine.addDialect(new EgovPaginationDialect());
		// Spring Security Dialect 추가
		templateEngine.addDialect(securityDialect());
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/egovframework/**")
				.addResourceLocations("classpath:/static/css/egovframework/")
				.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.resourceChain(true);
		registry.addResourceHandler("/js/egovframework/**")
				.addResourceLocations("classpath:/static/js/egovframework/")
				.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.resourceChain(true);
		registry.addResourceHandler("/images/egovframework/**")
				.addResourceLocations("classpath:/static/images/egovframework/")
				.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
				.resourceChain(true);
		registry.addResourceHandler("/summernote-0.8.18-dist/**")
				.addResourceLocations("classpath:/static/summernote-0.8.18-dist/")
				.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOriginPatterns("*")
				.allowedMethods("*")
				.allowedHeaders("*")
				.allowCredentials(true)
				.exposedHeaders("Authorization", "Content-Type")
				.maxAge(3600);
	}

	@Bean
	public SessionLocaleResolver localeResolver() {
		return new SessionLocaleResolver();
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
		Properties prop = new Properties();
		prop.setProperty("org.springframework.dao.DataAccessException", "egovSampleError");
		prop.setProperty("org.springframework.transaction.TransactionException", "egovSampleError");
		prop.setProperty("org.egovframe.rte.fdl.cmmn.exception.EgovBizException", "egovSampleError");
		prop.setProperty("org.springframework.security.AccessDeniedException", "egovSampleError");
		prop.setProperty("java.lang.Throwable", "egovSampleError");

		Properties statusCode = new Properties();
		statusCode.setProperty("egovSampleError", "400");
		statusCode.setProperty("egovSampleError", "500");

		SimpleMappingExceptionResolver smer = new SimpleMappingExceptionResolver();
		smer.setDefaultErrorView("egovSampleError");
		smer.setExceptionMappings(prop);
		smer.setStatusCodes(statusCode);
		resolvers.add(smer);
	}

	@Bean
	public SpringSecurityDialect securityDialect() {
		return new SpringSecurityDialect();
	}

}
