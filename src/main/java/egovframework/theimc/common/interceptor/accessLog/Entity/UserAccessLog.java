package egovframework.theimc.common.interceptor.accessLog.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_access_log")
@Getter
@Setter
@NoArgsConstructor
public class UserAccessLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sn")
  private Long sn;

  @Column(name = "svc_nm")
  private String svcNm;

  @Column(name = "ctrl_nm")
  private String ctrlNm;

  @Column(name = "mthd_nm")
  private String mthdNm;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "user_role")
  private String userRole;

  @Column(name = "process_time")
  private Long processTime;

  @Column(name = "req_url")
  private String reqUrl;

  @Column(name = "req_info")
  private String reqInfo;

  @Column(name = "req_params")
  private String reqParams;

  @Column(name = "ip_addr")
  private String ipAddr;

  @Column(name = "browser_info")
  private String browserInfo;

  @Column(name = "device_type")
  private String deviceType;

  @Column(name = "err_cd")
  private String errCd;

  @Column(name = "err_msg", length = 2000)
  private String errMsg;

}