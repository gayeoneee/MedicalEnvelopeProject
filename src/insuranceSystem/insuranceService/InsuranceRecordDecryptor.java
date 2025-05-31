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
		
		public static void decryptEnvelope(String underwriterId, String patientCode) throws Exception {
			String baseDir = "src/data/insuranceInbox/" + patientCode;
			
			String privateKeyPath = "src/keys/underwriter/" + underwriterId + "/private.key";
	        PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);
	        
	        File aesKeyFile = new File(baseDir + "/aes_for_insurance.key");
	        byte[] encryptedAESKey;
	        try (FileInputStream fis = new FileInputStream(aesKeyFile)) {
	            encryptedAESKey = fis.readAllBytes();
	        }

	        byte[] aesKey = crypto.RSACryptoUtil.decrypt(encryptedAESKey, privateKey);

	        File encryptedRecord = new File(baseDir + "/record.enc");
	        byte[] encryptedData;
	        try (FileInputStream fis = new FileInputStream(encryptedRecord)) {
	            encryptedData = fis.readAllBytes();
	        }

	        byte[] decrypted = AESCryptoUtil.decrypt(encryptedData, aesKey);
	        
	        try (FileOutputStream fos = new FileOutputStream(baseDir + "/record_decrypted.zip")) {
	            fos.write(decrypted);
	        }
	        
	        System.out.println("✅ record.enc 복호화 완료 → record_decrypted.zip 생성됨");

		}
	
}
