package insuranceSystem;

import common.LoginService;
import common.Role;
import common.User;

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
                // TODO: 보험청구용 전자봉투 제출
                break;
            case UNDERWRITER:
                System.out.println("심사관 기능 실행 중...");
                // TODO: 전자봉투 검증 및 심사 승인
                break;
            case ADJUSTER:
                System.out.println("보상담당자 기능 실행 중...");
                // TODO: 심사 승인 여부 확인 및 보상 처리
                break;
            default:
                System.out.println("지원하지 않는 역할입니다.");
        }
    }
}

