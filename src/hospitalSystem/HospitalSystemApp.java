package hospitalSystem;

import common.LoginService;
import common.Role;
import common.User;
import common.UserStore;
import java.util.Scanner;
import java.io.File;
import java.nio.file.*;


public class HospitalSystemApp {
	
	public static void main(String[] args) {
		System.out.println("🏥 병원 시스템");
		
		User user = login();
		
		if (user == null) {
            System.out.println("프로그램을 종료합니다.");
            return;
        }
		
		handleRoleBasedActions(user);
	}
    
	
	// 🔐 로그인 전용 메서드
	private static User login() {
        LoginService loginService = new LoginService();

        // 병원 시스템에서는 의사, 간호사, 환자만 로그인 허용
        return loginService.login(Role.DOCTOR, Role.NURSE, Role.PATIENT);
    }
	
	// 🧑‍⚕️ 로그인된 역할에 따라 동작 분기
    private static void handleRoleBasedActions(User user) {
        switch (user.getRole()) {
            case DOCTOR:
                System.out.println("의사 기능 실행 중...");
                // TODO: 진료기록 생성, 서명 등
                
                try {
                	Scanner scanner = new Scanner(System.in); //try문 안으로 scanner 넣는 거 리팩토링
                	
                	System.out.print("진료할 환자 식별 코드(Pxxxx_xxx)를 입력하세요: ");
                	String patientCode = scanner.nextLine();
                	
                	// 1. 환자 식별 코드(Pxxxx_xxx) 기반 진료 기록 생성
                	HospitalRecordGenerator.generateMedicalRecordByCode(user, patientCode);
                	
                	// 2. 진료 기록 압축 + 해시 저장
                	RecordCompressor.compressAndHash(patientCode);
                	
                	// 3. 전자 서명 (의사)
                	SignatureCreator.signHash(user, patientCode);
                	
                	// 4. 진료기록 암호화 + AES 키 생성 + 수신자별 RSA 암호화
                    EncryptionProcessor.encryptRecordWithMultiKeys(patientCode);

                    // 5. 전자봉투 zip 생성
                    EnvelopeBuilder.createEnvelope(patientCode);
                    
                    System.out.println("✅ 의사 - 전자봉투 생성 완료!");
                	
                }catch (Exception e) {
                	System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                
                break;
            case NURSE:
                System.out.println("간호사 기능 실행 중...");
                // TODO: 전자봉투 전달, 서명 등
                
                try {
                	Scanner scanner = new Scanner(System.in); //try문 안으로 scanner 넣는 거 리팩토링
                	
                	System.out.print("서명할 환자 식별 코드(Pxxxx_xxx)를 입력하세요: ");
                	String patientCode = scanner.nextLine();
                	
                	/* 코드 리팩토링 - 유효성 검사 */
                    // ✅ [1] 환자 코드 유효성 확인
                    User patient = UserStore.getUserByPatientCode(patientCode);
                    if (patient == null) {
                        System.out.println("❌ 존재하지 않는 환자 코드입니다.");
                        return;
                    }

                    // ✅ [2] 의사 서명 존재 여부 확인
                    File doctorSigFile = new File("src/data/records/" + patientCode + "/sign_doctor.sig");
                    if (!doctorSigFile.exists()) {
                        System.out.println("⛔ 의사 서명이 먼저 필요합니다.");
                        return;
                    }
                	
                	// 1. 간호사 서명 생성
                	SignatureCreator.signHash(user, patientCode);
                	
                	// 2. 전자봉투 zip 재구성  <- 간호사 서명 추가
                	EnvelopeBuilder.createEnvelope(patientCode); 
                	
                	System.out.println("✅ 간호사 서명 및 봉투 재구성 완료!");
                	
                }catch (Exception e) {
                	System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                
                break;
            case PATIENT:
                System.out.println("환자 기능 실행 중...");
                // TODO: 진료기록 열람, 복호화 등
                break;
            default:
                System.out.println("지원하지 않는 역할입니다.");
        }
    }
	
	
}
