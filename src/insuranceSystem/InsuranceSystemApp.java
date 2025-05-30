package insuranceSystem;

import java.util.Scanner;

import common.LoginService;
import common.Role;
import common.User;
import insuranceSystem.insuranceService.InsuranceRecordArchiver;
import insuranceSystem.insuranceService.InsuranceRecordDecryptor;
import insuranceSystem.insuranceService.InsuranceRecordReceiver;
import insuranceSystem.insuranceService.InsuranceRecordViewer;
import insuranceSystem.insuranceService.InsuranceSigner;
import insuranceSystem.insuranceService.SignatureVerifier;

public class InsuranceSystemApp {

    public static void main(String[] args) {
        System.out.println("💼 보험사 시스템");

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
                    archiver.receiveEnvelopeFromPatient("src/data/insuranceInbox/" + patientCode);
                } catch (Exception e) {
                    System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                break;
            case UNDERWRITER:
                System.out.println("심사관 기능 실행 중...");
                try {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("📌 환자 식별 코드(Pxxxx_xxx)를 입력하세요: ");
                    String patientCode = scanner.nextLine();
                    String baseDir = "src/data/insuranceInbox/" + patientCode;

                    // 전자봉투 수신 및 압축 해제
                    InsuranceRecordReceiver.receiveEnvelope(patientCode);
                    
                    // 전자봉투 복호화
                    InsuranceRecordDecryptor.decryptEnvelope(user.getId(), patientCode);

                    
                    // 2️⃣ 복호화된 record_decrypted.zip 경로
                    String decryptedZipPath = baseDir + "/record_decrypted.zip";

                    // 3️⃣ 병원 서명 검증
                    SignatureVerifier verifier = new SignatureVerifier();
                    boolean verified = verifier.verifySignatures(decryptedZipPath);
                    if (verified) {
                        System.out.println("병원 서명 검증 성공! 진료기록 열람 가능.");

                        // 4️⃣ 복호화된 진료기록 열람
                        InsuranceRecordViewer.viewDecryptedRecord(patientCode);

                        // 5️⃣ 심사관 전자서명
                        InsuranceSigner signer = new InsuranceSigner();
                        signer.signAsUnderwriter(baseDir + "/", user);

                    } else {
                        System.out.println("병원 서명 검증 실패! 위조 가능성 있음.");
                    }
                } catch (Exception e) {
                    System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                break;

            case ADJUSTER:
                System.out.println("관리자 기능 실행 중...");
                try {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("📌 환자 식별 코드(Pxxxx_xxx)를 입력하세요: ");
                    String patientCode = scanner.nextLine();

                    String baseDir = "src/data/insuranceInbox/" + patientCode + "/";

                    InsuranceSigner adjusterSigner = new InsuranceSigner();
                    adjusterSigner.signAsAdjuster(baseDir, user);

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
