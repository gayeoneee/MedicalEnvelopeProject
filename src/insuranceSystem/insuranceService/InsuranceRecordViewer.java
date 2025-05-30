package insuranceSystem.insuranceService;

import java.io.*;
import java.util.zip.*;

public class InsuranceRecordViewer {

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
	
	
	public static void viewDecryptedRecord(String patientCode) throws Exception {
	    String baseDir = "src/data/insuranceInbox/" + patientCode;
	    String zipFilePath = baseDir + "/record_decrypted.zip";

	    // 1️⃣ 압축 해제
	    String outputDir = baseDir + "/InsuranceDocuments"; //진단서 처방전 담을 파일
	    unzip(zipFilePath, outputDir);

	    // 2️⃣ 해제된 파일 읽기
	    readAndPrintFile(outputDir + "/diagnosis.txt", "🩺 진단서 내용");
	    readAndPrintFile(outputDir + "/prescription.txt", "💊 처방전 내용");
	}

	private static void unzip(String zipFilePath, String outputDir) throws IOException {
	    File dir = new File(outputDir);
	    if (!dir.exists()) dir.mkdirs();

	    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
	        ZipEntry entry;
	        while ((entry = zis.getNextEntry()) != null) {
	            File newFile = new File(outputDir, entry.getName());
	            try (FileOutputStream fos = new FileOutputStream(newFile)) {
	                byte[] buffer = new byte[1024];
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                    fos.write(buffer, 0, len);
	                }
	            }
	            zis.closeEntry();
	        }
	    }
	}

}
