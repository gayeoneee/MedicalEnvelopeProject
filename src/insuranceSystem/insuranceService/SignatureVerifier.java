package insuranceSystem.insuranceService;

import crypto.HashUtil;
import crypto.RSACryptoUtil;
import common.KeyManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.PublicKey;

public class SignatureVerifier {
	// [4단계] 병원 서명 검증
	// - hash.txt와 record_decrypted.zip 비교
	// - 병원 서명(의사, 간호사) 검증
	public boolean verifySignatures(String recordZipPath) throws Exception {
	    System.out.println("🔍 병원 서명 검증 시작...");

	    Path zipPath = Paths.get(recordZipPath);
	    Path hashPath = zipPath.resolveSibling("hash.txt");
	    byte[] expectedHash = Files.readAllBytes(hashPath);

	    byte[] recordData = Files.readAllBytes(zipPath);
	    byte[] actualHash = HashUtil.generateSHA256(recordData);

	    if (!MessageDigest.isEqual(expectedHash, actualHash)) {
	        System.out.println("⚠️ 해시값 불일치! 위조 가능성 있음");
	        return false;
	    }
	    
        System.out.println("\n🧾 전자서명 진본 여부 검증 결과");
        System.out.println("-----------------------------------------------");
	    
        // 의사 서명 검증
	    PublicKey doctorPubKey = KeyManager.loadPublicKey("src/keys/doctor/doc1/public.key");
	    Path doctorSigPath = zipPath.resolveSibling("sign_doctor.sig");
	    byte[] doctorSig = Files.readAllBytes(doctorSigPath);
	    boolean doctorValid = RSACryptoUtil.verify(expectedHash, doctorSig, doctorPubKey);
	    
	    
        // 간호사 서명 검증
	    PublicKey nursePubKey = KeyManager.loadPublicKey("src/keys/nurse/nurse1/public.key");
	    Path nurseSigPath = zipPath.resolveSibling("sign_nurse.sig");
	    byte[] nurseSig = Files.readAllBytes(nurseSigPath);
	    boolean nurseValid = RSACryptoUtil.verify(expectedHash, nurseSig, nursePubKey);
	    
        // 출력 메시지
        String docMessage = doctorValid ? "✅ DOCTOR 서명 (by doc1): 진본 확인됨" : "❌ DOCTOR 서명 (by doc1): 위조 또는 손상된 서명";
        String nurseMessage = nurseValid ? "✅ NURSE 서명 (by nurse1): 진본 확인됨" : "❌ NURSE 서명 (by nurse1): 위조 또는 손상된 서명";

        System.out.println(docMessage);
        System.out.println(nurseMessage);
        System.out.println("-----------------------------------------------\n");
        
        if (doctorValid && nurseValid) {
            return true;
        } 
        else {
            return false;
        }
        
	}

}
