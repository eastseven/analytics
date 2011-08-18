<%@page import="java.util.List"%>
<%@page import="org.dongq.analytics.model.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sample</title>
</head>
<body>

	<%
		Questionnaire paper = QuestionnaireFactory.getSampleQuestionnaire();
	%>
	<form action="">
		<%
			for(int index = 0; index < paper.getQuestions().size(); index++) {
				QuestionGroup item = (QuestionGroup)paper.getQuestions().get(index);
				out.println(item.getTitle()+"<br/>");
				List list = item.getQuestions();
				for(int index1 = 0; index1 < list.size(); index1++) {
					Question q = (Question)list.get(index1);
					String html = (index1+1) + "." + q.getContent();
					List options = q.getOptions();
					for(int index2 = 0; index2 < options.size(); index2++) {
						Option option = (Option)options.get(index2);
						html += "  <input type='radio' name='"+("q_"+index1)+"' value='"+option.getKey()+"'>" + option.getValue() + " ";
					}
					out.println(html+"<br/>");
				}
				out.print("<br/>");
			}
		%>
	</form>

</body>
</html>