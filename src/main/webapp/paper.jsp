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

	<script type="text/javascript" src="js/paper.js">
	</script>

	<div id="main">
		<form method="post" id="survey">
			<div class="survey_body">
				<link rel="stylesheet" href="./a_files/default(1).css" type="text/css">
				<div class="page">
					<div class="page-header">
						<h1 class="survey-title">调查表</h1>
					</div>
					<ol class="content">
						<!-- <li class="part select" name="aaa" id="aaa">
								<h4 class="title">
									<span class="subject">您的问题是</span> <span class="require">*</span>
									<label class="error"></label>
								</h4>
								<table class="options">
									<tbody>
										<tr class="odd">
											<td><input type="radio"
												id="f0bf5c05-e4db-4ff8-95a0-0ab1e97c7790"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="f0bf5c05-e4db-4ff8-95a0-0ab1e97c7790"><label
												for="f0bf5c05-e4db-4ff8-95a0-0ab1e97c7790">A. BUG</label></td>
											<td><input type="radio"
												id="21cdac11-f92a-4b10-96d6-fbf44b414f07"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="21cdac11-f92a-4b10-96d6-fbf44b414f07"><label
												for="21cdac11-f92a-4b10-96d6-fbf44b414f07">B. 网站内容</label></td>
										</tr>
										<tr class="even">
											<td><input type="radio"
												id="406635c6-a3cf-49b3-b08c-458cd5ee5765"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="406635c6-a3cf-49b3-b08c-458cd5ee5765"><label
												for="406635c6-a3cf-49b3-b08c-458cd5ee5765">C. 建议与意见</label></td>
											<td><input type="radio"
												id="a989ce53-a664-41f2-ad61-01a7a4ce3222"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="a989ce53-a664-41f2-ad61-01a7a4ce3222"><label
												for="a989ce53-a664-41f2-ad61-01a7a4ce3222">D. 不满</label></td>
										</tr>
										<tr class="odd">
											<td><input type="radio"
												id="b7a08697-0231-4e6f-81ba-cf4213170e5f"
												name="88850302-4534-4dce-902d-73e0990b4e68[]"
												value="b7a08697-0231-4e6f-81ba-cf4213170e5f"><label
												for="b7a08697-0231-4e6f-81ba-cf4213170e5f">E. 其他问题</label></td>
											<td></td>
										</tr>
									</tbody>
								</table>
							</li> -->
					</ol>
				</div>
			</div>
		</form>
	</div>

	<div id="questionnaire">
		<input type="hidden" name="responderId" value="<%=request.getParameter("id") %>"/>
		<input type="hidden" name="version" value="<%=request.getParameter("v") %>" />
		<input type="hidden" name="ctx" value="<%="http://"+request.getLocalAddr()+":"+request.getLocalPort()+""+request.getContextPath() %>"/>
	</div>
	<!-- <button id="submitBtn">提交</button> -->
	<a id="submitBtn" style="cursor: pointer;">提交</a>
</body>
</html>