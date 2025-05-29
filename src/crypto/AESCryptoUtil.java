package crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESCryptoUtil {
	/* 민주 작성 코드*/
	public static byte[] generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256bit
        return keyGen.generateKey().getEncoded();			// 바이트 배열로 변환
    }

    public static byte[] encrypt(byte[] data, byte[] keyBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES"); 	// 바이트 배열 → 키 객체 로 변환 후 사용
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] keyBytes) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(data);
    }
    
    /* 실습때 사용한 코드 */
//    public static SecretKey generateAESKey() throws Exception {
//        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//        keyGen.init(256);
//        return keyGen.generateKey(); // SecretKey 객체 그대로 반환, 위에서는 반환할 때 Encoded() 통해 바이트 배열로 변환해서 반환했음!
//    }
//    
//    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        return cipher.doFinal(data);
//    }
    /* keySpec 으로 리팩토링 했다 설명하면서 왜 keySpec 사용했는지 이유 작성해야 할 듯 */
}