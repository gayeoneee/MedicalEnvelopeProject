package insuranceSystem;

import common.*;
import crypto.*;

import java.io.*;
import java.nio.file.*;
import java.security.*;

public class InsuranceSystemApp {
	
	public static void main(String[] args) {
        System.out.println("💼 보험사 시스템");

        User user = login();
		
		if (user == null) {
            System.out.println("프로그램을 종료합니다.");
            return;
        }
		
		handleRoleBasedActions(user);
    }
	
	// 🔐 로그인 전용 메서드
	private static User login() {
        LoginService loginService = new LoginService();

        // 병원 시스템에서는 의사, 간호사, 환자만 로그인 허용
        return loginService.login(Role.PATIENT, Role.INSURANCE);
    }
	
	// 🧑‍💼 로그인된 사용자 역할에 따라 기능 분기
    private static void handleRoleBasedActions(User user) {
        switch (user.getRole()) {
            case PATIENT:
                System.out.println("환자 기능 실행 중...");
                // TODO: 보험청구용 전자봉투 제출
                submitEncryptedZipToInsurance();
                break;
            case INSURANCE:
                System.out.println("보험사 기능 실행 중...");
                // TODO: 서명 검증 및 진료기록 열람
                verifyAndReadMedicalRecord();
                break;
            default:
                System.out.println("지원하지 않는 역할입니다.");
        }
    }
    
    private static void submitEncryptedZipToInsurance() {
        System.out.println("🚀 환자가 보험사에 제출할 전자봉투를 준비 중입니다…");
        try {
            // 1) 복호화된 zip 읽기
            byte[] zipData = Files.readAllBytes(Paths.get("test.zip"));

            // 2) 새로운 AES키 생성 및 zip 암호화
            byte[] newAESKey = AESCryptoUtil.generateAESKey();
            byte[] encryptedZip = AESCryptoUtil.encrypt(zipData, newAESKey);

            // 3) AES키를 보험사 공개키로 암호화
            PublicKey insurancePublicKey = KeyManager.loadPublicKey("insurance_public.key");
            byte[] encryptedAESKey = RSACryptoUtil.encrypt(newAESKey, insurancePublicKey);

            // 4) 해시 생성
            byte[] hash = HashUtil.generateSHA256(zipData);

            // 5) 보험사 제출용 전자봉투 저장 (의사 서명 제거됨!)
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("insurance.bin"));
            oos.writeObject(encryptedZip);
            oos.writeObject(encryptedAESKey);
            oos.writeObject(hash);
            oos.close();

            System.out.println("✅ 보험사 제출용 전자봉투 생성 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🚀 보험사: 환자가 제출한 전자봉투 열람 및 검증
    private static void verifyAndReadMedicalRecord() {
        System.out.println("🚀 환자가 제출한 전자봉투를 검증하고 진료기록을 열람합니다…");
        try {
            // 1) 전자봉투 열기
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("insurance.bin"));
            byte[] encryptedZip = (byte[]) ois.readObject();
            byte[] encryptedAESKey = (byte[]) ois.readObject();
            byte[] hash = (byte[]) ois.readObject();
            ois.close();

            // 2) 보험사 개인키로 AES키 복호화
            PrivateKey insurancePrivateKey = KeyManager.loadPrivateKey("insurance_private.key");
            byte[] aesKey = RSACryptoUtil.decrypt(encryptedAESKey, insurancePrivateKey);

            // 3) zip 복호화
            byte[] zipData = AESCryptoUtil.decrypt(encryptedZip, aesKey);

            // 4) 해시 검증
            boolean hashValid = HashUtil.verifySHA256(zipData, hash);

            // 5) 최종 결과
            if (hashValid) {
                System.out.println("✅ 검증 완료: 문서 위조 없음!");

                // 복호화된 zip 저장
                Files.write(Paths.get("test.zip"), zipData);
                System.out.println("복호화된 zip 저장: test.zip");

                // 🚀 zip 안의 txt 파일 내용 출력
                try (ByteArrayInputStream bais = new ByteArrayInputStream(zipData);
                     java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(bais)) {

                    java.util.zip.ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().endsWith(".txt")) {
                            System.out.println("📄 zip 안의 txt 파일 (" + entry.getName() + ") 내용:");
                            BufferedReader br = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
                            String line;
                            while ((line = br.readLine()) != null) {
                                System.out.println(line);
                            }
                            break;
                        }
                    }
                }
            } else {
                System.out.println("❌ 검증 실패: 문서 위조 의심!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
}
