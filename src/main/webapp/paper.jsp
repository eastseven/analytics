<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>调查问卷</title>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>

<link rel="stylesheet" href="js/jquery/css/smoothness/jquery-ui-1.8.16.custom.css" />
<script type="text/javascript" src="js/jquery/js/jquery-ui-1.8.16.custom.min.js"></script>

<link rel="stylesheet" href="./a_files/default.css" type="text/css">
<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">

<style type="text/css">
.subutton{ background:url(./images/button_bg3.gif); width:100px; height:34px; border:none;}
</style>

</head>
<body>

	<script type="text/javascript" src="js/paper.js"></script>

	<div id="main">
		<div id="info"/>
		<form method="post" id="survey">
			<div class="survey_body">
				<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">
				<div class="page">
					<div class="page-header">
						<h1 class="survey-title">调查表</h1>
					</div>
					<ol class="content"></ol>
				</div>
			</div>
			<div id="questionnaire">
				<input type="hidden" name="responderId" value="<%=request.getAttribute("id") %>"/>
				<input type="hidden" name="version" value="<%=request.getAttribute("v") %>" />
				<input type="hidden" name="name" value="<%=request.getAttribute("name") %>" />
				<input type="hidden" name="ctx" value="<%="http://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath() %>"/>
			</div>
		</form>
	</div>

	<a style="cursor: pointer;"><button class="subutton">提交</button></a>
</body>
</html>