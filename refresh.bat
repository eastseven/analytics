@echo off
set curdir=%~dp0
set partition=%curdir:~0,1%

echo 工程目录：%curdir%
%partition%:
cd %curdir%

echo 清理工作
call mvn clean eclipse:clean

echo 构建eclipse工程环境,下载JAR源代码,将项目订制为web项目
call mvn eclipse:eclipse -DdownloadSources=true -Dwtpversion=2.0

echo 生成工程依赖JAR包,放到WEB-INF/lib
echo 删除 %curdir%src\main\webapp\WEB-INF\lib\ 下面的jar包
cd %curdir%src\main\webapp\WEB-INF\lib\
del *.jar
cd %curdir%
call mvn dependency:copy-dependencies

echo MAVEN工程构建完成
pause