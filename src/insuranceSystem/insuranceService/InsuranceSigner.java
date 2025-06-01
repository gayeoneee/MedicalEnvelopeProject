package insuranceSystem.insuranceService;

import crypto.RSACryptoUtil;
import common.User;
import common.KeyManager;

import java.io.*;
import java.nio.file.*;
import java.security.PrivateKey;

public class InsuranceSigner {

    public void signAsUnderwriter(String dir, User underwriter) throws Exception {
        System.out.println("🖋️ 심사관 전자서명 중...");

        String hashPath = dir + "hash.txt";
        byte[] hashData = Files.readAllBytes(Paths.get(hashPath));
        PrivateKey privateKey = KeyManager.loadPrivateKey("src/keys/underwriter/" + underwriter.getId() + "/private.key");

        byte[] signature = RSACryptoUtil.sign(hashData, privateKey);
        Files.write(Paths.get(dir + "sign_underwriter.sig"), signature);

        System.out.println("✅ 심사관 서명 완료!");
    }

    public void signAsAdjuster(String dir, User adjuster) throws Exception {
        System.out.println("🖋️ 관리자 전자서명 중...");

        String hashPath = dir + "hash.txt";
        byte[] hashData = Files.readAllBytes(Paths.get(hashPath));
        PrivateKey privateKey = KeyManager.loadPrivateKey("src/keys/adjuster/" + adjuster.getId() + "/private.key");

        byte[] signature = RSACryptoUtil.sign(hashData, privateKey);
        Files.write(Paths.get(dir + "sign_adjuster.sig"), signature);

        System.out.println("✅ 관리자 서명 완료!");
    }
}
