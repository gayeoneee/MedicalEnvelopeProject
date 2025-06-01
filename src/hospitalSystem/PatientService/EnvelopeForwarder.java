package hospitalSystem.PatientService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class EnvelopeForwarder {
	// [5단계] 전자봉투 보험사로 전송
	// envelope_P2025_001.zip → data/insuranceInbox/ 폴더로 복사
	
	public static void forwardEnvelope(String patientCode) throws Exception {
		// 1. 파일 경로 설정
		String sourcePath = "src/data/records/" + patientCode + "/envelope_" + patientCode + ".zip";	// 병원에서 받은 전자봉투 경로
        String targetDir = "src/data/insuranceInbox/"+ patientCode;	// (환자 -> 보험사) 보험사에 보내지는 경로
        new File(targetDir).mkdirs(); //리팩토링 (만약 디렉토리가 없으면 만들기로)
        String targetPath = targetDir + "/envelope_" + patientCode + ".zip";
        
        // 2. 전송 할 파일
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
        	throw new Exception("전송할 전자봉투가 존재하지 않습니다.");
        }
        
        // 3. 전자봉투 복사
        Files.copy(Path.of(sourcePath), Path.of(targetPath), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("📤 전자봉투가 보험사로 성공적으로 전송되었습니다 → " + targetPath);
	}
}
