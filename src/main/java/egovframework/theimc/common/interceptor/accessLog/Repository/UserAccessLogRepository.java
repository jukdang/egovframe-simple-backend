package egovframework.theimc.common.interceptor.accessLog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import egovframework.theimc.common.interceptor.accessLog.Entity.UserAccessLog;

@Repository
public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {

}
