# MedicalEnvelopeProject
[웹코드보안] 전자봉투를 생성하고 검증하는 프로토타입 시스템 개발
---
## 👨‍💻 역할 분담

| 팀원     | 담당 기능                                |
|----------|-------------------------------------------|
| 이가연   | 병원 시스템 + 공통 로그인 |
| 강민주   | 보험사 시스템  |

---
## 📁 프로젝트 구조 및 주요 파일 설명
- `src/common/` : 공통 유틸리티 및 키 관리 클래스
- `src/crypto/` : 암호화 관련 유틸리티
- `src/hospitalSystem/` : 병원 시스템 (의사, 간호사)
- `src/patientSystem/` : 환자 기능
- `src/insuranceSystem/` : 보험사 시스템 (심사관, 보상담당자)
- `src/data/` : 실행 중 생성되는 파일 저장 디렉터리

---
## 🔐 로그인 로직 설명

```java
public User login(Role... allowedRoles) {
    // 1. 사용자 입력: 아이디, 비밀번호
    // 2. UserStore 에서 id로 사용자 조회
    // 3. 비밀번호 일치 여부 확인
    // 4. 역할이 allowedRoles 중 하나일 경우 로그인 성공
}
```

- 병원 시스템에서는 `DOCTOR`, `NURSE`, `PATIENT`만 허용
- 보험사 시스템에서는 `PATIENT`, `UNDERWRITER`, `ADJUSTER`만 허용

---
## 병원 시스템

> 👩‍⚕️ 병원 측 기능 플로우

### ▶ 의사 수행 단계
| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [1단계] | 진료 기록 생성 (`diagnosis.txt`, `prescription.txt`, `timestamp.txt`, `patientCode.txt`) | `HospitalRecordGenerator.generateMedicalRecordByCode(User, String)` |
| [2단계] | 진료 기록 압축 및 해시 생성 → `record_*.zip`, `hash.txt` | `RecordCompressor.compressAndHash(String)` |
| [3단계] | 해시 서명 → `sign_doctor.sig`, `sign_doctor_id.txt` | `SignatureCreator.signHash(User, String)` |
| [4단계] | 진료 기록 AES 암호화 + RSA 키로 암호화 키 분배 | `EncryptionProcessor.encryptRecordWithMultiKeys(String)` |
| [5단계] | 전자봉투 생성 (의사 서명 포함) → `envelope_*.zip`  (병원 최초 전자봉투) | `EnvelopeBuilder.createEnvelope(User, String)` |

#### ▶ 간호사 수행 단계
| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [6단계] | 간호사 서명 → `sign_nurse.sig`, `sign_nurse_id.txt` | `SignatureCreator.signHash(User, String)` |
| [7단계] | 전자봉투 최종 생성 (간호사 서명 포함) → `envelope_*.zip`  (최종 봉투 완성)| `EnvelopeBuilder.createEnvelope(User, String)` |


> 🧑‍🦰 환자 측 기능 플로우

#### ▶ 환자 수행 단계
| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [0단계] | 진료 요청 제출 → `request.txt` (심사관 코드 포함) | `RecordRequestSubmitter.submitRequest(String, String)` |
| [1단계] | 병원에서 전자봉투 수신 및 압축 해제 | `PatientEnvelopeReceiver.receiveEnvelope(String)` |
| [2단계] | AES 키 복호화 및 진료 기록 복호화 (`record.enc → record_decrypted.zip`) | `EnvelopeDecryptor.decryptEnvelope(String, String)` |
| [3단계] | 복호화된 ZIP 압축 해제 → `diagnosis.txt`, `prescription.txt` 복원 | `DecryptedZipExtractor.extractDecryptedRecord(String)` |
| [4단계] | 진단서/처방전 열람 및 서명 검증 결과 출력 | `DecryptedRecordViewer.viewDecryptedRecord(String)` |
| [5단계] | 전자봉투를 보험사로 전송 → `insuranceInbox/` 복사 | `EnvelopeForwarder.forwardEnvelope(String)` |

---
## 보험사 시스템

> 🧑‍🦰 환자 측 기능 플로우

#### ▶ 환자 수행 단계
| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [0단계] | 제출한 전자봉투 확인 로그 출력 | `InsuranceRecordArchiver.receiveEnvelopeFromPatient()` |


> 🏢 보험사 측 기능 플로우

### ▶ 심사관(Underwriter) 역할

| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [1단계] | 요청된 심사관 ID 확인 및 권한 검증 | `InsuranceSystemApp` 내부 |
| [2단계] | 전자봉투 수신 및 압축 해제 | `InsuranceRecordReceiver.receiveEnvelope()` |
| [3단계] | 봉투 복호화 | `InsuranceRecordDecryptor.decryptEnvelope()` |
| [4단계] | 병원 서명 검증 | `SignatureVerifier.verifySignatures()` |
| [5단계] | 진단서/처방전 열람 | `InsuranceRecordViewer.viewDecryptedRecord()` |
| [6단계] | 심사관 서명 추가 (`sign_underwriter.sig`) | `InsuranceSigner.signAsUnderwriter()` |

### ▶ 보상담당자(Adjuster) 역할

| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [7단계] | 보상담당자 서명 추가 (`sign_adjuster.sig`) | `InsuranceSigner.signAsAdjuster()` |
| [8단계] | 최종 전자봉투 보관 → `final_envelope.zip` | `InsuranceRecordArchiver.archiveFinalEnvelope()` |


