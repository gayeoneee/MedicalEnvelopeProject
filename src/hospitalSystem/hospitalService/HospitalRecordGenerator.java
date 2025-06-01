package hospitalSystem.hospitalService;

import common.User;
import common.UserStore;
import hospitalSystem.PatientService.RecordRequestSubmitter; // 리팩토링 C

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HospitalRecordGenerator {
	
	// [1단계] 환자 식별 코드(Pxxxx-xxx) 기반 진료 기록 파일 생성
	public static void generateMedicalRecordByCode(User doctor, String patientCode) throws Exception {
		// 리팩토링 C
		// 0. 진료 요청 존재 여부 확인
        if (!RecordRequestSubmitter.hasRequest(patientCode)) {
            System.out.println("❌ 진료 요청이 존재하지 않습니다. 요청 후 진행하세요.");
            return;
        }
		
		
		// 1. 환자 코드로 UserStore에서 사용자 검색        
        // 리팩토링 9 : 전체 사용자 탐색 대신 환자 코드 검색을 통해 사용자 검색 (효율성)
        User patient = UserStore.getUserByPatientCode(patientCode);
       
        if (patient == null) {
            System.out.println("❌ 해당 환자코드의 환자가 존재하지 않습니다.");
            return;
        }
        
        
        // 2. 환자 기록 디렉토리 확인
        String baseDir = "src/data/records/" + patientCode;
        
        File dir = new File(baseDir);
        
        if (!dir.exists()) {
            System.out.println("❌ 해당 환자의 기록 폴더가 존재하지 않습니다: " + baseDir);
            return;
        }
        

        // 3. 진단서 및 처방전 존재 확인
        File diagnosisFile = new File(baseDir + "/diagnosis.txt");
        File prescriptionFile = new File(baseDir + "/prescription.txt");

        if (!diagnosisFile.exists() || !prescriptionFile.exists()) {
            System.out.println("❌ 파일이 존재하지 않습니다.");
            return;
        }
        
        
        // 리팩토링 8 : PrintWriter 출력 자원 자동 해제 처리
        // 4. timestamp.txt 생성
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(baseDir + "/timestamp.txt"))) {
            pw.println(timestamp);
        }
        
        // 5. patientCode.txt 생성
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(baseDir + "/patientCode.txt"))) {
            pw.println(patientCode);
        }
        
        System.out.println("✅ 진단서 및 처방전 기록이 정상적으로 저장되었습니다.");
        System.out.println("   → 생성 파일: diagnosis.txt, prescription.txt, timestamp.txt, patientCode.txt\n");
	}
}
