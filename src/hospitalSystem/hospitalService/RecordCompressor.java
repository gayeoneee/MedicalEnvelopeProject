package hospitalSystem.hospitalService;

import crypto.HashUtil;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RecordCompressor {
	// 압축 + 해시 생성 → hash.txt 저장
	public static void compressAndHash(String patientCode) throws Exception {
		String baseDir = "src/data/records/" + patientCode;
		
		/* 1. 압축 */
		// 압축 대상 파일들
        String[] filesToZip = {
                "diagnosis.txt",
                "prescription.txt",
                "timestamp.txt",
                "patientCode.txt"
        };
        
        // ZIP 파일 생성
        // 참고 코드 (공부함) : https://velog.io/@wlgns3855/JAVA-java%EB%A1%9C-zip%ED%8C%8C%EC%9D%BC-%EB%A7%8C%EB%93%A4%EA%B8%B0
        File zipFile = new File(baseDir + "/record.zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
        	for(String fileName : filesToZip){
        		File file = new File(baseDir, fileName);
        		
        		if (!file.exists()) {
                    System.out.println("누락된 파일: " + fileName);
                    return;
                }
        		
        		try (FileInputStream fis = new FileInputStream(file)) {
        			zos.putNextEntry(new ZipEntry(fileName));
        			
        			byte[] buf = new byte[4096];
        			int length;
        			
                    while ((length = fis.read(buf)) > 0) {
                        zos.write(buf, 0, length);
                    }
                    
                    zos.closeEntry();
        		}

        	}
        }
        System.out.println("📦 record.zip 생성 완료");
        
        /* 2. 해시 값(SHA-256) 생성 */
        byte[] zipBytes = Files.readAllBytes(zipFile.toPath());
        byte[] hash = HashUtil.generateSHA256(zipBytes);
        
        /* 3. 해시 한 결과 저장 */
        File hashFile = new File(baseDir + "/hash.txt");
        try (FileOutputStream fos = new FileOutputStream(hashFile)) {
            fos.write(hash);
        }
        
        System.out.println("✅ hash.txt 생성 완료 (SHA-256)");
	}
}
