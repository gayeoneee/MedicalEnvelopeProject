package insuranceSystem.insuranceService;

import java.io.*;
import java.util.zip.*;

public class InsuranceRecordArchiver {

    public void archiveFinalEnvelope(String dir, String patientCode) throws Exception {
        System.out.println("📦 보험사 최종 전자봉투 생성 중...");

        // ✅ 보험사 Outbox 경로 지정 (P2025_001 폴더 생성)
        String insuranceOutboxDir = "src/data/insuranceOutbox/" + patientCode + "/";
        new File(insuranceOutboxDir).mkdirs();

        // ✅ 최종 전자봉투 경로 지정
        String zipFilePath = insuranceOutboxDir + "final_envelope_" + patientCode + ".zip";

        // ✅ 포함할 파일 목록
        String[] filesToInclude = {
            "aes_for_patient.key",
            "aes_for_insurance.key",
            "hash.txt",
            "record.enc",
            "sign_doctor.sig",
            "sign_nurse.sig",
            "sign_underwriter.sig",
            "sign_adjuster.sig"
        };

        // ✅ ZIP 생성 및 파일 추가
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

        System.out.println("✅ 최종 전자봉투 보관 완료! → " + zipFilePath);
    }


    private void addFile(ZipOutputStream zos, String filePath, String entryName) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            zos.putNextEntry(new ZipEntry(entryName));
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
        }
    }

    // (선택) 환자가 전송한 봉투를 별도로 저장하는 receive 메서드
    public void receiveEnvelopeFromPatient(String dir) {
        String patientCode = new File(dir).getName(); // P2025_001
        String zipFileName = "envelope_" + patientCode + ".zip"; // envelope_P2025_001.zip

        File zipFile = new File(dir, zipFileName);

        if (zipFile.exists()) {
            System.out.println("📥 환자로부터 전자봉투 수신 완료 ");
        } else {
            System.out.println("⚠️ 경고: 해당 경로에 전자봉투가 없습니다!");
        }
    }



}
