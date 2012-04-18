<%@page import="org.dongq.analytics.service.QuestionnairePaperService"%>
<%@page import="org.dongq.analytics.service.QuestionnairePaperServiceImpl"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>开放式社会网络调查问卷</title>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>

<link rel="stylesheet" href="js/jquery/css/smoothness/jquery-ui-1.8.16.custom.css" />
<script type="text/javascript" src="js/jquery/js/jquery-ui-1.8.16.custom.min.js"></script>

<link rel="stylesheet" href="./a_files/default.css" type="text/css">
<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">

<style type="text/css">
.subutton{ background:url(./images/button_bg3.gif); width:100px; height:34px; border:none;}
/* 
.trapezoid table { border-collapse:collapse; }
.trapezoid table, th, td { border: 1px solid black; }
.matrixNet table { border-collapse:collapse; }
.matrixNet table, th, td { border: 1px solid black; }
 */
</style>

<%
	QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
	String ctx = "http://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath();
%>

</head>
<body>

<script type="text/javascript" src="js/open.js"></script>

	<h1><%=request.getAttribute("name") %></h1>
	<div id="main">
		<div id="info"/>
		<!-- <form method="post" id="survey"> -->
			<div class="survey_body">
				<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">
				<div class="page">
					<div class="page-header">
						<h1 class="survey-title">调查表</h1>
						<div id="title"><%=service.getQuestionnaireTitle(request.getAttribute("v")) %></div>
					</div>
					<ol class="content">
					
					</ol>
				</div>
			</div>
			<div id="questionnaire">
				<input type="hidden" name="name" value="<%=request.getAttribute("name") %>" />
				<input type="hidden" name="version" value="<%=service.getOpenPaperVersion() %>"/>
				<input type="hidden" name="ctx" value="<%=ctx %>"/>
			</div>
		<!-- </form> -->
	</div>
	<a style="cursor: pointer;" name="submit"><button class="subutton">提交</button></a>
</body>
</html>