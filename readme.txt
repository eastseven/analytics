1.安装步骤
1.1.数据库的安装
	数据库使用derby-10.8.2.2, 进入db-derby-10.8.2.2-bin目录下的bin目录，启动数据库的命令：nohup sh startNetworkServer -h localhost -p 1527 &,关闭数据库：先ps -ef|grep derby,找到derby的进程ID，kill掉既可
	derby使用手册在doc目录下，非常的详细，数据库默认端口是1527
1.2.应用的安装
	analytics是一个标准的java web应用，只需将其拷入常用的java web容器，如tomcat、jetty等的webapp目录下，启动容器既可，该应用已经在tomcat7.0.25上测试通过

2.使用手册
2.1.管理后台
	地址：http://ip:port/analytics/index.zul,管理员账号：admin，密码：000000
	下载试卷模版，填写好后上传，既可
2.2.封闭式问卷
	地址：http://ip:port/analytics/login.jsp
2.3.开放式问卷
	地址：http://ip:port/analytics/login_open.jsp