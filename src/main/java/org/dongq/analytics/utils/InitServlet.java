package org.dongq.analytics.utils;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(InitServlet.class);
	
	private final String METHOD = "m";
	
	@Override
	public void init(ServletConfig config) throws ServletException {}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String method = req.getParameter(METHOD);
		logger.debug(method);
		if("login".equalsIgnoreCase(method)) {
			login(req, resp);
		} else if("loginOpen".equalsIgnoreCase(method)) {
			loginOpen(req, resp);
		}
	}
	
	void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String no = req.getParameter("no");
		String pwd = req.getParameter("pwd");
		String path = "paper.jsp";

		QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
		Responder responder = service.login(no, pwd);
		if(responder != null && StringUtils.isNotBlank(responder.getNo())) {
			boolean answered = service.hasAnswered(responder.getId());
			if(answered) {
				resp.sendRedirect("login.jsp?msg=hasanswered");
			} else {
				req.setAttribute("v", responder.getVersion());
				req.setAttribute("id", responder.getId());
				req.setAttribute("name", responder.getName());
				req.getRequestDispatcher(path).forward(req, resp);
			}
		} else {
			resp.sendRedirect("login.jsp?msg=loginfail");
		}
		
	}
	
	void loginOpen(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter("name");
		
		logger.debug("responder name is " + name);
		req.setAttribute("name", name);
		req.getRequestDispatcher("paper_open.jsp").forward(req, resp);
	}
}
