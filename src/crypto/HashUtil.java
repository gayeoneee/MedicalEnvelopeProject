package crypto;

import java.security.MessageDigest;
import java.util.Arrays;

public class HashUtil {
	// byte[] 기반 해시 생성
    public static byte[] generateSHA256(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }
    
    // byte[] 기반 해시 비교
    public static boolean verifySHA256(byte[] data, byte[] hash) throws Exception {
        byte[] calcHash = generateSHA256(data);
        return Arrays.equals(calcHash, hash);
    }
    
    
    /* 리팩토링 10 : 비밀번호는 해시 함수를 이용하여 저장, 비교 */
    
    // String 입력용 SHA-256 해시 생성 → 16진수 문자열 반환
    public static String generateSHA256(String input) throws Exception {
        byte[] hashed = generateSHA256(input.getBytes("UTF-8"));
        return bytesToHex(hashed);
    }

    // String 입력값과 저장된 해시(String)를 비교
    public static boolean verifySHA256(String input, String expectedHash) throws Exception {
        String actualHash = generateSHA256(input);
        return actualHash.equals(expectedHash);
    }

    // 내부 유틸: 바이트 배열 → 16진수 문자열
    private static String bytesToHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
