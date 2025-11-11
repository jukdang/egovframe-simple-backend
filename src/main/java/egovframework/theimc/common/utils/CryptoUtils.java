package egovframework.theimc.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 암복호화 유틸리티 클래스
 * AES 알고리즘 사용
 */
@Slf4j
@Component
public class CryptoUtils {

    /** 알고리즘 */
    private static final String ALGORITHM = "AES";

    /** 알고리즘 패딩 */
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /** 암호화 키 (static 접근 가능) */
    private static String SECRETKEY;

    /** 인코딩 (static 접근 가능) */
    private static String ENCODING;

    /** Spring에서 주입받을 필드 */
    @Value("${cryptoUtils.secretkey}")
    private String secretKeyProp;

    @Value("${cryptoUtils.encoding}")
    private String encodingProp;

    /** 초기화 후 static 변수에 복사 */
    @PostConstruct
    public void init() {
        SECRETKEY = secretKeyProp;
        ENCODING = encodingProp;
    }

    public static String getSecretKey() {
        return SECRETKEY;
    }

    public static String getEncoding() {
        return ENCODING;
    }

    /**
     * 문자열 암호화
     * 
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열 (URL 안전 Base64 인코딩)
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }

        try {
            // GCM용 IV 생성
            byte[] iv = new byte[12];
            new java.security.SecureRandom().nextBytes(iv);

            SecretKeySpec keySpec = new SecretKeySpec(SECRETKEY.getBytes(ENCODING), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            javax.crypto.spec.GCMParameterSpec gcmSpec = new javax.crypto.spec.GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(ENCODING));

            // IV + 암호문 결합
            byte[] result = new byte[12 + encrypted.length];
            System.arraycopy(iv, 0, result, 0, 12);
            System.arraycopy(encrypted, 0, result, 12, encrypted.length);

            // URL 안전 Base64 인코딩 사용
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt UnsupportedEncodingException: ", e);
            }
        } catch (NoSuchAlgorithmException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt NoSuchAlgorithmException: ", e);
            }
        } catch (NoSuchPaddingException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt NoSuchPaddingException: ", e);
            }
        } catch (InvalidKeyException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt InvalidKeyException: ", e);
            }
        } catch (java.security.InvalidAlgorithmParameterException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt InvalidAlgorithmParameterException: ", e);
            }
        } catch (IllegalBlockSizeException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt IllegalBlockSizeException: ", e);
            }
        } catch (BadPaddingException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils encrypt BadPaddingException: ", e);
            }
        }

        return null;
    }

    /**
     * 암호화된 문자열 복호화
     * 
     * @param encryptedText 복호화할 암호문 (URL 안전 Base64 인코딩)
     * @return 복호화된 평문
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.length() == 0) {
            return "";
        }

        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRETKEY.getBytes(ENCODING), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // URL 안전 Base64 디코딩 사용
            byte[] combined = Base64.getUrlDecoder().decode(encryptedText);

            if (combined.length < 28) { // IV(12) + 태그(16) 최소
                return null;
            }

            // IV와 암호문 분리
            byte[] iv = new byte[12];
            byte[] encrypted = new byte[combined.length - 12];
            System.arraycopy(combined, 0, iv, 0, 12);
            System.arraycopy(combined, 12, encrypted, 0, encrypted.length);

            javax.crypto.spec.GCMParameterSpec gcmSpec = new javax.crypto.spec.GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, ENCODING);
        } catch (IllegalArgumentException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt IllegalArgumentException: ", e);
            }
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt UnsupportedEncodingException: ", e);
            }
        } catch (NoSuchAlgorithmException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt NoSuchAlgorithmException: ", e);
            }
        } catch (NoSuchPaddingException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt NoSuchPaddingException: ", e);
            }
        } catch (InvalidKeyException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt InvalidKeyException: ", e);
            }
        } catch (java.security.InvalidAlgorithmParameterException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt InvalidAlgorithmParameterException: ", e);
            }
        } catch (IllegalBlockSizeException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt IllegalBlockSizeException: ", e);
            }
        } catch (BadPaddingException e) {
            if (log.isErrorEnabled()) {
                log.error("[ERROR] CryptoUtils decrypt BadPaddingException: ", e);
            }
        }

        return null;
    }

    /**
     * List<EgovMap>에서 모든 항목의 특정 키 값을 암호화하는 메서드
     *
     * @param mapList 처리할 EgovMap 리스트
     * @param key     암호화할 값의 키
     */
    public static void encryptByKey(List<EgovMap> mapList, String key) {
        if (mapList == null || key == null) {
            return;
        }

        mapList.forEach(map -> {
            encryptByKey(map, key);
        });
    }

    /**
     * EgovMap에서 특정 키의 값을 가져와 암호화하고 다시 설정하는 메서드
     *
     * @param map 처리할 EgovMap
     * @param key 암호화할 값의 키
     */
    public static void encryptByKey(EgovMap map, String key) {
        if (map == null || key == null || !map.containsKey(key)) {
            return;
        }

        map.put(key, encrypt(StringUtil.isNullToString(map.get(key))));

    }

    /**
     * HashMap에서 특정 키의 값을 가져와 암호화하고 다시 설정하는 메서드
     *
     * @param map 처리할 HashMap
     * @param key 암호화할 값의 키
     */
    public static void encryptByKey(HashMap<String, Object> map, String key) {
        if (map == null || key == null || !map.containsKey(key)) {
            return;
        }

        map.put(key, encrypt(StringUtil.isNullToString(map.get(key))));

    }

    /**
     * List<EgovMap>에서 모든 항목의 특정 키 값을 복호화하는 메서드
     *
     * @param mapList 처리할 EgovMap 리스트
     * @param key     복호화할 값의 키
     */
    public static void decryptByKey(List<EgovMap> mapList, String key) {
        if (mapList == null || key == null) {
            return;
        }

        mapList.forEach(map -> {
            decryptByKey(map, key);
        });
    }

    /**
     * EgovMap에서 특정 키의 값을 가져와 복호화하고 다시 설정하는 메서드
     *
     * @param map 처리할 EgovMap
     * @param key 복호화할 값의 키
     */
    public static void decryptByKey(EgovMap map, String key) {
        if (map == null || key == null || !map.containsKey(key)) {
            return;
        }

        map.put(key, decrypt(StringUtil.isNullToString(map.get(key))));

    }

    /**
     * HashMap에서 특정 키의 값을 가져와 복호화하고 다시 설정하는 메서드
     *
     * @param map 처리할 HashMap
     * @param key 복호화할 값의 키
     */
    public static void decryptByKey(HashMap<String, Object> map, String key) {
        if (map == null || key == null || !map.containsKey(key)) {
            return;
        }

        map.put(key, decrypt(StringUtil.isNullToString(map.get(key))));

    }
}