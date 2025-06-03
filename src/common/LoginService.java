package common;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

import crypto.HashUtil;

//로그인 검증 로직 수행 클래스
public class LoginService {
	public User login(Role... allowedRoles) throws Exception {
		 Scanner scanner = new Scanner(System.in); // try-with-resources 사용하지 않음 (닫지 말아야 함) : 입력 버퍼 정리 문제

		    System.out.print("아이디 입력: ");
		    String id = scanner.nextLine();
		    
		    // 리팩토링 : 보안 입력 처리 String → char[]
		    System.out.print("비밀번호 입력: ");
//		    String pw = scanner.nextLine();
		    String pwInput = scanner.nextLine();
            char[] pw = pwInput.toCharArray();
            pwInput = null; // String 참조 제거

		    User user = UserStore.getUserById(id);

		    // 로그인 성공 시 
		    if (user != null && HashUtil.verifySHA256(pw, user.getPassword())) {
		    	Arrays.fill(pw, ' '); // 입력 후 메모리 정리
		        
		    	List<Role> allowed = Arrays.asList(allowedRoles);

		        if (allowed.contains(user.getRole())) {
		            System.out.println("\n✅ 로그인 성공!");
		    		System.out.println("==========================================================");
		            return user;
		        } else {
		            System.out.println("⛔ 이 시스템에서 허용되지 않는 역할입니다.");
		        }
		    }
		    // 로그인 실패
		    else {
		        if (user == null) {
		            System.out.println("❌ 로그인 실패: 존재하지 않는 아이디입니다.");
		        } else {
		            System.out.println("❌ 로그인 실패: 비밀번호가 틀렸습니다.");
		        }
		    }

		    return null;
		}
}

