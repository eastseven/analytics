<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	final String prefix = "question_";
	Map params = request.getParameterMap();
	Set keys = params.keySet();
	for(Iterator iter = keys.iterator(); iter.hasNext(); ) {
		String key = (String)iter.next();
		if(key.startsWith(prefix)) {
			Object[] value = (Object[])params.get(key);
			out.print(key + "=" + value[0] + "<br />");
			
		}
	}
%>