# 📌 AI 기반 주문 및 회원 관리 자동화 시스템 (2NE1) - 개인 기여 내역

## 🗣️ 프로젝트 소개

- **프로젝트 명:** AI 활용 비즈니스 프로젝트 (2NE1)
- **한 줄 소개:** 배달 플랫폼 서비스를 벤치마킹하여 주문, 결제, 배송 프로세스를 자동화한 Spring Boot 기반 모놀리식 시스템
- **개발 기간:** 2025년 2월 12일 ~ 2025년 2월 25일
- **원본 레포지토리:** [2NE1팀 깃허브 원본 레포지토리 링크](https://github.com/sparta-2NE1/delivery)
- **나의 역할:** **Backend Developer** (`회원`, `배송지`, `인증/인가` 담당)

<br>

## 🚀 나의 핵심 기여 (My Key Contributions)

프로젝트에서 **회원(User) 도메인과 보안(Security)** 영역을 전담하여 시스템의 안정성과 확장성을 확보했습니다.

* **보안 아키텍처 구축:** Spring Security 6.x와 JWT를 활용하여 인증/인가 시스템을 바닥부터 설계 및 구현했습니다.
* **토큰 기반 인증:** Access/Refresh Token 전략을 도입하고, 필터 체인을 커스터마이징하여 보안성을 강화했습니다.
* **회원 관리 시스템:** 회원가입, 정보 수정, 배송지 관리 등 핵심 User 기능을 개발하고 입력값 검증(Validation)을 적용했습니다.
* **동적 검색 구현:** QueryDSL을 도입하여 관리자 및 유저가 다양한 조건으로 회원을 검색할 수 있는 기능을 최적화했습니다.
* **데이터 관리:** Soft Delete(논리적 삭제)를 적용하여 데이터 복구 가능성을 열어두고 참조 무결성을 유지했습니다.

<br>

## 🛠️ 상세 구현 내용

### 1. Spring Security & JWT 인증 시스템 구축

* **인증/인가 처리:** Spring Security 6.x를 기반으로 `SecurityFilterChain`을 구성하여, URL별 접근 권한(Role)을 세밀하게 제어했습니다.
* **JWT 전략:**
    * 로그인 성공 시 **Access Token**(Header)과 **Refresh Token**(Cookie)을 동시 발급하는 이중 토큰 전략을 사용했습니다.
    * `Access Token` 만료 시, `Refresh Token`을 검증하여 토큰을 재발급하는 로직을 구현, 사용자 편의성과 보안성을 균형 있게 맞췄습니다.
* **Custom Filter:** 표준 필터 외에 `JwtAuthenticationFilter`, `JwtExceptionFilter` 등 커스텀 필터를 구현하여 토큰 유효성 검증 및 예외 처리를 전담시켰습니다.

### 2. 회원(User) 및 배송지 관리 기능

* **CRUD & Validation:** 회원 및 배송지 정보에 대한 CRUD API를 구현하고, `Spring Validation`(@Valid, @NotNull 등)을 적용하여 잘못된 데이터 유입을 사전에 차단했습니다.
* **배송지 관리:** 한 명의 회원이 여러 배송지를 가질 수 있도록 1:N 관계를 설계하고, 배송지 추가/수정/삭제 기능을 구현했습니다.

### 3. QueryDSL 기반 동적 검색 및 페이징

* **동적 쿼리:** `username`, `email`, `role` 등 다양한 조건으로 회원을 검색할 수 있도록 **QueryDSL**의 `BooleanBuilder`를 활용해 동적 쿼리를 작성했습니다.
* **성능 고려:** 대량의 회원 데이터 조회 시 성능 저하를 막기 위해 `Pageable`을 적용하여 효율적인 페이징 처리를 구현했습니다.

### 4. 데이터 일관성 및 공통 관심사 처리

* **Soft Delete:** 회원 탈퇴 시 DB에서 물리적으로 삭제하는 대신 `deleted_at` 타임스탬프를 찍는 **논리적 삭제** 방식을 적용하여 데이터 이력을 보존하고 관계 데이터의 정합성을 유지했습니다.
* **AOP Logging:** 회원 서비스의 주요 메서드 실행 전후에 로그를 남기는 **AOP(Aspect Oriented Programming)** 를 적용하여 트러블슈팅과 운영 모니터링 효율을 높였습니다.

<br>

## 💡 기술적 트러블슈팅 및 개선 경험 (담당)

프로젝트를 진행하며 마주친 문제를 해결하고 구조를 개선한 경험입니다.

| 문제 상황 (Challenge) | 해결 과정 (Solution) |
| :--- | :--- |
| **UserService의 과도한 책임 (SRP 위반)** | 초기에는 `UserService`가 회원 로직뿐만 아니라 **JWT 토큰 생성, 검증, 파싱** 로직까지 모두 처리했습니다.<br/>이로 인해 코드의 응집도가 낮아지고, `UserService`가 `JwtUtil` 등에 강하게 결합되어 테스트와 유지보수가 어려웠습니다. |
| **토큰 도메인 분리 (Refactoring)** | 1. **TokenService(RefreshTokenService) 분리:** 토큰 생성 및 관리 책임을 전담하는 별도 서비스 컴포넌트를 생성했습니다.<br/>2. **위임 구조 변경:** `UserService`는 회원 데이터만 관리하고, 인증 관련 작업은 `TokenService`에게 위임하도록 리팩토링했습니다.<br/>👉 결과적으로 클래스 간 결합도를 낮추고 단일 책임 원칙(SRP)을 준수하는 구조로 개선했습니다. |

<br>

## 💻 사용 기술 스택

(담당 도메인 및 프로젝트 전반에서 사용한 기술입니다.)

* **Backend:** Java 17, Spring Boot 3.x, Spring Security 6.x, Spring Data JPA, QueryDSL
* **Auth:** JWT (JSON Web Token), Custom Security Filter
* **Database:** PostgreSQL
* **DevOps & Tools:** Docker, Docker-Compose, Swagger, AOP

## ☁️ 인프라 설계서
![Image](https://github.com/user-attachments/assets/30111de9-b5b7-47f3-9a0d-b9e71399e04c)

<br>

## 📌 ERD

![Image](https://github.com/user-attachments/assets/f144479b-b3e2-47f8-8ca2-6c9f0beb9f0f)

<br>


## 🚩 로컬 실행 방법
```bash
# 1. 레포지토리 클론
git clone https://github.com/jjsh0208/delivery.git
cd delivery

# 2. 변경사항 커밋 및 푸시
git add .
git commit -m "커밋 메시지"
git push origin 브랜치명

# 3. CI/CD 파이프라인 실행 (예: GitHub Actions로 Docker 이미지 빌드 및 푸시)

# 4. 빌드된 Docker 이미지를 Docker Hub에 푸시
# 예: docker push [dockerhub-username]/delivery:latest

# 5. EC2 서버에 접속 후 Docker Hub에서 이미지 pull
docker pull [dockerhub-username]/delivery:latest

# 6. docker-compose를 사용해 환경변수 주입 및 컨테이너 실행
docker-compose up -d
```


<br>


## 🎯 팀원 역할분담
<table>
  <tr>
    <th>
      <a href="https://github.com/Leewon2" target="_blank">
        이원희&lt;팀장&gt;
      </a>
    </th>
    <th>
      <a href="https://github.com/dlchacha" target="_blank">
        이채연
      </a>
    </th>
    <th>
      <a href="https://github.com/leeseowoo" target="_blank">
        이서우
      </a>
    </th>
    <th>
      <a href="https://github.com/ckdrmsdl9999" target="_blank">
        윤창근
      </a>
    </th>
    <th>
      <a href="https://github.com/jjsh0208" target="_blank">
        전승현
      </a>
    </th>
  </tr>
  <tr>
    <td>
      <img src="https://github.com/Leewon2.png" width="150" alt="이원희 팀장">
    </td>
    <td>
      <img src="https://github.com/dlchacha.png" width="150" alt="이채연">
    </td>
    <td>
      <img src="https://github.com/leeseowoo.png" width="150" alt="이서우">
    </td>
    <td>
      <img src="https://github.com/ckdrmsdl9999.png" width="150" alt="윤창근">
    </td>
    <td>
      <img src="https://github.com/jjsh0208.png" width="150" alt="전승현">
    </td>
  </tr>
  <tr>

  <th>Payment <br> Card <br> CI/CD Pipeline  <!-- 원희 -->
  <th>Order <br> Review</th> <!-- 채연 -->
  <th>Gemini AI <br> Product</th> <!-- 서우 -->
  <th>Region <br> Store</th> <!-- 창근 -->
  <th>User<br> DeliveryAddress <br> Spring Security</th> <!-- 승현 -->
  </tr>
</table>


