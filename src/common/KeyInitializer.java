package common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collection;

/* 각 역할(Role)별 사용자의 공개키/개인키를 자동 생성하여 지정된 디렉토리에 저장하는 초기화 유틸 */
public class KeyInitializer {
	public static void main(String[] args) throws Exception {
		// UserStore에서 직접 사용자 목록을 가져옴
		Collection<User> allUsers = UserStore.getAllUsers();
		
		for (User user : allUsers) {
			
            String userId = user.getId();
            Role role = user.getRole();
            
            generateAndSaveKeyPair(userId, role);
        }

        System.out.println("✅ 모든 사용자 키가 역할별 디렉토리에 저장되었습니다.");
	}
	
	private static void generateAndSaveKeyPair(String userId, Role role) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024); 
        KeyPair keypair = keyPairGen.generateKeyPair();

        PublicKey publicKey = keypair.getPublic();
        PrivateKey privateKey = keypair.getPrivate();
        
        // "해당 role (소문자 변환 후)  + 사용자 id " 으로 디렉토리 생성
        String dirPath = "src/keys/" + role.name().toLowerCase() + "/" + userId;
        new File(dirPath).mkdirs(); // 디렉토리 생성
        
        String publicFilePath = dirPath + "/public.key";
        String privateFilePath = dirPath + "/private.key";
        
        // 리팩토링 6 : 자동 close가 필요한 자원은 try-with-resources 구문으로 안전하게 처리
        try (ObjectOutputStream pubOut = new ObjectOutputStream(new FileOutputStream(publicFilePath));
             ObjectOutputStream priOut = new ObjectOutputStream(new FileOutputStream(privateFilePath))) {

            pubOut.writeObject(publicKey);		// 공개키 저장
            priOut.writeObject(privateKey);		// 개인키 저장
        }

        System.out.println("🔑 [" + role.name() + "] 키 생성 완료 → " + dirPath);
	}
}
