package common;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

//로그인 검증 로직 수행 클래스
public class LoginService {
	public User login(Role... allowedRoles) {
		Scanner scanner = new Scanner(System.in);	// 스캐너 닫기 리팩토링 할 수 있을 듯
		
		
		System.out.print("아이디 입력: ");
        String id = scanner.nextLine();

        System.out.print("비밀번호 입력: ");
        String pw = scanner.nextLine();
        
        User user = UserStore.getUserById(id);
        
        // 로그인 검증 로직
        if(user != null && user.getPassword().equals(pw)) {	// 해당 아이디를 가진 사용자가 있고, 패스워드가 일치할 경우
        	
        	List<Role> allowed = Arrays.asList(allowedRoles);
        	
        	if (allowed.contains(user.getRole())) {
        		System.out.println("✅ 로그인 성공!");
        		
        		// 디버깅 용
        		if (user.getRole() == Role.PATIENT) {
                    System.out.println("📌 환자 식별자: " + user.getPatientCode());
                }
        		
        		return user;
        	}
        	else {
                System.out.println("⛔ 이 시스템에서 허용되지 않는 역할입니다."); // 해당 사용자는 있지만 이 시스템에서 사용하지 않는 역할일때
            }
        }
        else {
            System.out.println("❌ 로그인 실패: 아이디 또는 비밀번호가 틀렸습니다."); //후에 아이디가 틀렸는지 비밀번호가 틀렸는지 오류 메세지 추가 로직 
        }
        
        return null;
       
	}

}
