package insuranceSystem.insuranceService;

import crypto.RSACryptoUtil;
import crypto.HashUtil;
import common.KeyManager;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.PublicKey;

public class SignatureVerifier {

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

	    PublicKey doctorPubKey = KeyManager.loadPublicKey("src/keys/doctor/doc1/public.key");
	    Path doctorSigPath = zipPath.resolveSibling("sign_doctor.sig");
	    byte[] doctorSig = Files.readAllBytes(doctorSigPath);
	    boolean doctorValid = RSACryptoUtil.verify(expectedHash, doctorSig, doctorPubKey);

	    PublicKey nursePubKey = KeyManager.loadPublicKey("src/keys/nurse/nurse1/public.key");
	    Path nurseSigPath = zipPath.resolveSibling("sign_nurse.sig");
	    byte[] nurseSig = Files.readAllBytes(nurseSigPath);
	    boolean nurseValid = RSACryptoUtil.verify(expectedHash, nurseSig, nursePubKey);

	    if (doctorValid && nurseValid) {
	        System.out.println("✅ 병원 서명 검증 성공!");
	        return true;
	    } else {
	        System.out.println("❌ 병원 서명 검증 실패!");
	        return false;
	    }
	}

}
