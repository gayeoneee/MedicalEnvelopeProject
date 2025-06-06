package common;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import crypto.HashUtil;

// 사용자 계정 관리 (하드코딩된 Map 기반)
public class UserStore {
	/*UserStore는 인스턴스를 생성하지 않고도 로그인 기능 등에서 직접 호출해서 사용하는 클래스이므로, 모든 필드와 메서드가 static이어야 한다 */
	
	// 로그인 ID 기준으로 저장된 사용자
    private static final Map<String, User> usersById = new HashMap<>();
	
    // 리팩토링 7 : 환자 식별 코드(Pxxxx_xxx) 기준으로 저장된 사용자 (환자만 해당)
    private static final Map<String, User> usersByPatientCode = new HashMap<>();
    
    private static final Map<String, User> usersByUnderwriterCode = new HashMap<>();
    
    static {
        try {
            // 병원 관계자
            usersById.put("doc1", new User("doc1", HashUtil.generateSHA256("d1234"), Role.DOCTOR));
            usersById.put("nurse1", new User("nurse1", HashUtil.generateSHA256("n1234"), Role.NURSE));

            // 환자
            User patient1 = new User("patient1", HashUtil.generateSHA256("p1234"), Role.PATIENT, "P2025_001");
            usersById.put("patient1", patient1);
            usersByPatientCode.put(patient1.getPatientCode(), patient1);


            // 보험사 관계자
            User under1 = new User("under1", HashUtil.generateSHA256("u1234"), Role.UNDERWRITER, null, "U2025_001");
            usersById.put("under1", under1);
            usersByUnderwriterCode.put(under1.getUnderwriterCode(), under1);

            usersById.put("adjust1", new User("adjust1", HashUtil.generateSHA256("a1234"), Role.ADJUSTER));
            
        } catch (Exception e) {
            System.out.println("⚠️ 사용자 초기화 중 오류: " + e.getMessage());
        }
    }
    
    // 로그인 ID로 사용자 조회
    public static User getUserById(String id) {
        return usersById.get(id);
    }
    
    // 전체 사용자 리스트 반환
    public static Collection<User> getAllUsers() {
        return usersById.values();
    }
   

    // 환자 식별 코드로 환자 조회
    public static User getUserByPatientCode(String patientCode) {
        return usersByPatientCode.get(patientCode);
    }
    
    // 심사관 코드로 심사관 조회
    public static User getUserByUnderwriterCode(String code) {
        for (User u : usersById.values()) {
            if (u.getRole() == Role.UNDERWRITER && code.equals(u.getUnderwriterCode())) {
                return u;
            }
        }
        return null;
    }
}
