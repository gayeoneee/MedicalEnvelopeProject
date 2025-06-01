package hospitalSystem.PatientService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DecryptedZipExtractor {
	// [3단계] 복호화된 ZIP 파일(record_decrypted.zip) 압축 해제
	// → diagnosis.txt, prescription.txt 등으로 복원
	
	public static void extractDecryptedRecord(String patientCode) throws Exception {
		// 1. 파일 경로 설정
		String baseDir = "src/data/envelopes/" + patientCode;
        String zipPath = baseDir + "/record_decrypted.zip";
        
        // 2. 복호화된 zip 파일 불러오기
        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
        	throw new Exception("3복호화된 ZIP 파일이 존재하지 않습니다.");
        }
        
        // 3. zip 압축 해제
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFile));
             ZipInputStream zis = new ZipInputStream(bis)) {

               ZipEntry entry;
               
               while ((entry = zis.getNextEntry()) != null) {
            	   
                   String filePath = baseDir + "/" + entry.getName();
                   
                   try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
                       int read;
                       
                       while ((read = zis.read()) != -1) {
                           bos.write(read);
                       }
                   }
                   
                   zis.closeEntry();
               }
        }
        
        System.out.println("📂 복호화된 진료기록 압축 해제 완료");
        System.out.println("   → diagnosis.txt, prescription.txt 파일 복원\n");
	}
}
