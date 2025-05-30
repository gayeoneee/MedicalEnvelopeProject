package insuranceSystem.insuranceService;

import common.KeyManager;
import common.User;
import crypto.AESCryptoUtil;
import crypto.RSACryptoUtil;

import java.io.*;
import java.nio.file.*;
import java.security.PrivateKey;
import java.util.zip.*;

public class InsuranceRecordDecryptor {
		// [2]암호화된 진료기록 복호화
		// ex) aes_for_patient.key → 개인키로 복호화 → AES 키 획득
		//     record.enc → AES로 복호화 → record_decrypted.zip 생성
		
		public static void decryptEnvelope(String underwriterId, String patientCode) throws Exception {
			// 1. 파일 경로 설정
			String baseDir = "src/data/insuranceInbox/" + patientCode;
			
			// 2. 환자 개인키 로드
			String privateKeyPath = "src/keys/underwriter/" + underwriterId + "/private.key";
	        PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);
	        
	        // 3. 암호화된 AES 키 로드
	        File aesKeyFile = new File(baseDir + "/aes_for_insurance.key");
	        byte[] encryptedAESKey = new FileInputStream(aesKeyFile).readAllBytes();
	        
	        // 4. RSA로 AES 키 복호화
	        byte[] aesKey = crypto.RSACryptoUtil.decrypt(encryptedAESKey, privateKey);
	        
	        // 5. 암호화된 진료기록 로드
	        File encryptedRecord = new File(baseDir + "/record.enc");
	        byte[] encryptedData = new FileInputStream(encryptedRecord).readAllBytes();
	        
	        // 6. AES로 record.enc 복호화 → ZIP 복원
	        byte[] decrypted = AESCryptoUtil.decrypt(encryptedData, aesKey);
	        
	        // 7. 복호화된 zip 저장
	        try (FileOutputStream fos = new FileOutputStream(baseDir + "/record_decrypted.zip")) {
	            fos.write(decrypted);
	        }
	        
	        System.out.println("✅ record.enc 복호화 완료 → record_decrypted.zip 생성됨");

		}
	
}
