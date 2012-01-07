package org.dongq.analytics.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.model.Option;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnairePaper;
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
	final String GetQuestionnaireByResponderId = "getQuestionnaireByResponderId";
	final String GetQuestionnaireMatrixNetByResponderId = "getQuestionnaireMatrixNetByResponderId";
	final String GetQuestionnaireMatrixNetAnswer = "getQuestionnaireMatrixNetAnswer";

	private QuestionnairePaperService service;

	public ControllerServlet() {
		service = new QuestionnairePaperServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");
		final String method = req.getParameter(METHOD);
		if (GetRespondersOfVersion.equals(method)) {
			getRespondersOfVersion(req, resp);
		} else if(GetMatrixNetOfVersion.equals(method)) {
			getMatrixNetOfVersion(req, resp);
		} else if(GetQuestionnairePaper.equals(method)) {
			getQuestionnairePaper(req, resp);
		} else if(GetQuestionnaireByResponderId.equals(method)) {
			getQuestionnaireByResponderId(req, resp);
		} else if(GetQuestionnaireMatrixNetByResponderId.equals(method)) {
			getQuestionnaireMatrixNetByResponderId(req, resp);
		} else if(GetQuestionnaireMatrixNetAnswer.equals(method)) {
			getQuestionnaireMatrixNetAnswer(req, resp);
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
	
	void getQuestionnaireByResponderId(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String responderIdString = req.getParameter("responderId");
		String sql = "select * from questionnaire where responder_id = " + responderIdString;
		QueryRunner query = new QueryRunner();
		try {
			List<QuestionnairePaper> list = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<QuestionnairePaper>>() {
				@Override
				public List<QuestionnairePaper> handle(ResultSet rs) throws SQLException {
					List<QuestionnairePaper> list = new ArrayList<QuestionnairePaper>();
					while(rs.next()) {
						QuestionnairePaper e = new QuestionnairePaper();
						e.setFinishTime(rs.getLong("finish_time"));
						e.setOptionKey(rs.getLong("option_key"));
						e.setQuestionId(rs.getLong("question_id"));
						e.setResponderId(rs.getLong("responder_id"));
						e.setType(rs.getInt("type"));
						e.setVersion(rs.getLong("version"));
						list.add(e);
					}
					return list;
				}
				
			});
			
			Object data = list;
			String result = JSON.toJSONString(data);
			PrintWriter out = resp.getWriter();
			logger.debug(result);
			out.write(result);
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	void getQuestionnaireMatrixNetByResponderId(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String responderIdString = req.getParameter("responderId");
		String versionString = req.getParameter("version");
		String sql = "select relation_person_id from questionnaire_matrixnet where responder_id = " + responderIdString + " group by relation_person_id";
		QueryRunner query = new QueryRunner();
		try {
			List<Object> relationPersonIds = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
			
			List<Question> questions = service.getQuestionsOfVersion(Long.valueOf(versionString), Question.TYPE_MATRIX_NET, Long.valueOf(responderIdString));
			logger.debug("questions size : "+questions.size());
			List<Map<String, Object>> finalData = new ArrayList<Map<String,Object>>();
			
			for(Object relationPersonId : relationPersonIds) {
				Map<String, Object> map = new HashMap<String, Object>();
				List<Map<String, Object>> questionList = new ArrayList<Map<String,Object>>();
				for(Question q : questions) {
					sql = "select option_key from questionnaire_matrixnet where ";
					sql += " responder_id = " + responderIdString;
					sql += " and relation_person_id = " + relationPersonId;
					sql += " and question_id = " + q.getId();
					logger.debug("\n\n"+sql+"\n\n");
					Map<String, Object> mapOptionKey = query.query(DbHelper.getConnection(), sql, new MapHandler());
					long optionKey = (Long)mapOptionKey.get("OPTION_KEY");
					for(Option o : q.getOptions()) {
						if(o.getKey() == optionKey) {
							q.setAnswer(o.getValue());
							Map<String, Object> e = new HashMap<String, Object>();
							e.put("answer", q.getAnswer());
							e.put("questionId", q.getId());
							e.put("content", q.getContent());
							questionList.add(e);
							logger.debug("\n\n"+q+"\n\n");
							continue;
						}
					}
				}
				
				map.put("relationPersonId", relationPersonId);
				map.put("questions", questionList);
				finalData.add(map);
			}
			
			for(Map<String, Object> map : finalData) {
				System.out.println("relationPersonId: "+map.get("relationPersonId"));
				System.out.println(map);
			}
			
			Object data = finalData;
			String result = JSON.toJSONString(data);
			PrintWriter out = resp.getWriter();
			logger.debug("\ngetQuestionnaireMatrixNetByResponderId:\n" + result);
			out.write(result);
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void getQuestionnaireMatrixNetAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String questionIdString = req.getParameter("questionId");
		String responderIdString = req.getParameter("responderId");
		String relationPersonIdString = req.getParameter("relationPersonId");
		
		String sql = "select option_key from questionnaire_matrixnet where ";
		sql += " responder_id = " + responderIdString;
		sql += " and relation_person_id = " + relationPersonIdString;
		sql += " and question_id = " + questionIdString;
		QueryRunner query = new QueryRunner();
		try {
			Map<String, Object> optionKey = query.query(DbHelper.getConnection(), sql, new MapHandler());
			
			Object data = optionKey;
			String result = JSON.toJSONString(data);
			PrintWriter out = resp.getWriter();
			logger.debug("\ngetQuestionnaireMatrixNetByResponderId:\n" + result);
			out.write(result);
			out.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
