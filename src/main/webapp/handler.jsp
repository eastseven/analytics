<%@page import="org.dongq.analytics.model.*"%>
<%@page import="org.dongq.analytics.service.*"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	final String prefix_question = "question_";
	final String prefix_matrix   = "matrix_";
	final String prefix_property = "property_";
	Map params = request.getParameterMap();
	Set keys = params.keySet();

	Map answer = new HashMap();
	
	for(Iterator iter = keys.iterator(); iter.hasNext(); ) {
		String key = (String)iter.next();
		String[] value = (String[])params.get(key);
		String text = "";
		for(int index = 0; index < value.length; index++) {
			text += "," + value[index];
		}
		out.print(key+" : "+text+"<br />");
		
		if(key.startsWith(prefix_question)) {
			answer.put(key, value[0]);
		}
		
		if(key.startsWith(prefix_matrix)) {
			answer.put(key, text);
		}
	}
	
	String responderId = request.getParameter("responderId");
	String version = request.getParameter("version");
	Responder responder = new Responder();
	responder.setId(Long.valueOf(responderId));
	responder.setVersion(Long.valueOf(version));
	
	boolean bln = new QuestionnairePaperServiceImpl().saveQuestionnairePaper(responder, answer);
	out.print("<h1>"+bln+"</h1>");
	/*
	if(!answers.isEmpty()) {
		QuestionnairePaper paper = new QuestionnairePaper();
		paper.setId(Long.valueOf(request.getParameter("questionnaireId")));
		paper.setAnswers(answers);
		String username = request.getParameter("username");
		String gender = request.getParameter("gender");
		Responder responder = new Responder();
		responder.setName(username);
		responder.setGender(gender);
		
		paper.setResponder(responder);
		
		boolean bln = new QuestionnairePaperServiceImpl().saveQuestionnairePaper(paper);
		out.print(bln);
	}
	*/
%>