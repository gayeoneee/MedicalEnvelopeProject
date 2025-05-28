package common;

public class User {
	private String id;
	private String password;
	private Role role;
	private String patientCode; // 환자 전용 식별자
	
    // 일반 사용자 생성자 (의사, 간호사, 보험사 등)
    public User(String id, String password, Role role) {
        this(id, password, role, null);
    }

    // 환자 전용 생성자
    public User(String id, String password, Role role, String patientCode) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.patientCode = patientCode;
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
}
