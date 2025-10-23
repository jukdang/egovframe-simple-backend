package egovframework.theimc.api.user.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import egovframework.theimc.common.Converter.EncryptConverter;
import egovframework.theimc.common.Converter.passwordConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sn")
    private Long sn;

    @Column(name = "user_id", unique = true, nullable = false)
    private String id;

    @Column(name = "pswd", nullable = false)
    @Convert(converter = passwordConverter.class)
    private String password;

    @Column(name = "user_nm", nullable = false)
    @Convert(converter = EncryptConverter.class)
    private String name;

    @Column(name = "tel_no", nullable = false)
    @Convert(converter = EncryptConverter.class)
    private String telNo;

    @Column(name = "email", nullable = false)
    @Convert(converter = EncryptConverter.class)
    private String email;

    @Column(name = "user_role")
    private String role;

}