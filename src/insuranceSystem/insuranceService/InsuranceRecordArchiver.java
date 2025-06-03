package insuranceSystem.insuranceService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// 최종 전자봉투 생성 및 보험사 outbox에 저장
public class InsuranceRecordArchiver {
	// [8단계] 최종 전자봉투 생성
	// - record.enc, hash.txt, 서명들 포함한 final_envelope_*.zip 압축 및 보관
    public void archiveFinalEnvelope(String dir, String patientCode) throws Exception {
        System.out.println("📦 보험사 최종 전자봉투 생성 중...");

        
        // 최종 전자봉투를 저장할 파일
        String insuranceOutboxDir = "src/data/insuranceOutbox/" + patientCode + "/";
        new File(insuranceOutboxDir).mkdirs();

        // 최종 전자봉투 경로 지정
        String zipFilePath = insuranceOutboxDir + "final_envelope_" + patientCode + ".zip";

        // 포함할 파일 목록
        String[] filesToInclude = {
            "aes_for_patient.key",
            "aes_for_insurance.key",
            "hash.txt",
            "/record_" +  patientCode + ".enc",
            "sign_doctor.sig",
            "sign_nurse.sig",
            "sign_underwriter.sig",
            "sign_adjuster.sig"
        };

        // ZIP 생성 및 파일 추가
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (String relativePath : filesToInclude) {
                File file = new File(dir, relativePath);
                if (!file.exists()) {
                    System.out.println("⚠️ 포함되지 않음 (파일 없음): " + relativePath);
                    continue;
                }

                try (FileInputStream fis = new FileInputStream(file)) {
                    String entryName = new File(relativePath).getName();
                    zos.putNextEntry(new ZipEntry(entryName));

                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
        }

        System.out.println("📦 보험사 전용 최종 전자봉투를 생성하여 보관소에 안전하게 저장했습니다.");
    }

    // [0단계] 환자 → 전자봉투가 도착했는지 여부만 출력
    public void receiveEnvelopeFromPatient(String dir) {
        String patientCode = new File(dir).getName(); // P2025_001
        String zipFileName = "envelope_" + patientCode + ".zip"; // envelope_P2025_001.zip

        File zipFile = new File(dir, zipFileName);

        if (zipFile.exists()) {
            System.out.println("📥 제출된 진료 기록 전자봉투를 성공적으로 수신했습니다. 보험 심사를 기다려주세요. ");
        } else {
            System.out.println("⚠️ 제출된 전자봉투가 확인되지 않습니다. 병원 측 전송 상태를 다시 확인해주세요.");
        }
    }



}
