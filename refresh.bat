@echo off
set curdir=%~dp0
set partition=%curdir:~0,1%

echo ����Ŀ¼��%curdir%
%partition%:
cd %curdir%

echo ������
call mvn clean eclipse:clean

echo ����eclipse���̻���,����JARԴ����,����Ŀ����Ϊweb��Ŀ
call mvn eclipse:eclipse -DdownloadSources=true -Dwtpversion=2.0

echo ���ɹ�������JAR��,�ŵ�WEB-INF/lib
echo ɾ�� %curdir%src\main\webapp\WEB-INF\lib\ �����jar��
cd %curdir%src\main\webapp\WEB-INF\lib\
del *.jar
cd %curdir%
call mvn dependency:copy-dependencies

echo MAVEN���̹������
pause