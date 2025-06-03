package insuranceSystem.insuranceService;

import common.KeyManager;
import crypto.AESCryptoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;

public class InsuranceRecordDecryptor {
		// [3단계] 전자봉투 복호화
		// - 심사관 개인키로 aes_for_insurance.key 복호화 → AES 키 획득
		// - AES로 record.enc 복호화 → record_decrypted.zip 생성
		public static void decryptEnvelope(String underwriterId, String patientCode) throws Exception {
			String baseDir = "src/data/insuranceInbox/" + patientCode;
			
			String privateKeyPath = "src/keys/underwriter/" + underwriterId + "/private.key";
	        PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);
	        
	        
	        // 리팩토링 2 : FileInputStream 객체 try-with-resources 적용하여 안전하게 닫게함
	        File aesKeyFile = new File(baseDir + "/aes_for_insurance.key");
	        byte[] encryptedAESKey;
	        try (FileInputStream fis = new FileInputStream(aesKeyFile)) {
	            encryptedAESKey = fis.readAllBytes();
	        }

	        byte[] aesKey = crypto.RSACryptoUtil.decrypt(encryptedAESKey, privateKey);

	        File encryptedRecord = new File(baseDir + "/record_" +  patientCode + ".enc");
	        byte[] encryptedData;
	        try (FileInputStream fis = new FileInputStream(encryptedRecord)) {
	            encryptedData = fis.readAllBytes();
	        }

	        byte[] decrypted = AESCryptoUtil.decrypt(encryptedData, aesKey);
	        
	        try (FileOutputStream fos = new FileOutputStream(baseDir + "/record_decrypted.zip")) {
	            fos.write(decrypted);
	        }
	        
	        System.out.println("🔓 전자봉투 복호화 완료! 진료기록 압축파일을 복원했습니다.");
	        System.out.println("   → 복호화된 진료기록 파일: record_decrypted.zip\n");

		}
	
}
