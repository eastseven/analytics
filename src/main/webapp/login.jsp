<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="css/ez-min.css" type="text/css">
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<style type="text/css">
table { border-collapse:collapse; }
table, th, td { border: 1px solid black; }
</style>

<script type="text/javascript">

var msg = '<%=request.getParameter("msg")%>';

$(function() {
	if(msg == 'loginfail') alert('编号或者密码错误');
	
	$('#submit').click(function() {
		var no = $('input[name=no]').val();
		var pwd = $('input[name=pwd]').val();
		if(no == '') {
			alert('受访者编号不能为空');
		} else if(pwd == '') {
			alert('受访者密码不能为空');
		} else {
			$('table').wrap('<form method=post action="./eastseven"></form>');
		}
	});
	
	$('#admin').click(function() {
		self.location = 'login.zul';
	});
});
</script>
<title>封闭式社会网络问卷调查</title>
</head>
<body>
<!-- Layout 3 -->
<div class="ez-wr">
  <div class="ez-box"><!-- .header. -->&nbsp;<br/>&nbsp;<br/>&nbsp;</div>
    <!-- Module 3A -->
    <div class="ez-wr">
      <div class="ez-fl  ez-negmx ez-33">
        <div class="ez-box"><!-- .1. -->&nbsp;</div>
      </div>
      <div class="ez-fl ez-negmr ez-33">
        <div class="ez-box">
			<table align="center">
				<tr>
					<td>受访者编号：</td>
					<td><input type="text" name="no" value=""/></td>
				</tr>
				<tr>
					<td>受访者密码：</td>
					<td><input type="text" name="pwd" value=""/></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="hidden" name="m" value="login"/>
						<button id="submit">开始答题</button>
						<button id="admin">管理后台</button>
					</td>
				</tr>
			</table>
		</div>
      </div>
      <div class="ez-last ez-oh">
        <div class="ez-box"></div>
      </div>
    </div>
  <div class="ez-box"></div>
</div>
</body>
</html>