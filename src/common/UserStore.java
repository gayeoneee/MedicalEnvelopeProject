package common;

import java.util.HashMap;
import java.util.Map;

// 사용자 계정 관리 (하드코딩된 Map 기반)
public class UserStore {
	/*UserStore는 인스턴스를 생성하지 않고도 로그인 기능 등에서 직접 호출해서 사용하는 클래스이므로, 모든 필드와 메서드가 static이어야 한다 */
	
	private static final Map<String, User> users = new HashMap<>();
	
    static {
        // 병원 관계자
        users.put("doc1", new User("doc1", "1234", Role.DOCTOR));
        users.put("nurse1", new User("nurse1", "abcd", Role.NURSE));

        // 환자
        users.put("patient1", new User("patient1", "p123", Role.PATIENT, "P2024-001"));

        // 보험사
        users.put("insure1", new User("insure1", "i456", Role.INSURANCE));
    }
    
    public static User getUserById(String id) {
        return users.get(id);
    }
}
