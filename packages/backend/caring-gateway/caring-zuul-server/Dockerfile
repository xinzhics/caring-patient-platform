FROM openjdk:8-jre
MAINTAINER caring 244387066@qq.com
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ARG JAR_FILE
COPY ${JAR_FILE} /app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
CMD ["--spring.profiles.active=docker"]
