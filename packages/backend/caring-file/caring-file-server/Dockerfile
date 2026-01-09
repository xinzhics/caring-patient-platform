FROM openjdk:8-jre
MAINTAINER leizhi zhi.lei@caringnurse.cn

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

ENV PARAMS=""
ENTRYPOINT ["sh","-c","java -jar -Xmx512m -Djava.security.egd=file:/dev/./urandom -jar /app.jar $PARAMS"]