---
## 📁 실행 중 생성되는 데이터 파일 구조

```
src/
└── data/
    ├── records/                        ← 병원에서 생성하는 원본 데이터
    │   └── P2025_001/
    │       ├── diagnosis.txt              ← [병원 1단계] 진단서
    │       ├── prescription.txt           ← [병원 1단계] 처방전
    │       ├── timestamp.txt              ← [병원 1단계] 진단 시각
    │       ├── patientCode.txt            ← [병원 1단계] 환자 코드 기록
    │       ├── record_P2025_001.zip       ← [병원 2단계] 압축된 기록
    │       ├── hash.txt                   ← [병원 2단계] SHA-256 해시
    │       ├── sign_doctor.sig            ← [병원 3단계] 의사 서명
    │       ├── sign_doctor_id.txt         ← [병원 3단계] 의사 ID
    │       ├── record_P2025_001.enc       ← [병원 4단계] 암호화된 진료 기록
    │       ├── aes_for_patient.key        ← [병원 4단계] 환자용 암호화 키
    │       ├── aes_for_insurance.key      ← [병원 4단계] 보험사용 암호화 키
    │       ├── sign_nurse.sig             ← [병원 6단계] 간호사 서명
    │       ├── sign_nurse_id.txt          ← [병원 6단계] 간호사 ID
    │       └── envelope_P2025_001.zip     ← [병원 5단계 or 7단계] 최종 전자봉투 (병원 기준)
    │
    ├── requests/                      ← 환자가 진료 요청한 내역 저장
    │   └── P2025_001/
    │       └── request.txt              ← [환자 0단계] 희망 심사관 코드 저장
    │
    ├── envelopes/                     ← 환자 수신 및 복호화 결과
    │   └── P2025_001/
    │       ├── record_P2025_001.enc       ← [환자 1단계] 수신된 암호문
    │       ├── aes_for_patient.key        ← [환자 1단계] 환자 키
    │       ├── aes_for_insurance.key      ← [환자 1단계] 보험사 키
    │       ├── hash.txt                   ← [환자 1단계] 해시
    │       ├── sign_doctor.sig            ← [환자 1단계] 의사 서명
    │       ├── sign_doctor_id.txt         ← [환자 1단계] 의사 ID
    │       ├── sign_nurse.sig             ← [환자 1단계] 간호사 서명
    │       ├── sign_nurse_id.txt          ← [환자 1단계] 간호사 ID
    │       ├── record_decrypted.zip       ← [환자 2단계] 복호화된 ZIP
    │       ├── diagnosis.txt              ← [환자 3단계] 진단서 복원
    │       └── prescription.txt           ← [환자 3단계] 처방전 복원
    │
    ├── insuranceInbox/               ← 보험사 수신 데이터
    │   └── P2025_001/
    │       ├── envelope_P2025_001.zip     ← [환자 5단계] 전송된 전자봉투
    │       ├── record_P2025_001.enc       ← [보험사 2단계] 수신 암호문
    │       ├── aes_for_patient.key        ← [보험사 2단계]
    │       ├── aes_for_insurance.key      ← [보험사 2단계]
    │       ├── hash.txt                   ← [보험사 2단계]
    │       ├── sign_doctor.sig            ← [보험사 2단계]
    │       ├── sign_nurse.sig             ← [보험사 2단계]
    │       ├── record_decrypted.zip       ← [보험사 3단계] 복호화 결과
    │       ├── InsuranceDocuments/
    │       │   ├── diagnosis.txt          ← [보험사 5단계] 진단서 열람
    │       │   └── prescription.txt       ← [보험사 5단계] 처방전 열람
    │       ├── sign_underwriter.sig       ← [보험사 6단계] 심사관 서명
    │       └── sign_adjuster.sig          ← [보험사 7단계] 관리자 서명
    │
    └── insuranceOutbox/             ← 보험사 최종 전자봉투 보관소
        └── P2025_001/
            └── final_envelope_P2025_001.zip ← [보험사 8단계] 최종 패키지
```

### 📎 파일 명세 요약
| 파일명 | 설명 |
|--------|------|
| `diagnosis.txt`, `prescription.txt` | 진료기록 (의사가 작성) |
| `record_*.zip` | 진단서/처방전/타임스탬프 압축 |
| `hash.txt` | SHA-256 해시 |
| `sign_*.sig` | 전자서명 (의사, 간호사, 심사관, 관리자) |
| `record.enc` | AES 암호화된 진료기록 |
| `aes_for_patient.key` | 환자 공개키로 암호화된 AES 키 |
| `aes_for_insurance.key` | 보험사 공개키로 암호화된 AES 키 |
| `envelope_*.zip` | 암호문 + 키 + 서명이 포함된 전자봉투 |
| `final_envelope_*.zip` | 보험사 최종 보관용 전자봉투 |

---
## 🛠 기술 요소

- 🔐 **RSA 비대칭키 암호화**: 공개키/개인키 기반 서명 및 키 전송
- 🔒 **AES 대칭키 암호화**: 진료기록 데이터 암호화
- 🧾 **전자서명**: 해시 + 개인키 서명 → 진본 검증
- 📁 **ZIP 압축/해제**: 진료기록의 보안 패키징 및 전송
