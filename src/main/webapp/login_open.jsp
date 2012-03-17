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

$(function() {
	
	$('#submit').click(function() {
		var name = $('input[name=name]').val();
		if(name == '') {
			alert('受访者姓名不能为空');
		} else {
			$('table').wrap('<form method=post action="./eastseven"></form>');
		}
		
	});
	
});
</script>

<title>开放式社会网络问卷调查</title>
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
					<td>受访者姓名：</td>
					<td><input type="text" name="name" value=""/></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="hidden" name="m" value="loginOpen"/>
						<button id="submit">开始答题</button>
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