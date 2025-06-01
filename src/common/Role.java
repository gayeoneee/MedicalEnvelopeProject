package common;

// 리펙토링 7 : 열거형 구조 개선
public enum Role {
    DOCTOR(1),
    NURSE(2),
    PATIENT(3),
    UNDERWRITER(4),
    ADJUSTER(5);

    private final int level;
    
    Role(int level) { 
    	this.level = level; 
    }
    
    public int getLevel() { 
    	return level; 
    }
}