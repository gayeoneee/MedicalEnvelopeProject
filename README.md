# MedicalEnvelopeProject
[웹코드보안] 전자봉투를 생성하고 검증하는 프로토타입 시스템 개발

---

## 📁 프로젝트 구조 및 주요 파일 설명
```
MedicalEnvelopeProject/
│
├── common/                       # 공통 로그인 및 사용자 역할 관리
│   ├── Role.java                 // 사용자 역할(enum): DOCTOR, NURSE, PATIENT, INSURANCE
│   ├── User.java                 // 사용자 클래스: id, password, role, patientCode(병원시스템에서 환자일 경우만) 포함
│   ├── UserStore.java            // 사용자 계정 저장소 (하드코딩 기반 Map)
│   └── LoginService.java         // 로그인 검증 로직 수행 클래스
│
├── hospital-system/             # 병원 시스템 (의사/간호사/환자 전용)
│   └── HospitalSystemApp.java   // 로그인 후 역할에 따라 기능 분기되는 메인 실행 클래스
│
├── insurance-system/            # 보험사 시스템 (환자/보험사 전용)
│   └── InsuranceSystemApp.java  // 로그인 후 역할에 따라 기능 분기되는 메인 실행 클래스
│
├── data/                        # 파일 저장 디렉토리 (향후 사용 예정)
│   ├── records/                 // 병원 진료기록 zip 저장
│   ├── envelopes/               // 전자봉투 파일 저장
│   └── submissions/             // 보험청구 제출 데이터 저장
│
└── README.md                    // 프로젝트 설명 문서
```
- 최대한 이 브랜치의 프로젝트 파일 구조 지켜서 개발하기

---
## ✅ 현재 구현 완료 범위

| 구분              | 구현 내용 |
|-------------------|-----------|
| **공통 코드**     | 로그인 인증 로직 (`LoginService`), 사용자/역할 모델 (`User`, `Role`, `UserStore`) 구현 완료 |
| **병원 시스템**   | `HospitalSystemApp`에서 로그인 → 의사/간호사/환자 역할에 따라 기능 분기 처리 |
| **보험사 시스템** | `InsuranceSystemApp`에서 로그인 → 환자/보험사 역할에 따라 기능 분기 처리 |

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
- 보험사 시스템에서는 `PATIENT`, `INSURANCE`만 허용

---
## 👨‍⚕️ 병원 시스템 - `HospitalSystemApp.java`

```java
User user = login(Role.DOCTOR, Role.NURSE, Role.PATIENT);

if (user != null) {
    switch (user.getRole()) {
        case DOCTOR -> System.out.println("▶ 의사 기능 메뉴 진입");
        case NURSE -> System.out.println("▶ 간호사 기능 메뉴 진입");
        case PATIENT -> System.out.println("▶ 환자 기능 메뉴 진입");
    }
}
```

- 의사: 진료기록 zip 생성, 해시 생성, 전자서명 (1차)
- 간호사: 의사 서명 확인 후 전자서명 (2차), 전자봉투 전송
- 환자: 추후 수신 확인 기능 구현 예정

---

## 🏢 보험사 시스템 - `InsuranceSystemApp.java`

```java
User user = login(Role.PATIENT, Role.INSURANCE);

if (user != null) {
    switch (user.getRole()) {
        case PATIENT -> System.out.println("▶ 환자 보험청구 기능 진입");
        case INSURANCE -> System.out.println("▶ 보험사 검증 기능 진입");
    }
}
```
---
## 📌 기타 참고 사항

- 모든 사용자 계정은 `UserStore.java`에 하드코딩
- 각 시스템은 `LoginService`를 통해 권한 분기 처리
