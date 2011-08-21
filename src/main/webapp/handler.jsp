<%@page import="org.dongq.analytics.model.Responder"%>
<%@page import="org.dongq.analytics.model.QuestionnairePaper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	final String prefix = "question_";
	Map params = request.getParameterMap();
	Set keys = params.keySet();
	Map answers = new HashMap();
	for(Iterator iter = keys.iterator(); iter.hasNext(); ) {
		String key = (String)iter.next();
		if(key.startsWith(prefix)) {
			Object[] value = (Object[])params.get(key);
			key = key.replaceAll(prefix, "");
			answers.put(Integer.parseInt(key), Integer.parseInt(value[0]));
			
		}
	}
	
	if(!answers.isEmpty()) {
		QuestionnairePaper paper = new QuestionnairePaper();
		paper.setAnswers(answers);
		String username = request.getParameter("username");
		String gender = request.getParameter("gender");
		Responder responder = new Responder();
		responder.setName(username);
		responder.setGender(gender);
		
		paper.setResponder(responder);
	}
%>