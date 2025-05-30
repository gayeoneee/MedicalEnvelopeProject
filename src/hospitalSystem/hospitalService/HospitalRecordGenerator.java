package hospitalSystem.hospitalService;

import common.Role;
import common.User;
import common.UserStore;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;

public class HospitalRecordGenerator {
	
	// 1단계: 환자 식별 코드(Pxxxx-xxx) 기반 진료 기록 파일 생성
	public static void generateMedicalRecordByCode(User doctor, String patientCode) throws Exception {
		// 1. 환자 코드로 UserStore에서 사용자 검색
		/*
        User patient = null;
        
        Collection<User> allUsers = UserStore.getAllUsers();
        for (User u : allUsers) {
            if (u.getRole() == Role.PATIENT && patientCode.equals(u.getPatientCode())) {
                patient = u;
                break;
            }
        }
        */
        
        // 리팩토링 A (UserStore도 수정됨)
        User patient = UserStore.getUserByPatientCode(patientCode);

        if (patient == null) {
            System.out.println("❌ 해당 식별 코드의 환자가 존재하지 않습니다.");
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
        /* 후에 리팩토링으로 목업처리된 진단서, 처방전만 불러오는 게 아닌 해당 디렉터리 아래에 있는 파일 모두 불러오는 방식으로 수정 */
        File diagnosisFile = new File(baseDir + "/diagnosis.txt");
        File prescriptionFile = new File(baseDir + "/prescription.txt");

        if (!diagnosisFile.exists() || !prescriptionFile.exists()) {
            System.out.println("파일이 존재하지 않습니다.");
            return;
        }
        
        // 4. timestamp.txt 생성
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        FileOutputStream fos = new FileOutputStream(baseDir + "/timestamp.txt");
        PrintWriter pw = new PrintWriter(fos);
        pw.println(timestamp);
        
        fos.close();
        pw.close();
        
        // 리팩토링
        /*
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(baseDir + "/timestamp.txt"))) {
            pw.println(timestamp);
        }
        */
        
        // 5. patientCode.txt 생성
        FileOutputStream fos2 = new FileOutputStream(baseDir + "/patientCode.txt");
        PrintWriter pw2 = new PrintWriter(fos2);
        pw2.println(timestamp);
        
        fos2.close();
        pw2.close();
        
        //리팩토링
        /*
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(baseDir + "/patientCode.txt"))) {
            pw.println(patientCode);
        }
        */
        
        System.out.println("✅ 기록 보완 완료 → " + baseDir);
	}
}
