package egovframework.theimc.common.interceptor.accessLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLog {
    String svcName() default ""; // 통계 식별자

    String description() default ""; // 통계 설명
}
