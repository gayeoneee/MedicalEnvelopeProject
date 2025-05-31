package hospitalSystem.PatientService;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

// 리팩토링 C : 환자가 먼저 병원에 진료 요청을 해야만 병원에서 진료기록 생성 가능하도록 흐름을 구성
public class RecordRequestSubmitter {
	// 진료 요청 제출 (파일 생성)
	public static void submitRequest(String patientCode, String underwriterCode) throws IOException {
		// 1. 파일 경로 설정
		String dirPath = "src/data/requests/" + patientCode;
        String filePath = dirPath + "/request.txt";
        
        File dir = new File(dirPath);
        if (!dir.exists()) {	// 디렉토리가 없을 경우 디렉토리 생성
        	dir.mkdirs();
        }
        
        // 2. 심사관 코드 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(underwriterCode);
        }
        
        System.out.println("진료 요청 제출 완료 → " + filePath);
	}
	
	// 진료 요청 존재 여부 확인
	// File.exists : 파일이 존재하는지 여부. 반환결과가 boolean으로 파일이 존재하면 참, 없으면 거짓을 반환 한다. 
    public static boolean hasRequest(String patientCode) {
        String filePath = "src/data/requests/" + patientCode + "/request.txt";
        
        return Files.exists(new File(filePath).toPath());
    }
    
    // 요청된 심사관 코드 읽기
    // Files.readString : 파일에서 문자열 읽을 수 있음
    // File(filePath).toPath() : 파일의 경로를 찾아 반환한다
    public static String readUnderwriterCode(String patientCode) throws IOException {
        String filePath = "src/data/requests/" + patientCode + "/request.txt";
        
        return Files.readString(new File(filePath).toPath()).trim();
    }
}
