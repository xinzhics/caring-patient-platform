FROM ubuntu:20.04
MAINTAINER leizhi zhi.lei@caringnurse.cn

# support multiarch: i386 architecture
# install Java
# install essential tools
# install Qt
ARG JDK_VERSION=8
RUN dpkg --add-architecture i386 && \
    apt-get update && \
    apt-get dist-upgrade -y && \
    apt-get install -y --no-install-recommends libncurses5:i386 libc6:i386 libstdc++6:i386 lib32gcc1 lib32ncurses6 lib32z1 zlib1g:i386 && \
    apt-get install -y --no-install-recommends openjdk-${JDK_VERSION}-jdk && \
    apt-get install -y --no-install-recommends git wget unzip && \
    apt-get install -y --no-install-recommends qt5-default

# 安装中文黑体字体
ADD fonts/simhei.ttf /usr/share/fonts/
RUN fc-cache

# download and install Gradle
# https://services.gradle.org/distributions/
ARG GRADLE_VERSION=6.7
ARG GRADLE_DIST=bin
RUN cd /opt && \
    wget -q https://mirrors.cloud.tencent.com/gradle/gradle-${GRADLE_VERSION}-${GRADLE_DIST}.zip && \
    unzip gradle*.zip && \
    ls -d */ | sed 's/\/*$//g' | xargs -I{} mv {} gradle && \
    rm gradle*.zip

# download and install Android SDK
# https://developer.android.com/studio#command-tools
ARG ANDROID_SDK_VERSION=6858069
ENV ANDROID_SDK_ROOT /opt/android-sdk
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_VERSION}_latest.zip && \
    unzip *tools*linux*.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools && \
    mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/tools && \
    rm *tools*linux*.zip

# set the environment variables
ENV LANG C.UTF-8
ENV JAVA_HOME /usr/lib/jvm/java-${JDK_VERSION}-openjdk-amd64
ENV GRADLE_HOME /opt/gradle
#ENV KOTLIN_HOME /opt/kotlinc
#ENV PATH ${PATH}:${GRADLE_HOME}/bin:${KOTLIN_HOME}/bin:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/cmdline-tools/tools/bin:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/emulator
ENV PATH ${PATH}:${GRADLE_HOME}/bin:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/cmdline-tools/tools/bin:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/emulator
ENV _JAVA_OPTIONS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap
# WORKAROUND: for issue https://issuetracker.google.com/issues/37137213
ENV LD_LIBRARY_PATH ${ANDROID_SDK_ROOT}/emulator/lib64:${ANDROID_SDK_ROOT}/emulator/lib64/qt/lib
# patch emulator issue: Running as root without --no-sandbox is not supported. See https://crbug.com/638180.
# https://doc.qt.io/qt-5/qtwebengine-platform
ENV QTWEBENGINE_DISABLE_SANDBOX 1

# accept the license agreements of the SDK components
ADD license_accepter.sh /opt/
RUN chmod +x /opt/license_accepter.sh && /opt/license_accepter.sh $ANDROID_SDK_ROOT




RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime &&\
    echo "Asia/Shanghai" > /etc/timezone &&\
    mkdir -p /usr/local/src/caring

COPY SaasApp.zip /usr/local/src/caring/SaasApp.zip

RUN cd /usr/local/src/caring &&\
    unzip SaasApp.zip

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

ENV PARAMS=""
ENTRYPOINT ["sh","-c","java -jar -Xmx1024m -Djava.security.egd=file:/dev/./urandom -jar /app.jar $PARAMS"]
