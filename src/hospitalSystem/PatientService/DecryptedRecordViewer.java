package hospitalSystem.PatientService;

import java.io.*;
import java.nio.file.Files;
import java.security.PublicKey;


import crypto.RSACryptoUtil;
import common.KeyManager;

public class DecryptedRecordViewer {
	// [4] 복호화된 결과 열람 (텍스트 파일 읽기)
	// diagnosis.txt, prescription.txt 출력
	
	// 리팩토링 D : 환자가 진료기록 열람 시 다음 두 전자서명 파일의 진위를 RSA 서명 검증을 통해 확인
	
    // 공개키 경로 하드코딩 (향후 개선 가능)
//    private static final Map<String, String> PUBLIC_KEY_PATHS = Map.of(
//            "DOCTOR", "src/keys/doctor/doc1/public.key",
//            "NURSE", "src/keys/nurse/nurse1/public.key"
//    );
    // -> 리펙토링 E
    // 역할에 따른 서명 파일 이름 반환
    private static String getSigFileName(String role) {
        return role.equals("DOCTOR") ? "sign_doctor.sig" : "sign_nurse.sig";
    }
    
    private static String getSignerIdFileName(String role) {
        return role.equals("DOCTOR") ? "sign_doctor_id.txt" : "sign_nurse_id.txt";
    }
    
	
    // 역할에 따라 서명 파일 경로 반환
    private static String getPublicKeyPath(String role, String signerId) {
        return "src/keys/" + role.toLowerCase() + "/" + signerId + "/public.key";
    }
	
    // 서명 검증 함수 (hashBytes + 공개키 + 서명파일)
    private static String verifySignature(String role, byte[] hashBytes, String baseDir) {
        try {
        	// 서명자 ID 로드
            File idFile = new File(baseDir + "/" + getSignerIdFileName(role));
            if (!idFile.exists()) {
            	return role + " 서명자 ID 파일 없음";
            }

            String signerId = Files.readString(idFile.toPath()).trim();
            String pubKeyPath = getPublicKeyPath(role, signerId);

            PublicKey publicKey = KeyManager.loadPublicKey(pubKeyPath);

            File sigFile = new File(baseDir + "/" + getSigFileName(role));
            if (!sigFile.exists()) {
            	return role + " 서명 없음";
            }

            byte[] sigBytes = Files.readAllBytes(sigFile.toPath());

            // hash와 서명을 이용해 진본 여부 검증
            boolean valid = RSACryptoUtil.verify(hashBytes, sigBytes, publicKey);
            return valid ? role + " 서명 (by " + signerId + "): 진본 확인됨"
                    : role + " 서명 (by " + signerId + "): 위조됨";

        } catch (Exception e) {
            return role + "서명 검증 실패:" + e.getMessage();
        }
    }
	
    // 텍스트 파일 출력 함수 (진단서/처방전용)
    private static void readAndPrintFile(String filePath, String title) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
        	throw new FileNotFoundException(title + " 파일이 존재하지 않습니다.");
        }

        System.out.println("\n" + title);
        System.out.println("----------------------");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
	
    // 진료기록 열람 + 서명 검증 출력
	public static void viewDecryptedRecord(String patientCode) throws Exception {
		String baseDir = "src/data/envelopes/" + patientCode;
		
		// 1. 해시 파일 로드
        byte[] hashBytes = Files.readAllBytes(new File(baseDir + "/hash.txt").toPath());
        
        // 2. 서명 검증 결과 출력
        System.out.println("🔍 서명 검증 결과");
        System.out.println("----------------------");
        System.out.println(verifySignature("DOCTOR", hashBytes, baseDir));
        System.out.println(verifySignature("NURSE", hashBytes, baseDir));
		
		// 3. 진단서/처방전 텍스트 출력
		readAndPrintFile(baseDir + "/diagnosis.txt", "🩺 진단서 내용");
		readAndPrintFile(baseDir + "/prescription.txt", "💊 처방전 내용");
	}
}
