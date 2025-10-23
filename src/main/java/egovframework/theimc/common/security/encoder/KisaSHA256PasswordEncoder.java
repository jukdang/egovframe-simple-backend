package egovframework.theimc.common.security.encoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KisaSHA256PasswordEncoder implements PasswordEncoder {

    private static final int DEFAULT_ITERATIONS = 10000;
    private static final int SALT_LENGTH = 16;
    private final int iterations;
    private final SecureRandom random;

    public KisaSHA256PasswordEncoder() {
        this(DEFAULT_ITERATIONS);
    }

    public KisaSHA256PasswordEncoder(int iterations) {
        this.iterations = iterations;
        this.random = new SecureRandom();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        // 랜덤 솔트 생성
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        // 해시 생성
        byte[] hash = generateHash(rawPassword.toString(), salt, iterations);

        // 포맷: iterations + ":" + salt(Base64) + ":" + hash(Base64)
        return iterations + ":" + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }

        String[] parts = encodedPassword.split(":");
        if (parts.length != 3) {
            return false;
        }

        try {
            int iterCount = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

            byte[] actualHash = generateHash(rawPassword.toString(), salt, iterCount);

            return slowEquals(expectedHash, actualHash);
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.error("[DEBUG] KisaSHA256PasswordEncoder matches NumberFormatException: 반복 횟수를 파싱할 수 없습니다", e);
            }
        } catch (IllegalArgumentException e) {
            if (log.isDebugEnabled()) {
                log.error("[DEBUG] KisaSHA256PasswordEncoder matches IllegalArgumentException: Base64 디코딩 실패", e);
            }
        } catch (NullPointerException e) {
            if (log.isDebugEnabled()) {
                log.error("[DEBUG] KisaSHA256PasswordEncoder matches NullPointerException: 원시 비밀번호가 null입니다", e);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.error("[DEBUG] KisaSHA256PasswordEncoder matches Exception: 비밀번호 검증 중 오류 발생", e);
            }
        }

        return false;
    }

    // 타이밍 공격 방지를 위한 상수 시간 비교
    private boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    private byte[] generateHash(String password, byte[] salt, int iterations) {
        // 솔트와 패스워드 결합
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] saltedInput = new byte[salt.length + passwordBytes.length];
        System.arraycopy(salt, 0, saltedInput, 0, salt.length);
        System.arraycopy(passwordBytes, 0, saltedInput, salt.length, passwordBytes.length);

        // 초기 해시 생성
        byte[] hash = new byte[32]; // SHA-256 결과 크기는 32바이트
        KISA_SHA256.SHA256_Encrpyt(saltedInput, saltedInput.length, hash);

        // 반복 해싱을 통한 보안 강화
        byte[] result = hash;
        for (int i = 1; i < iterations; i++) {
            // 이전 해시 결과와 솔트를 결합하여 다시 해싱
            byte[] iterInput = new byte[result.length + salt.length];
            System.arraycopy(result, 0, iterInput, 0, result.length);
            System.arraycopy(salt, 0, iterInput, result.length, salt.length);

            KISA_SHA256.SHA256_Encrpyt(iterInput, iterInput.length, hash);
            result = hash.clone();
        }

        return result;
    }
}