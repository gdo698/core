# Java 17 이미지 기반
FROM openjdk:17-jdk-slim

# 작업 디렉토리 생성
WORKDIR /app

# JAR 파일 복사 (파일명 정확히 일치해야 함!)
COPY build/libs/erp-pos-system-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
