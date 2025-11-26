package egovframework.theimc.api.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "user_id", unique = true, nullable = false)
    private String id;

    @Column(name = "pswd", nullable = false)
    // @Convert(converter = passwordConverter.class)
    private String password;

    @Column(name = "user_nm", nullable = false)
    // @Convert(converter = EncryptConverter.class)
    private String name;

    @Column(name = "tel_no", nullable = false)
    // @Convert(converter = EncryptConverter.class)
    private String telNo;

    @Column(name = "email", nullable = false)
    // @Convert(converter = EncryptConverter.class)
    private String email;

    @Column(name = "user_role")
    private String role;

    @Column(name = "utztn_trms_agre_yn", nullable = false)
    private String utztnTrmsAgreYn;

    @Column(name = "prvc_clct_agre_yn", nullable = false)
    private String prvcClctAgreYn;

}