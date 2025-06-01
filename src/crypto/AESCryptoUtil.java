package crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptoUtil {
	// 리팩토링 5 : SecretKey 객체를 전송 가능하고, 복원 가능한 바이트 배열 기반 키 사용 방식으로 구조 개선
	// 참고 자료 : https://learn.microsoft.com/ko-kr/dotnet/api/javax.crypto.spec.secretkeyspec.getencoded?view=net-android-34.0
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
    


}