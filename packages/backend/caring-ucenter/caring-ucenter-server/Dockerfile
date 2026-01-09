FROM openjdk:8-jre
MAINTAINER leizhi zhi.lei@caringnurse.cn

# set the environment variables
ENV LANG C.UTF-8

# 安装中文黑体字体
ADD fonts/simhei.ttf /usr/share/fonts/
ADD fonts/Source_Han_Sans_Old_Style.otf /usr/share/fonts/
ADD fonts/Alimama_ShuHeiTi.ttf /usr/share/fonts/
ADD fonts/NotoSansCJK_Black_7.otf /usr/share/fonts/
RUN fc-cache

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

ENV PARAMS=""
ENTRYPOINT ["sh","-c","java -jar -Xmx512m -Djava.security.egd=file:/dev/./urandom -jar /app.jar $PARAMS"]
