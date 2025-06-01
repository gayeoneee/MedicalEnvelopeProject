package insuranceSystem.insuranceService;

import crypto.RSACryptoUtil;
import common.User;
import common.KeyManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;

public class InsuranceSigner {
	// [6단계] 심사관 전자서명
	// - 해시(hash.txt)를 심사관 개인키로 서명 → sign_underwriter.sig
    public void signAsUnderwriter(String dir, User underwriter) throws Exception {
        System.out.println("🖋️ 심사관 전자서명 중...");
        String hashPath = dir + "hash.txt";
        byte[] hashData = Files.readAllBytes(Paths.get(hashPath));
        PrivateKey privateKey = KeyManager.loadPrivateKey("src/keys/underwriter/" + underwriter.getId() + "/private.key");

        byte[] signature = RSACryptoUtil.sign(hashData, privateKey);
        Files.write(Paths.get(dir + "sign_underwriter.sig"), signature);

        System.out.println("🖋️ 진료기록에 대한 심사관 서명을 완료했습니다. 보험 심사가 정상적으로 마무리되었습니다.\n");
        System.out.println("-----------------------------------------------\n");
    }
    
	 // [7단계] 보상담당자 전자서명
	 // - 해시(hash.txt)를 보상담당자 개인키로 서명 → sign_adjuster.sig
    public void signAsAdjuster(String dir, User adjuster) throws Exception {
        System.out.println("🖋️ 관리자 전자서명 중...");

        String hashPath = dir + "hash.txt";
        byte[] hashData = Files.readAllBytes(Paths.get(hashPath));
        PrivateKey privateKey = KeyManager.loadPrivateKey("src/keys/adjuster/" + adjuster.getId() + "/private.key");

        byte[] signature = RSACryptoUtil.sign(hashData, privateKey);
        Files.write(Paths.get(dir + "sign_adjuster.sig"), signature);

        System.out.println("🖋️ 보상담당자의 전자서명을 완료했습니다. 보험금 청구가 최종 승인되었습니다.");
        System.out.println("-----------------------------------------------\n");
    }
}
