package hospitalSystem;

import common.LoginService;
import common.Role;
import common.User;
import common.UserStore;
import hospitalSystem.hospitalService.*;
import hospitalSystem.PatientService.*;

import java.util.Scanner;
import java.io.File;


public class HospitalSystemApp {
	
	public static void main(String[] args) throws Exception {
		System.out.println("🏥 병원 시스템");
		System.out.println("==========================================================");
		
        // 로그인
		Scanner scanner = new Scanner(System.in);
        User user = login();
        
        if (user == null) {
            System.out.println("프로그램을 종료합니다.");
            return;
        }
        
        //‍ 역할에 따른 기능 실행
        handleRoleBasedActions(user);
	}
    
	
	// 로그인 전용 메서드
	private static User login() throws Exception {
        LoginService loginService = new LoginService();

        // 병원 시스템에서는 의사, 간호사, 환자만 로그인 허용
        return loginService.login(Role.DOCTOR, Role.NURSE, Role.PATIENT);
    }
	
	// 진료 요청 파일이 존재하는지 확인
    private static boolean isRequestExists(String patientCode) {
        File requestFile = new File("src/data/requests/" + patientCode + "/request.txt");
        return requestFile.exists();
    }
	
	// 로그인된 역할에 따라 동작 분기
    private static void handleRoleBasedActions(User user) {
        switch (user.getRole()) {
            case DOCTOR:
                System.out.println("의사 기능 실행 중...");
                // TODO: 진료기록 생성, 서명 등
                
             // 리펙토링 1 : try-with-resources로 개선
                try (Scanner scanner = new Scanner(System.in)) {
                	
                	System.out.print("진료할 환자 식별 코드(Pxxxx_xxx)를 입력하세요: ");
                	String patientCode = scanner.nextLine();
                	
                    // 유효성 검사 1 - 환자 코드 유효성 확인
                    User patient = UserStore.getUserByPatientCode(patientCode);
                    if (patient == null) {
                        System.out.println("❌ 존재하지 않는 환자 코드입니다.");
                        return;
                    }
                    
                	// 유효성 검사 2- 진료 요청 확인
                    if (!isRequestExists(patientCode)) {
                        System.out.println("⛔ 환자의 요청이 존재하지 않아 전자봉투 생성이 중단되었습니다.");
                        return;
                    }
                    
                    System.out.println("===================================");
                    System.out.println("📌 환자 진료를 시작합니다.\n");
                	
                    /* 병원측 플로우 */
                	// [1단계] 환자 식별 코드(Pxxxx_xxx) 기반 진료 기록 생성
                	HospitalRecordGenerator.generateMedicalRecordByCode(user, patientCode);
                	
                	// [2단계] 진료 기록 압축 + 해시 저장
                	RecordCompressor.compressAndHash(patientCode);
                	
                	// [3단계] 전자 서명 (의사)
                	SignatureCreator.signHash(user, patientCode);
                	
                	// [4단계] 진료기록 암호화 + AES 키 생성 + 수신자별 RSA 암호화
                    EncryptionProcessor.encryptRecordWithMultiKeys(patientCode);

                    // [5단계] 전자봉투 zip 생성
                    EnvelopeBuilder.createEnvelope(user, patientCode);
                    
                    System.out.println("===================================");
                    System.out.println("✅ 의사 - 진료 후 진료 기록이 생성되었습니다. 간호사에게 진료 기록을 보냈습니다!");
                	
                }catch (Exception e) {
                	System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                
                break;
            case NURSE:
                System.out.println("간호사 기능 실행 중...");
                // TODO: 전자봉투 전달, 서명 등
                
                try (Scanner scanner = new Scanner(System.in)) {
                	
                	System.out.print("진료 기록을 보낼 환자 식별 코드(Pxxxx_xxx)를 입력하세요: ");
                	String patientCode = scanner.nextLine();
                	
                    // 유효성 검사 1 - 환자 코드 유효성 확인
                    User patient = UserStore.getUserByPatientCode(patientCode);
                    if (patient == null) {
                        System.out.println("❌ 존재하지 않는 환자 코드입니다.");
                        return;
                    }
                    
                	// 유효성 검사 2- 진료 요청 확인
                    if (!isRequestExists(patientCode)) {
                        System.out.println("⛔ 환자의 요청이 존재하지 않아 전자봉투 생성이 중단되었습니다.");
                        return;
                    }

                    // 유효성 검사 3- 의사 서명 존재 여부 확인
                    File doctorSigFile = new File("src/data/records/" + patientCode + "/sign_doctor.sig");
                    if (!doctorSigFile.exists()) {
                        System.out.println("⛔ 의사 서명이 먼저 필요합니다.");
                        return;
                    }
                    
                    System.out.println("===================================");
                    System.out.println("📌 의사에게 받은 진료기록을 환자에게 보내기 위해 서류 작업을 시작합니다!\n");
                    
                    /* 병원측 플로우 */
                	// [6단계] 간호사 서명 생성
                	SignatureCreator.signHash(user, patientCode);
                	
                	// [7단계] 전자봉투 zip 재구성 (간호사 서명 추가) 후 환자에게 전송
                	EnvelopeBuilder.createEnvelope(user, patientCode);
                	
                    System.out.println("===================================");
                	System.out.println("✅ 간호사 - 환자에게 진료기록을 전송했습니다! ");
                	
                }catch (Exception e) {
                	System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                
                break;
            case PATIENT:
                System.out.println("환자 기능 실행 중...");
                // TODO: 진료기록 열람, 복호화 등
                
                // 현재 로그인된 사용자의 ID 가져오기
                String patientId = user.getId();
                String patientCode = user.getPatientCode();
                
                try (Scanner scanner = new Scanner(System.in)) {
                	
                	// 메뉴 선택
                    System.out.println("\n원하시는 기능을 선택해주세요!");
                	System.out.println("1. 진료 요청 제출");
                    System.out.println("2. 진료기록 열람 및 보험사 제출\n");
                    System.out.print("번호 선택 > ");
                    int menu = scanner.nextInt();
                    
                    scanner.nextLine(); // 버퍼 비우기
                    System.out.println("===================================");
                    
                    
                    // 요청 파일 경로
                    File requestFile = new File("src/data/requests/" + patientCode + "/request.txt");
                    
                    // 1. 진료 요청 제출
                    if (menu == 1) {
                    	// 이미 존재할 경우 차단
                        if (requestFile.exists()) {
                            System.out.println("⚠️ 이미 진료 요청이 제출되어 있습니다. 중복 요청은 불가능합니다.");
                            return;
                        }
                        
                        System.out.print("📌 희망하는 심사관 코드(Uxxxx_xxx)를 입력하세요: ");
                        String adjusterCode = scanner.nextLine();
                        
                        // 진료 요청 제출
                        RecordRequestSubmitter.submitRequest(patientCode, adjusterCode);
                        
                        return;
                    }
                    
                    // 2. 진료기록 열람 및 보험사 제출
                    else if (menu == 2) {
                    	// 요청이 없으면 차단
                        if (!requestFile.exists()) {
                            System.out.println("⛔ 진료 요청이 존재하지 않습니다. 먼저 요청을 제출해주세요.");
                            return;
                        }
                        
                        System.out.println("📌 병원으로부터 받은 전자봉투를 수신합니다...\n ");
                        
                        /* 환자 측 플로우 */
                        // [1단계] 전자봉투 수신 및 압축 해제
                        PatientEnvelopeReceiver.receiveEnvelope(patientCode);

                        // [2단계] 암호화된 진료기록 복호화                    
                        EnvelopeDecryptor.decryptEnvelope(patientId, patientCode);

                        // [3단계] 복호화된 ZIP 압축 해제
                        DecryptedZipExtractor.extractDecryptedRecord(patientCode);

                        // [4단계] 진단서 및 처방전 열람
                        DecryptedRecordViewer.viewDecryptedRecord(patientCode);

                        // [5단계] 보험사로 전자봉투 전송
                        EnvelopeForwarder.forwardEnvelope(patientCode);
                        
                        System.out.println("===================================");
                        System.out.println("✅ 환자 - 열람한 진료기록을 보험사에 제출했습니다!");
                        
                    }
                    
                    else {
                        System.out.println("❌ 잘못된 입력입니다.");
                    }

                    
                }catch (Exception e) {
                    System.out.println("❌ 오류 발생: " + e.getMessage());
                }
                
                
                break;
            default:
                System.out.println("지원하지 않는 역할입니다.");
        }
    }
	
	
}
