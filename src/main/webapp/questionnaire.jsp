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
				<input type="hidden" name="paperType" value="check"/>
				<input type="hidden" name="responderId" value="<%=request.getParameter("id") %>"/>
				<input type="hidden" name="version" value="<%=request.getParameter("v") %>" />
				<input type="hidden" name="name" value="<%=new String(request.getParameter("name").getBytes("ISO-8859-1"), "UTF-8") %>" />
				<input type="hidden" name="ctx" value="<%="http://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath() %>"/>
			</div>
		</form>
	</div>

	<a style="cursor: pointer;">提交</a>
</body>
</html>