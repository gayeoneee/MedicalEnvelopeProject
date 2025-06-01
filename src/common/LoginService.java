package common;

import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

import crypto.HashUtil;

//로그인 검증 로직 수행 클래스
public class LoginService {
	public User login(Role... allowedRoles) throws Exception {
	    try (Scanner scanner = new Scanner(System.in)) {
	        System.out.print("아이디 입력: ");
	        String id = scanner.nextLine();

	        System.out.print("비밀번호 입력: ");
	        String pw = scanner.nextLine();

	        User user = UserStore.getUserById(id);
	        
	        /* 로그인 검증 로직 */
	        
	        // 로그인 성공 시 : 해당 아이디의 유저가 있고, 그 아이디와 비밀번호가 일치해아햠
	        if (user != null && HashUtil.verifySHA256(pw, user.getPassword())) { // 리팩토링 10 : 비밀번호는 해시 함수를 이용하여 저장
	            List<Role> allowed = Arrays.asList(allowedRoles);
	            
	            if (allowed.contains(user.getRole())) {
	                System.out.println("✅ 로그인 성공!");
	                return user;
	            } 
	            else { // 해당 시스템에서 허용하는 역할인지 
	                System.out.println("⛔ 이 시스템에서 허용되지 않는 역할입니다.");
	            }
	        } 
	        else { // 로그인 실패 시
	        	if (user == null) {
	        	    System.out.println("❌ 로그인 실패: 존재하지 않는 아이디입니다.");
	        	} 
	        	else if (!user.getPassword().equals(pw)) {
	        	    System.out.println("❌ 로그인 실패: 비밀번호가 틀렸습니다.");
	        	}
	        }
	    }
	    catch (Exception e) {
            System.out.println("⚠ 로그인 중 오류 발생: " + e.getMessage());
        }
	    
	    return null;
	}

}
