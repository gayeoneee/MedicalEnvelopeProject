package common;

//리팩토링 3 : 광범위한 와일드 카드 사용 제거
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.security.PrivateKey;


public class KeyManager {
	// 리팩토링 4: 자동 close가 필요한 자원은 try-with-resources 구문으로 안전하게 처리
    public static PublicKey loadPublicKey(String fname) throws IOException, ClassNotFoundException {
    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname))) {
    	    return (PublicKey) ois.readObject();
    	}
    }

    public static PrivateKey loadPrivateKey(String fname) throws IOException, ClassNotFoundException {        
    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname))) {
    	    return (PrivateKey) ois.readObject();
    	}
    }
}



