FROM centos:7
MAINTAINER leizhi zhi.lei@caringnurse.cn

# set the environment variables
ENV LANG C.UTF-8

# install Java
RUN yum -y update
RUN yum -y remove java
RUN yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel
RUN yum install -y speex
RUN yum install -y speex-devel
RUN yum install -y git
RUN yum -y install gcc automake autoconf libtool make

RUN git clone http://gitlab.caringsaas.cn/shuai.yang/wechat-speex-declib.git && \
    cd wechat-speex-declib && \
    make && \
    cp ./bin/speex_decode /usr/local/bin/speex2wav

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

ENV PARAMS=""
ENTRYPOINT ["sh","-c","java -jar -Xmx512m -Djava.security.egd=file:/dev/./urandom -jar /app.jar $PARAMS"]
