package hospitalSystem.hospitalService;

import common.Role;
import common.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EnvelopeBuilder {
	// 환자 코드 기반 전자봉투 생성 [5단계] 의사 1차 전자봉투 생성 + [7단계] 간호사 최종 전자봉투 생성
	public static void createEnvelope(User user, String patientCode) throws Exception {
        // 1. 환자 기록 디렉토리 확인
        String baseDir = "src/data/records/" + patientCode;
        
        // 1. 역할별 전자봉투 구성 파일 정의
        String[] filesToInclude;

        if (user.getRole() == Role.NURSE) {
            filesToInclude = new String[]{
                "record_" + patientCode + ".enc",
                "aes_for_patient.key",
                "aes_for_insurance.key",
                "hash.txt",
                "sign_doctor.sig",
                "sign_nurse.sig",
                "sign_doctor_id.txt",
                "sign_nurse_id.txt"
            };
        } 
        else if (user.getRole() == Role.DOCTOR) {
            filesToInclude = new String[]{
                "record_" + patientCode + ".enc",
                "aes_for_patient.key",
                "aes_for_insurance.key",
                "hash.txt",
                "sign_doctor.sig",
                "sign_doctor_id.txt"
            };
        } 
        else {
            System.out.println("⛔ 전자봉투 생성을 지원하지 않는 역할입니다.");
            return;
        }

        // 2. ZIP 압축 파일 생성
        File zipFile = new File(baseDir + "/envelope_" + patientCode + ".zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
        	for (String filename : filesToInclude) {
        		File file = new File(baseDir, filename);
        		
//        		System.out.println("🔍 경로 확인 중: " + file.getAbsolutePath()); //디버깅 용

        	    if (!file.exists()) {
        	        System.out.println("⚠️ 포함되지 않음 (파일 없음): " + filename);
        	        continue;
        	    }
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(filename));
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
        	}
        }
        
        String message = (user.getRole() == Role.DOCTOR) ? "📩 의사의 진료기록이 담긴 임시 전자봉투가 생성되었습니다!" : "📩 환자에게 보낼 최종 전자봉투가 생성되었습니다!";
        System.out.println(message);
	}
	
	
	
}
