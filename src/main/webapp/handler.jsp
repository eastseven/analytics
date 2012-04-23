<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.dongq.analytics.model.*"%>
<%@page import="org.dongq.analytics.service.*"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	final String prefix_question     = "question_";
	final String prefix_matrix       = "matrix_";
	final String prefix_matrix_net   = "matrixNet_";
	final String prefix_matrix_plus  = "matrixPlus_";
	final String prefix_property     = "property_";
	final String prefix_person       = "person";
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
		
		if(key.startsWith(prefix_question)) {
			answer.put(key, value[0]);
			//out.print(prefix_question+"="+key + ":" + value[0] + "<br/>");
		}
		
		if(key.startsWith(prefix_matrix)) {
			answer.put(key, text);
			//out.print(prefix_matrix+"="+key + ":" + text + "<br/>");
		}
		
		if(key.startsWith(prefix_matrix_net)) {
			answer.put(key, text);
			//out.print(prefix_matrix_net+"="+key + ":" + text + "<br/>");
		}
		
		if(key.startsWith(prefix_matrix_plus)) {
			answer.put(key, value[0]);
			//out.print(prefix_matrix_plus+"="+key + ":" + value[0] + "<br/>");
		}
		
		if(key.startsWith(prefix_property)) {
			text = text.replace(",", "");
			answer.put(prefix_property+text, text);
			//out.print(prefix_property+"="+key+":"+text+"<br/>");
		}
		
		if(key.startsWith(prefix_person)) {
			if(!StringUtils.endsWith(text, ",")) {
				text = text.replace(",", "");
				answer.put(key, text);
				//out.print(prefix_person+"="+key+":"+text+"<br/>");
			}
		}
		
	}
	
	Responder responder = new Responder();
	String responderId = request.getParameter("responderId");
	String version = request.getParameter("version");
	responder.setVersion(Long.valueOf(version));
	
	if(StringUtils.isBlank(responderId)) {
		responder.setId(0);
		responder.setName(request.getParameter("name"));
	} else {
		responder.setId(Long.valueOf(responderId));
	}
	
	boolean bln = new QuestionnairePaperServiceImpl().saveQuestionnairePaper(responder, answer);
	//TODO 没有做跳转
	if(bln) {
		//out.print("<h1>衷心感谢您参与本次调研，谢谢！</h1>");
		response.sendRedirect("200.html");
	} else {
		//out.print("<h1>ops! exception, oh no!!! please try again</h1>");
		response.sendRedirect("500.html");
	}
%>