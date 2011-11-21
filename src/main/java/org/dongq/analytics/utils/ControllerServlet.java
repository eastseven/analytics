package org.dongq.analytics.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;

import com.alibaba.fastjson.JSON;

public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 8908347914853579104L;
	private static final Log logger = LogFactory.getLog(ControllerServlet.class);

	final String METHOD = "action";
	final String GetRespondersOfVersion = "getRespondersOfVersion";
	final String GetMatrixNetOfVersion = "getMatrixNetOfVersion";
	final String GetQuestionnairePaper = "getQuestionnairePaper";

	private QuestionnairePaperService service;

	public ControllerServlet() {
		service = new QuestionnairePaperServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// super.doGet(req, resp);
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		final String method = req.getParameter(METHOD);
		if (GetRespondersOfVersion.equals(method)) {
			getRespondersOfVersion(req, resp);
		} else if(GetMatrixNetOfVersion.equals(method)) {
			getMatrixNetOfVersion(req, resp);
		} else if(GetQuestionnairePaper.equals(method)) {
			getQuestionnairePaper(req, resp);
		}
	}

	void getRespondersOfVersion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		String versionString = req.getParameter("version");
		long version = Long.parseLong(versionString);
		List<Responder> list = service.getRespondersOfVersion(version);
		if (list != null && !list.isEmpty()) {
			logger.debug(list);
			String result = JSON.toJSONString(list);
			PrintWriter out = resp.getWriter();
			logger.debug(result);
			out.write(result);
			out.close();
		}
	}
	
	void getMatrixNetOfVersion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		String versionString = req.getParameter("version");
		long version = Long.parseLong(versionString);
		Questionnaire blankPaper = service.getBlankQuestionnaire(version);
		List<Question> list = blankPaper.getMatrixNet();
		if (list != null && !list.isEmpty()) {
			logger.debug(list);
			String result = JSON.toJSONString(list);
			PrintWriter out = resp.getWriter();
			logger.debug(result);
			out.write(result);
			out.close();
		}
	}
	
	void getQuestionnairePaper(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		
		String versionString = req.getParameter("version");
		String responderIdString = req.getParameter("responderId");
		
		long version = Long.parseLong(versionString);
		logger.info("v:" + version);
		long responderId = Long.parseLong(responderIdString);
		
		Questionnaire blankPaper = service.getQuestionnaire(responderId);
		logger.debug(blankPaper);
		String result = JSON.toJSONString(blankPaper);
		PrintWriter out = resp.getWriter();
		logger.debug(result);
		out.write(result);
		out.close();
	}
}
