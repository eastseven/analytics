<%@page import="org.dongq.analytics.model.*"%>
<%@page import="org.dongq.analytics.service.*"%>
<%@page import="java.util.*"%>
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
			answers.put(Long.valueOf(key), Integer.valueOf(value[0].toString()));
		}
	}
	
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
%>