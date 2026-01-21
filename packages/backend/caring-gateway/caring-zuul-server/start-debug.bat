@echo off
echo Starting caring-zuul-server with debug logging...
echo.

cd /d D:\Projects4Java\caring-patient-platform\packages\backend\caring-gateway\caring-zuul-server

java -Xms512m -Xmx1024m ^
  -Dlogging.level.com.alibaba.nacos=DEBUG ^
  -Dlogging.level.net.oschina.j2cache=DEBUG ^
  -Dlogging.level.org.springframework.cloud.bootstrap=DEBUG ^
  -jar target\caring-zuul-server.jar

pause
