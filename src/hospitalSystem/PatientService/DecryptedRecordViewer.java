package hospitalSystem.PatientService;

import java.io.*;

public class DecryptedRecordViewer {
	// [4] 복호화된 결과 열람 (텍스트 파일 읽기)
	// diagnosis.txt, prescription.txt 출력
	
    private static void readAndPrintFile(String filePath, String title) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
        	throw new FileNotFoundException(title + " 파일이 존재하지 않습니다.4");
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
		String baseDir = "src/data/envelopes/" + patientCode;
		
		readAndPrintFile(baseDir + "/diagnosis.txt", "🩺 진단서 내용");
		readAndPrintFile(baseDir + "/prescription.txt", "💊 처방전 내용");
	}
}
