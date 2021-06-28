# 오늘한주

## 서비스 소개

> 술을 사랑하는 사람들이 모여 음주 라이프를 자유롭게 공유할 수 있는 모바일 앱 서비스

음주 라이프를 공유하는 SNS. 인기 있는 레시피를 확인하고, 본인만의 칵테일을 공유하는 익명 커뮤니티를 통해 풍부한 음주 라이프를 즐겨보세요.

## 프로젝트 기본 구조

### common ###
* 공통처리 모듈    
  (예) 문자열 변환등

### domain ###
* DB연동 모듈    
  domain, repository
  
### dto ###
* 도메인 변환 모듈    
   service, front에서 사용

### api ###
* 내부 API들. Controller등을 구현

### service ###
* 서비스 모듈    
   비즈니스 로직 처리