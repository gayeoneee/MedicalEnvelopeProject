# MedicalEnvelopeProject
[웹코드보안] 전자봉투를 생성하고 검증하는 프로토타입 시스템 개발

### 병원 시스템 + 공통 로그인
- 전자봉투 기반 진료 정보 처리 시스템의 전체 흐름을 아래와 같이 정리합니다.
- 의사·간호사·환자 각각의 역할에 따라 수행되는 단계별 기능과 관련 클래스/메서드를 명시하였습니다.

  
---
## 👩‍⚕️ 병원 측 기능 플로우
> 사용 주체: `의사`, `간호사`

### ▶ 의사 수행 단계
| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [1단계] | 진료 기록 생성 (`diagnosis.txt`, `prescription.txt`, `timestamp.txt`, `patientCode.txt`) | `HospitalRecordGenerator.generateMedicalRecordByCode(User, String)` |
| [2단계] | 진료 기록 압축 및 해시 생성 → `record_*.zip`, `hash.txt` | `RecordCompressor.compressAndHash(String)` |
| [3단계] | 해시 서명 → `sign_doctor.sig`, `sign_doctor_id.txt` | `SignatureCreator.signHash(User, String)` |
| [4단계] | 진료 기록 AES 암호화 + RSA 키로 암호화 키 분배 | `EncryptionProcessor.encryptRecordWithMultiKeys(String)` |
| [5단계] | 전자봉투 생성 (의사 서명 포함) → `envelope_*.zip`  (병원 최초 전자봉투) | `EnvelopeBuilder.createEnvelope(User, String)` |

### ▶ 간호사 수행 단계
| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [6단계] | 간호사 서명 → `sign_nurse.sig`, `sign_nurse_id.txt` | `SignatureCreator.signHash(User, String)` |
| [7단계] | 전자봉투 최종 생성 (간호사 서명 포함) → `envelope_*.zip`  (최종 봉투 완성)| `EnvelopeBuilder.createEnvelope(User, String)` |

---
## 🧑‍🦰 환자 측 기능 플로우

> 사용 주체: `환자`


| 단계 | 기능 설명 | 클래스 / 메서드 |
|------|------------|------------------|
| [0단계] | 진료 요청 제출 → `request.txt` (심사관 코드 포함) | `RecordRequestSubmitter.submitRequest(String, String)` |
| [1단계] | 병원에서 전자봉투 수신 및 압축 해제 | `PatientEnvelopeReceiver.receiveEnvelope(String)` |
| [2단계] | AES 키 복호화 및 진료 기록 복호화 (`record.enc → record_decrypted.zip`) | `EnvelopeDecryptor.decryptEnvelope(String, String)` |
| [3단계] | 복호화된 ZIP 압축 해제 → `diagnosis.txt`, `prescription.txt` 복원 | `DecryptedZipExtractor.extractDecryptedRecord(String)` |
| [4단계] | 진단서/처방전 열람 및 서명 검증 결과 출력 | `DecryptedRecordViewer.viewDecryptedRecord(String)` |
| [5단계] | 전자봉투를 보험사로 전송 → `insuranceInbox/` 복사 | `EnvelopeForwarder.forwardEnvelope(String)` |

---
## 📁 실행 중 생성되는 주요 파일 구조

```
src/
└── data/
├── records/
│ └── P2025_001/
│ ├── diagnosis.txt
│ ├── prescription.txt
│ ├── timestamp.txt
│ ├── patientCode.txt
│ ├── record_P2025_001.zip
│ ├── hash.txt
│ ├── sign_doctor.sig
│ ├── sign_doctor_id.txt
│ ├── record_P2025_001.enc
│ ├── aes_for_patient.key
│ ├── aes_for_insurance.key
│ ├── sign_nurse.sig
│ ├── sign_nurse_id.txt
│ └── envelope_P2025_001.zip
│
├── envelopes/
│ └── P2025_001/
│ ├── record_P2025_001.enc
│ ├── aes_for_patient.key
│ ├── aes_for_insurance.key
│ ├── hash.txt
│ ├── sign_doctor.sig
│ ├── sign_doctor_id.txt
│ ├── sign_nurse.sig
│ ├── sign_nurse_id.txt
│ ├── record_decrypted.zip
│ ├── diagnosis.txt
│ └── prescription.txt
│
├── requests/
│ └── P2025_001/
│ └── request.txt
│
└── insuranceInbox/
└── P2025_001/
└── envelope_P2025_001.zip
```

### 📎 파일 명세 요약
| 파일명 | 설명 |
|--------|------|
| `diagnosis.txt` | 진단서 내용 (의사가 작성) |
| `prescription.txt` | 처방전 내용 (의사가 작성) |
| `record_*.zip` | 진단서/처방전/타임스탬프를 압축한 zip |
| `hash.txt` | 압축 파일에 대한 SHA-256 해시 |
| `sign_*.sig` | 해시 파일에 대한 전자서명 (의사/간호사) |
| `record.enc` | AES로 암호화된 진료 기록 |
| `aes_for_patient.key` | 환자 공개키로 암호화된 AES 키 |
| `aes_for_insurance.key` | 보험사 공개키로 암호화된 AES 키 |
| `envelope_*.zip` | 전자봉투: 암호문 + 키 + 서명 포함된 압축 파일 |
| `request.txt` | 진료 요청 및 희망 심사관 정보 |
| `record_decrypted.zip` | 환자가 복호화한 진료 기록 압축 파일 |
| `insuranceInbox/` | 보험사로 전송된 전자봉투 보관 디렉토리 

---
## 🛠 기술 요소

- 🔐 **RSA 비대칭키 암호화**: 공개키/개인키 기반 서명 및 키 전송
- 🔒 **AES 대칭키 암호화**: 진료기록 데이터 암호화
- 🧾 **전자서명**: 해시 + 개인키 서명 → 진본 검증
- 📁 **ZIP 압축/해제**: 진료기록의 보안 패키징 및 전송
