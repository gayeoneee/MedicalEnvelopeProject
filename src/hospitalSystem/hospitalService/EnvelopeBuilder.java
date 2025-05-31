package hospitalSystem.hospitalService;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EnvelopeBuilder {
	// 환자 코드 기반 전자봉투 생성
	public static void createEnvelope(String patientCode) throws Exception {
        // 1. 환자 기록 디렉토리 확인
        String baseDir = "src/data/records/" + patientCode;
        
        // 2. 전자봉투 구성 파일 
        String[] filesToInclude = {
                "record.enc",
                "aes_for_patient.key",
                "aes_for_insurance.key",
                "hash.txt",
                "sign_doctor.sig",
                "sign_nurse.sig"
        };
        
        // 최종 전자봉투 zip 파일
        File zipFile = new File(baseDir + "/envelope_" + patientCode + ".zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
        	for (String filename : filesToInclude) {
        		File file = new File(baseDir, filename);
        		
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
        
        System.out.println("📦 전자봉투 생성 완료: envelope_" + patientCode + ".zip");
	}
	
}
