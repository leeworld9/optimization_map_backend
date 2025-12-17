# 배달 플랫폼 최적 경로 계산 시스템

다수의 배달지를 대상으로 **최적 방문 순서를 계산하는 경로 계산 시스템**입니다.  
TSP(외판원 문제) 알고리즘과 지도 API를 결합하여  
실제 배달 서비스 환경에서 사용 가능한 구조를 검증하는 것을 목표로 개발했습니다.

---

## 🎯 프로젝트 목적
- 다중 경유지 기반 최적 경로 계산
- 지도 API 경유지 제한 환경 대응
- 모바일 클라이언트에서 즉시 활용 가능한 API 설계

---

## 🧠 주요 기능

- **경로 최적화**
  - TSP 기반 방문 순서 계산
  - Haversine 공식 + Distance Matrix 기반 거리 행렬 생성

- **외부 API 제약 대응**
  - 네이버 Directions API 경유지 15개 제한 대응
  - 경유지 분할 → 부분 경로 계산 → 결과 병합 로직 구현

- **백엔드 API**
  - Spring Boot 기반 REST API
  - 지도 시각화를 고려한 응답 구조 설계

---

## 🎥 데모 영상

> Flutter 클라이언트와 연동하여  
> 다중 경유지 기반 경로 계산 및 지도 시각화 시연
> 
[데모 영상 보기](https://github.com/leeworld9/optimization_map_frontend/demo/demo.mp4)

---

## 🛠 기술 스택

| 구분 | 기술 |
|----|----|
| Backend | Java, Spring Boot, JPA |
| DB | MariaDB |
| Infra | AWS EC2 / RDS |
| External API | Naver Directions API |
| Client | Flutter |

---

## ⚠️ 참고 사항
- 본 프로젝트는 **개념 검증(POC)** 목적의 구현입니다.
- 실제 서비스 적용 시 배차, 실시간 교통, 사용자 상태 관리 등의 추가 설계가 필요합니다.
