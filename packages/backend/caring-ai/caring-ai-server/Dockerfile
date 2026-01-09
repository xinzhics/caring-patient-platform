FROM openjdk:8-jre
MAINTAINER leizhi zhi.lei@caringnurse.cn

# 设置环境变量
ENV LANG C.UTF-8

# 替换为阿里云镜像源
RUN sed -i 's|http://deb.debian.org|http://mirrors.aliyun.com|g' /etc/apt/sources.list

# 安装必要的依赖项
RUN apt-get update && apt-get install -y \
    ffmpeg \
    fontconfig \
    tzdata \
    && rm -rf /var/lib/apt/lists/*

# 添加字体文件
ADD fonts/alibabapuhuiti.ttf /usr/share/fonts/
ADD fonts/Alimama_ShuHeiTi.ttf /usr/share/fonts/
ADD fonts/siyuanheiti_Medium.otf /usr/share/fonts/
ADD fonts/siyuanheiti_Regular.otf /usr/share/fonts/
RUN fc-cache

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

# 复制 JAR 文件
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

# 设置启动命令
ENV FFMPEG_PATH="/usr/bin/ffmpeg"
ENV PARAMS=""
ENTRYPOINT ["sh","-c","java -jar -Xmx512m -Djava.security.egd=file:/dev/./urandom -jar /app.jar $PARAMS"]
