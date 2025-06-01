package insuranceSystem;

import java.util.Scanner;

import common.LoginService;
import common.Role;
import common.User;
import common.UserStore;
import insuranceSystem.insuranceService.InsuranceRecordArchiver;
import insuranceSystem.insuranceService.InsuranceRecordDecryptor;
import insuranceSystem.insuranceService.InsuranceRecordReceiver;
import insuranceSystem.insuranceService.InsuranceRecordViewer;
import insuranceSystem.insuranceService.InsuranceSigner;
import insuranceSystem.insuranceService.SignatureVerifier;

public class InsuranceSystemApp {

    public static void main(String[] args) throws Exception {
        System.out.println("💼 보험사 시스템");
		System.out.println("==========================================================");
        
        // 🔐 로그인
        User user = login();

        if (user == null) {
            System.out.println("프로그램을 종료합니다.");
            return;
        }
        
        // 🧑‍💼 역할에 따른 기능 실행
        handleRoleBasedActions(user);
    }

    // 🔐 로그인 전용 메서드
    private static User login() throws Exception {
        LoginService loginService = new LoginService();

        // 보험사 시스템에서는 환자, 심사관(UNDERWRITER), 보상담당자(ADJUSTER)만 로그인 허용
        return loginService.login(Role.PATIENT, Role.UNDERWRITER, Role.ADJUSTER);
    }

    // 🧑‍💼 로그인된 사용자 역할에 따라 기능 분기
    private static void handleRoleBasedActions(User user) {
        switch (user.getRole()) {
            case PATIENT:
                System.out.println("환자 기능 실행 중...");
                try {
                	String patientCode = user.getPatientCode();
                    InsuranceRecordArchiver archiver = new InsuranceRecordArchiver();
                    
                    // [0단계] 환자 → 수신 전자봉투 보관
                    archiver.receiveEnvelopeFromPatient("src/data/insuranceInbox/" + patientCode);
                } catch (Exception e) {
                    System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                break;
            // 🧑‍⚖️ [1~5단계] 심사관 (Underwriter)    
            case UNDERWRITER: 
                System.out.println("심사관 기능 실행 중...");
                try {
                	
                    try(Scanner scanner = new Scanner(System.in)) {
	                    System.out.print("📌 심사할 환자의 코드(Pxxxx_xxx)를 입력해주세요 : ");
	                    String patientCode = scanner.nextLine();
	                    
	                    // 유효성 검사 1 - 환자 코드 유효성 확인
	                    User patient = UserStore.getUserByPatientCode(patientCode);
	                    if (patient == null) {
	                        System.out.println("❌ 존재하지 않는 환자 코드입니다.");
	                        return;
	                    }
	                    
	                    String baseDir = "src/data/insuranceInbox/" + patientCode;
	                    
	                    // [1단계] 환자가 지정한 심사관 코드 확인
	                    String requestFilePath = "src/data/requests/" + patientCode + "/request.txt";
	                    String requestedUnderwriterId = null;
	                    
	                    try (Scanner fileScanner = new Scanner(new java.io.File(requestFilePath))) {
	                        if (fileScanner.hasNextLine()) {
	                            requestedUnderwriterId = fileScanner.nextLine().trim();
	                        }
	                    }

	                    // 요청된 심사관과 로그인한 심사관이 일치하지 않으면 기능 차단
	                    if (requestedUnderwriterId == null || !requestedUnderwriterId.equals(user.getUnderwriterCode())) {
	                        System.out.println("⚠️ 환자가 지정한 심사관이 아니므로 접근이 제한됩니다.");
	                        return; // 종료
	                    }
	                    
	                    System.out.println("===================================");
	                    System.out.println("📌 환자에게 받은 진료 기록을 심사하겠습니다!\n");
	
	                    // [2단계] 전자봉투 수신 및 압축 해제
	                    InsuranceRecordReceiver.receiveEnvelope(patientCode);
	                    
	                    // [3단계] 전자봉투 복호화
	                    InsuranceRecordDecryptor.decryptEnvelope(user.getId(), patientCode);
	
	                    
	                    // 복호화된 record_decrypted.zip 경로
	                    String decryptedZipPath = baseDir + "/record_decrypted.zip";
	
	                    // [4단계] 병원 서명 검증
	                    SignatureVerifier verifier = new SignatureVerifier();
	                    boolean verified = verifier.verifySignatures(decryptedZipPath);
	                    if (verified) {
	                        System.out.println("병원 서명 검증 성공! 진료기록 열람 가능.");
	
	                        // [5단계] 복호화된 진료기록 열람
	                        InsuranceRecordViewer.viewDecryptedRecord(patientCode);
	
	                        // [6단계] 심사관 전자서명 -> 심사관이 심사했음을 증명
	                        InsuranceSigner signer = new InsuranceSigner();
	                        signer.signAsUnderwriter(baseDir + "/", user);
	
	                    } else {
	                        System.out.println("병원 서명 검증 실패! 위조 가능성 있음.");
	                    }
                    }
                } catch (Exception e) {
                    System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                break;
            // 🧑‍💼 [7~8단계] 보상담당자 (Adjuster)
            case ADJUSTER:
                System.out.println("관리자 기능 실행 중...");
                try (Scanner scanner = new Scanner(System.in)) {
                    System.out.print("📌 심사할 환자의 코드(Pxxxx_xxx)를 입력해주세요 : ");
                    String patientCode = scanner.nextLine();
                    
                    // 유효성 검사 1 - 환자 코드 유효성 확인
                    User patient = UserStore.getUserByPatientCode(patientCode);
                    if (patient == null) {
                        System.out.println("❌ 존재하지 않는 환자 코드입니다.");
                        return;
                    }
                    
                    String baseDir = "src/data/insuranceInbox/" + patientCode + "/";
                    
                    System.out.println("===================================");
                    System.out.println("📌 보험금 청구를 시작하겠습니다!\n\n");
                    
                    // [7단계] 보상담당자 서명 -> 보험담당자가 청구 했음을 증명
                    InsuranceSigner adjusterSigner = new InsuranceSigner();
                    adjusterSigner.signAsAdjuster(baseDir, user);
                    
                    // [8단계] 전자봉투 보관 → 보험사 보관소로 이동
                    InsuranceRecordArchiver finalArchiver = new InsuranceRecordArchiver();
                    finalArchiver.archiveFinalEnvelope(baseDir, patientCode);
                } catch (Exception e) {
                    System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                break;
            default:
                System.out.println("지원하지 않는 역할입니다.");
        }
    }
}
