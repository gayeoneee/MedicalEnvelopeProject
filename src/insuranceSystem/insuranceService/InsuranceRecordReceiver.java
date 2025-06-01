package insuranceSystem.insuranceService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InsuranceRecordReceiver {
		// [2단계] 전자봉투 수신 및 압축 해제
		// - envelope_*.zip 파일을 압축 해제하여 내부 파일들 추출
		// - 대상 디렉토리: src/data/insuranceInbox/Pxxxx_xxx/
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
	        
	        
	        System.out.println("📦 환자가 제출한 전자봉투를 성공적으로 수신하고 압축을 해제했습니다.\n");
		}
}
