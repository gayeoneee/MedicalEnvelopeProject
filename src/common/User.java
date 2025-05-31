package common;

public class User {
    private String id;            // 로그인 ID
    private String password;      // 비밀번호
    private Role role;            // 역할
    private String patientCode;   // 환자 코드 (PATIENT인 경우만)
    private String underwriterCode;  // 심사관 코드 (ADJUSTER인 경우만) //리팩토링 B
	
	
    // 병원 관계자, 보상 담당자용 기본 생성자
    public User(String id, String password, Role role) {
        this.id = id;
        this.password = password;
        this.role = role;
    }

    // 환자용 생성자
    public User(String id, String password, Role role, String patientCode) {
        this(id, password, role);
        this.patientCode = patientCode;
    }
    
    // 심사관용 생성자
    public User(String id, String password, Role role, String patientCode, String underwriterCode) {
        this(id, password, role, patientCode);
        this.underwriterCode = underwriterCode;
    }

	public String getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public Role getRole() {
		return role;
	}
	
    public String getPatientCode() {
        return patientCode;
    }
    
    public String getUnderwriterCode() {
        return underwriterCode;
    }

    public void setUnderwriterCode(String underwriterCode) {
        this.underwriterCode = underwriterCode;
    }

}
