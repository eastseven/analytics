<%@page import="java.util.*"%>
<%@page import="org.dongq.analytics.model.*"%>
<%@page import="org.dongq.analytics.service.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Questionnaire</title>

<link type="text/css" href="js/jquery/css/smoothness/jquery-ui-1.8.16.custom.css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery/js/jquery-ui-1.8.16.custom.min.js"></script>
<style>
	#feedback { font-size: 1.4em; }
	#selectable .ui-selecting { background: #FECA40; }
	#selectable .ui-selected { background: #F39814; color: white; }
	#selectable { list-style-type: none; margin: 0; padding: 0; }
	#selectable li { margin: 3px; padding: 1px; float: left; width: 20px; height: 20px; font-size: 1em; text-align: center; }
</style>
<script>
	$(function() {
		//$( "#selectable" ).selectable();
	});
</script>
</head>
<body>
<!-- 
<div class="demo">

<ol id="selectable">
	<li class="ui-state-default">1</li>
	<li class="ui-state-default">2</li>
	<li class="ui-state-default">3</li>
	<li class="ui-state-default">4</li>
	<li class="ui-state-default">5</li>
	<li class="ui-state-default">6</li>
	<li class="ui-state-default">7</li>
	<li class="ui-state-default">8</li>
	<li class="ui-state-default">9</li>
	<li class="ui-state-default">10</li>
	<li class="ui-state-default">11</li>
	<li class="ui-state-default">12</li>
</ol>

</div>
 -->
	<%
		String _id = request.getParameter("id");
		String version = request.getParameter("v");
		long responderId = Long.valueOf(_id);
		QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
		Questionnaire paper = service.getQuestionnaire(responderId);
	%>
	<br /><strong> <h1><%=responderId%></h1> </strong>
	<form action="handler.jsp" method="post">
		<p>
			<input type="submit" value="提交" />
			<input type="hidden" name="responderId" value="<%=responderId %>"/>
			<input type="hidden" name="version" value="<%=version %>" />
		</p>
		<%
			List matrix = paper.getMatrix();
			List people = paper.getPeople();
			for (int matrixIndex = 0; matrixIndex < matrix.size(); matrixIndex++) {
				Question q = (Question) matrix.get(matrixIndex);
				out.print("<p><h3>" + q.getTitle() + "</h3></p>");
				String content = "";
				for(int peopleIndex = 0; peopleIndex < people.size(); peopleIndex++) {
					Responder person = (Responder)people.get(peopleIndex);
					if(person.getId() == responderId) continue;
					content += "<input type='checkbox' name='matrix_"+q.getId()+"' value='"+person.getId()+"'/>" + person.getName();
				}
				out.print(content + "<br />");
			}
		%>
		<%
			List list = paper.getGroup();
			for (int index = 0; index < list.size(); index++) {
				QuestionGroup group = (QuestionGroup) list.get(index);
				String title = group.getTitle();
				out.print("<p><h3>" + title + "</h3></p>");
				List questions = group.getQuestions();
				for (int questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
					Question question = (Question) questions.get(questionIndex);
					out.print("<p>" + (questionIndex + 1) + "." + question.getContent() + " : " + question.getSelect() + "</p>");
				}
			}
		%>
		<p>
			<input type="submit" value="提交" />
		</p>
	</form>
</body>
</html>