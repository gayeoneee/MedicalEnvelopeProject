package hospitalSystem.hospitalService;

import common.KeyManager;
import common.Role;
import common.User;
import crypto.RSACryptoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;

public class SignatureCreator {
	// 전자서명 생성 - [3단계] 의사 전자서명 생성/ [6단계] 간호사 전자서명 생성 
	public static void signHash(User user, String patientCode) throws Exception {
		
		// 1. 권한 확인 (의사 또는 간호사만 가능)
        if (user.getRole() != Role.DOCTOR && user.getRole() != Role.NURSE) {
            System.out.println("서명 권한이 없습니다.");
            return;
        }
        
        // 2. 환자 기록 디렉토리 확인
        String baseDir = "src/data/records/" + patientCode;
        
        File hashFile = new File(baseDir + "/hash.txt");
        if (!hashFile.exists()) {
            System.out.println("hash.txt 파일이 없습니다.");
            return;
        }
        
        // 현재 로그인되어 있는 사용자(의사 or 간호사) 의 개인키 로딩
        String privateKeyPath = "src/keys/" + user.getRole().name().toLowerCase() + "/" + user.getId() + "/private.key";
        PrivateKey privateKey = KeyManager.loadPrivateKey(privateKeyPath);
        
        // hash.txt 로딩 및 서명
        byte[] hashBytes = Files.readAllBytes(hashFile.toPath());
        byte[] signature = RSACryptoUtil.sign(hashBytes, privateKey);
        
        // 서명 파일 저장
        String sigFileName = (user.getRole() == Role.DOCTOR) ? "sign_doctor.sig" : "sign_nurse.sig";
        
        try (FileOutputStream fos = new FileOutputStream(baseDir + "/" + sigFileName)) {
            fos.write(signature);
        }
        
        // 서명자 ID 파일 생성 
        String idFileName = (user.getRole() == Role.DOCTOR) ? "sign_doctor_id.txt" : "sign_nurse_id.txt";
        Files.writeString(Path.of(baseDir, idFileName), user.getId());

        String message = (user.getRole() == Role.DOCTOR) ? "🖋️ 담당 의사의 전자서명이 완료되었습니다.\n" : "🖋️ 담당 간호사의 전자서명이 완료되었습니다.\n";
        System.out.println(message);
	}
}
