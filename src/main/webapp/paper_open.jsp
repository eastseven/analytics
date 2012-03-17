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

<script type="text/javascript" src="js/open.js"></script>
<%
	String ctx = "http://"+request.getServerName()+":"+request.getServerPort()+""+request.getContextPath();
%>
</head>
<body>
	<h1><%=request.getAttribute("name") %></h1>
	<h2><%=ctx %></h2>
	<div id="main">
		<div id="info"/>
		<form method="post" id="survey">
			<div class="survey_body">
				<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">
				<div class="page">
					<div class="page-header">
						<h1 class="survey-title">调查表</h1>
						<div id="title">XXXXX</div>
					</div>
					<ol class="content">
						<div id="matrix"/>
						<div id="matrixNet"/>
						<div id="normal"/>
					<!-- 
						<li class="part select">
							<h4 class="title">XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX</h4>
							<table class="options">
								<tbody>
									<tr class="odd"><td>A</td></tr>
									<tr class="even"><td>B</td></tr>
									<tr class="odd"><td>C</td></tr>
								</tbody>
							</table>
						</li>
					 -->
					</ol>
				</div>
			</div>
			<div id="questionnaire">
				<input type="hidden" name="name" value="<%=request.getAttribute("name") %>" />
				<input type="hidden" name="ctx" value="<%=ctx %>"/>
			</div>
		</form>
	</div>

</body>
</html>