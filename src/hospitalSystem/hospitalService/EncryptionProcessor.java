package hospitalSystem.hospitalService;

import common.KeyManager;
import common.User;
import common.UserStore;
import crypto.AESCryptoUtil;
import crypto.RSACryptoUtil;
import hospitalSystem.PatientService.RecordRequestSubmitter; // 리팩토링 B

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.PublicKey;

public class EncryptionProcessor {
	// [4단계] 암호화 및 수신자 키 암호화
	public static void encryptRecordWithMultiKeys(String patientCode) throws Exception {
        // 1. 환자 기록 디렉토리 확인
        String baseDir = "src/data/records/" + patientCode;
        
        // 2. record.zip 파일 읽기
        File zipFile = new File(baseDir + "/record_" + patientCode + ".zip");
        
        if (!zipFile.exists()) {
            System.out.println("record_" + patientCode + ".zip 파일이 없습니다.");
            return;
        }
        
        byte[] zipBytes = Files.readAllBytes(zipFile.toPath());
        
        // 3. AES 키 생성
        byte[] aesKey = AESCryptoUtil.generateAESKey();
        
        // 4. record.enc 생성 (AES로 zip 암호화)
        byte[] encryptedRecord = AESCryptoUtil.encrypt(zipBytes, aesKey);
        
        String encPath = baseDir + "/record_" + patientCode + ".enc";
        try (FileOutputStream fos = new FileOutputStream(encPath)) {
            fos.write(encryptedRecord);
        }

        // 저장된 record.enc 파일 존재 확인 (추가된 로직)
        File encryptedFile = new File(encPath);
        if (!encryptedFile.exists()) {
            System.out.println("❌ record.enc 파일이 저장되지 않았습니다!");
            return;
        }
        
        /* 5. AES 키는 수신자별로 다르게 RSA 암호화 : 환자와 보험사 모두 같은 record.enc 복호화 가능하게 */
        
        // 5-1. 환자 공개키 로딩
        User patient = UserStore.getUserByPatientCode(patientCode);
        String patientKeyPath = "src/keys/patient/" + patient.getId() + "/public.key";
        PublicKey patientPublicKey = KeyManager.loadPublicKey(patientKeyPath);

        // 5-2. 보험사(under1) 공개키 로딩
        String underwriterCode = RecordRequestSubmitter.readUnderwriterCode(patientCode);
        
        // 심사관 코드 → User 객체 조회 (UserStore에 해당 메서드 필요)
        User underwriter = UserStore.getUserByUnderwriterCode(underwriterCode);
        if (underwriter == null) {
            System.out.println("❌ 해당 심사관 코드를 가진 사용자를 찾을 수 없습니다.");
            return;
        }
        
        // 심사관 ID 기반 경로 설정
        String insuranceKeyPath = "src/keys/underwriter/" + underwriter.getId() + "/public.key";
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
        
        System.out.println("🔐 진료 기록을 암호화하고, 환자 및 지정된 보험사 수신자를 위한 복호화 키를 생성합니다.\n");
	}
}
