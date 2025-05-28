package hospitalSystem;

import common.LoginService;
import common.Role;
import common.User;


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
                break;
            case NURSE:
                System.out.println("간호사 기능 실행 중...");
                // TODO: 전자봉투 전달, 서명 등
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
