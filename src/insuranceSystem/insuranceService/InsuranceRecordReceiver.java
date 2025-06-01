package insuranceSystem.insuranceService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InsuranceRecordReceiver {
	// [1] 전자봉투 수신 및 압축 해제 
		// ex) envelope_P2025_001.zip → record.enc, hash.txt 등 파일 추출
		//     data/envelopes/P2025_001/ 에 저장
		public static void receiveEnvelope(String patientCode) throws Exception {
			// 1. 수신받은 전자봉투 디렉토리 경로
			String sourceDir = "src/data/insuranceInbox/" + patientCode;
	        String zipPath = sourceDir + "/envelope_" + patientCode + ".zip";
	        
	        // 2. zip파일 추출 저장할 디렉토리 설정
	        String destDir = "src/data/insuranceInbox/" + patientCode;
	        new File(destDir).mkdirs(); //리팩토링 (만약 디렉토리가 없으면 만들기로)
	        
	        
	        // 2. 전자봉투 가져오기
	        File zipFile = new File(zipPath);
	        if (!zipFile.exists()) {
	        	throw new Exception("전자봉투(zip)가 존재하지 않습니다.");
	        }
	        
	        
	        
	        // 3. zip 파일 열기
	        // 참고 코드 (공부함) : https://velog.io/@wlgns3855/JAVA-java%EB%A1%9C-zip%ED%8C%8C%EC%9D%BC-%EC%95%95%EC%B6%95%ED%92%80%EA%B8%B0        
//	        FileInputStream fis = new FileInputStream(zipFile);
//	        BufferedInputStream bis = new BufferedInputStream(fis);
//	        
//	        ZipInputStream zis = new ZipInputStream(bis);
//	        ZipEntry zipEntry = null;
//	        
//	        while( (zipEntry = zis.getNextEntry()) != null ) {
//	        	String filePath = destDir + "/" + zipEntry.getName();
//	        	
//	        	File outFile = new File(filePath);
//	        	
//	        	FileOutputStream fos = new FileOutputStream(outFile);
//	        	BufferedOutputStream bos = new BufferedOutputStream(fos);
//	        	
//	        	int read;
//	        	
//	        	while( (read = zis.read()) != -1 ) {
//	        		bos.write(read);; //1바이트씩 기록
//	        	}
//	        	
//	        	bos.close();
//	        	fos.close();
//	        }
//	        
//	        zis.close();
//	        bis.close();
//	        fis.close();
	        
	        // 리팩토링
	        
	        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zipFile));
	                ZipInputStream zis = new ZipInputStream(bis)) {

	               ZipEntry entry;
	               while ((entry = zis.getNextEntry()) != null) {
	                   String filePath = destDir + "/" + entry.getName();
	                   File outFile = new File(filePath);
	                   try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile))) {
	                       int read;
	                       while ((read = zis.read()) != -1) {
	                    	   bos.write(read); // 1바이트씩 기록
	                       }
	                   }
	                   zis.closeEntry();
	               }
	           }
	        
	        
	        System.out.println("📦 전자봉투 수신 및 압축 해제 완료: " + zipPath);
		}
}
