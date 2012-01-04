<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" href="css/ez-min.css" type="text/css">
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<style type="text/css">

table
  {
  border-collapse:collapse;
  }

table,th, td
  {
  border: 1px solid black;
  }

</style>
<script type="text/javascript">
$(function() {
	
});
</script>
</head>
<body>

<%
	Map map = request.getParameterMap();
	for(Iterator iter = map.keySet().iterator(); iter.hasNext();) {
		Object key = iter.next();
		out.print(key + " : " + map.get(key) + "<br/>");
	}
%>

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
					<td><input type="text" name="no" value="<%=request.getParameter("no")%>"/></td>
				</tr>
				<tr>
					<td>受访者密码：</td>
					<td><input type="text" name="pwd" value="<%=request.getParameter("pwd")%>"/></td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="hidden" name="m" value="login"/>
						<!-- <input type="submit" value="开始答题"/> -->
						<button id="submit">开始答题</button>
					</td>
				</tr>
			</table>
		</div>
      </div>
      <div class="ez-last ez-oh">
        <div class="ez-box"><!-- .3. --></div>
      </div>
    </div>
  <div class="ez-box"><!-- .footer. --></div>
</div>

</body>
</html>