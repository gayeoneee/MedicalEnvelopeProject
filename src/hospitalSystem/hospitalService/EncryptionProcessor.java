package hospitalSystem.hospitalService;

import common.KeyManager;
import common.User;
import common.UserStore;
import crypto.AESCryptoUtil;
import crypto.RSACryptoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.PublicKey;

public class EncryptionProcessor {
	// 암호화 및 수신자 키 암호화
	public static void encryptRecordWithMultiKeys(String patientCode) throws Exception {
        // 1. 환자 기록 디렉토리 확인
        String baseDir = "src/data/records/" + patientCode;
        
        // 2. record.zip 파일 읽기
        File zipFile = new File(baseDir + "/record.zip");
        if (!zipFile.exists()) {
            System.out.println("❌ record.zip 파일이 없습니다.");
            return;
        }
        
        byte[] zipBytes = Files.readAllBytes(zipFile.toPath());
        
        // 3. AES 키 생성
        byte[] aesKey = AESCryptoUtil.generateAESKey();
        
        // 4. record.enc 생성 (AES로 zip 암호화)
        byte[] encryptedRecord = AESCryptoUtil.encrypt(zipBytes, aesKey);
        
        try (FileOutputStream fos = new FileOutputStream(baseDir + "/record.enc")) {
            fos.write(encryptedRecord);
        }
        
        /* 5. AES 키는 수신자별로 다르게 RSA 암호화 : 환자와 보험사 모두 같은 record.enc 복호화 가능하게 */
        
        // 5-1. 환자 공개키 로딩
        User patient = UserStore.getUserByPatientCode(patientCode);
        String patientKeyPath = "src/keys/patient/" + patient.getId() + "/public.key";
        PublicKey patientPublicKey = KeyManager.loadPublicKey(patientKeyPath);

        // 5-2. 보험사(under1) 공개키 로딩
        String insuranceKeyPath = "src/keys/underwriter/under1/public.key";
        PublicKey insurancePublicKey = KeyManager.loadPublicKey(insuranceKeyPath);
        
        // 6. AES 키를 각각 RSA로 암호화하여 저장
        byte[] aesForPatient = RSACryptoUtil.encrypt(aesKey, patientPublicKey);
        byte[] aesForInsurance = RSACryptoUtil.encrypt(aesKey, insurancePublicKey);
        
        try (FileOutputStream fos1 = new FileOutputStream(baseDir + "/aes_for_patient.key")) {
            fos1.write(aesForPatient);
        }

        try (FileOutputStream fos2 = new FileOutputStream(baseDir + "/aes_for_insurance.key")) {
            fos2.write(aesForInsurance);
        }
        
        System.out.println("✅ record.enc, aes_for_patient.key, aes_for_insurance.key 생성 완료");
	}
}
