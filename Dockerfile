# ===== 1) Build stage: gradlew 로 Spring Boot JAR 빌드 =====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle Wrapper & 설정 먼저 복사해서 캐시 극대화
COPY gradlew gradlew
COPY gradle gradle
RUN chmod +x gradlew
COPY build.gradle settings.gradle ./
# gradle.properties 있으면 같이 복사
# COPY gradle.properties* ./

# 의존성만 먼저 받아 캐시
RUN ./gradlew dependencies -x test --no-daemon || true

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew clean bootJar -x test --no-daemon

# ===== 2) Run stage: 경량 JRE 로 실행 =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# 실행 옵션 / 포트
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"
ENV SERVER_PORT=8080
EXPOSE 8080

# 빌드 산출물 복사
COPY --from=build /app/build/libs/*.jar /app/app.jar

# 도커 실행 명령
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
