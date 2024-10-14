# Team
<h3>동양미래대학교 컴퓨터 소프트웨어학과 2024년 졸업작품 - NOMAD</h3>

<br>

# 프로젝트 기간
<h3>2024/04 ~ 2024/11/22</h3>

<br>

# 📋프로젝트 주제 및 기능
주제 : AI와 이웃이 함께하는 실시간 Q&A지식 커뮤니티 플랫폼

<br>

# 서비스 흐름도
![image01](https://github.com/user-attachments/assets/c4a05ccf-ce99-4907-a512-8738a61ac543)

<br>

# 👨‍👨‍👧‍👦 팀원 구성

| 이름 | 역할 | GitHub |
| --- | --- | --- |
| 👦 이영훈 | FrontEnd | https://github.com/kr-younghoon |
| 👦 김경빈 | FrontEnd | https://github.com/kyeongb-bin |
| 👦 주성준 | BackEnd | https://github.com/rdyjun |
| 👦 백승민 | BackEnd | https://github.com/alpin87 |
| 👦 김우빈 | BackEnd | https://github.com/KROOKIMWOOBIN |

<br>

# 🧩 역할 분담
| 이름 | 역할
| --- | --- |
| 👦 이영훈 | FrontEnd - UI/UX 설계 및 미들웨어 구축 |
| 👦 김경빈 | FrontEnd - UI 설계 및 구현 |
| 👦 주성준 | BackEnd - Security 설정, AI 답변 구현, Rest API 개발 |
| 👦 백승민 | BackEnd - 인프라 구축, 채팅 개발, Rest API 개발 |
| 👦 김우빈 | BackEnd - Rest API 개발 |

<br>

## ⚙️ 기술 스택
-  Server : AWS EC2
-  VM : Docker
-  DataBase : MySql, Redis
-  WS/WAS : Nginx, Tomcat
-  OCR : AWS S3, CloudFront
-  아이디어 회의 : Discord, Notion
-  버전 관리 : Git, GitHub


## 💻 개발환경
<h3> FrontEnd </h3>

-  언어: JavaScript
-  Framework : Next.js
-  상태 관리: Zustand, React-Query
-  Editor 라이브러리 : Quill

<h3> BackEnd </h3>

-  Version: Java 17
-  Framework: Spring Boot
-  ORM: Spring Data JPA
-  Security: Spring Security, JWT
-  실시간 통신: WebSocket, STOMP

<br>

# 🔧프로젝트 아키텍쳐
![danum 아키텍쳐](https://github.com/user-attachments/assets/62665c36-df87-45b2-b516-af820a33e40d)

## 📌 주요 기능
-  회원 기능
   -  회원 가입
   -  로그인 / 로그아웃
   -  회원 정보 수정
   -  프로필 사진

-  질문/마을 게시판
   -  게시글 생성
   -  게시글 삭제
   -  게시글 수정
   -  게시글 조회
 
-  질문/마을 게시글 댓글
   -  댓글 생성
   -  댓글 삭제
   -  댓글 수정
   -  댓글 조회
   -  댓글 채택
   -  댓글 채택 취소
 
-  AI 답변 
   -  사용자의 위치정보를 통한 맞춤형 대답 생성
   -  답변을 사용자에게 전달
   -  생성된 AI 답변 종료

-  채팅
   -  1:1 채팅방 생성
   -  채팅방 삭제
   -  채팅방 목록 조회
   -  채팅 최근 기록 조회
   -  특정 채팅방 정보 조회
   -  채팅방 입장 및 이전 메시지 불러오기


# 테이블 설계

![danum-erd](https://github.com/user-attachments/assets/57da7a21-9e55-4279-8a56-e1d88a595c4d)

