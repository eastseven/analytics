package org.dongq.analytics.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.QuestionGroup;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnaireFactory;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.utils.DbHelper;

public class QuestionnairePaperServiceImpl implements QuestionnairePaperService {

	final static Log logger = LogFactory.getLog(QuestionnairePaperServiceImpl.class);
	
	public Questionnaire getQuestionnaire(long id) {
		
		Questionnaire blankPaper = new Questionnaire();
		blankPaper.setId(id);
		Connection conn = DbHelper.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		try {
			logger.info("start...");
			String sql = "select a.question_id, a.content from question a,questionnaire b where a.question_id = b.question_id and b.questionnaire_id = " + id + " and a.parent_id = 0";
			logger.info(sql);
			List<QuestionGroup> list = queryRunner.query(conn, sql, new ResultSetHandler<List<QuestionGroup>>() {
				public List<QuestionGroup> handle(ResultSet rs) throws SQLException {
					List<QuestionGroup> list = new ArrayList<QuestionGroup>();
					while(rs.next()) {
						QuestionGroup e = new QuestionGroup();
						e.setId(rs.getLong("question_id"));
						e.setTitle(rs.getString("content"));
						list.add(e);
					}
					return list;
				}
				
			});
			
			if(list != null && !list.isEmpty()) {
				List<QuestionGroup> _list = new ArrayList<QuestionGroup>();
				for(QuestionGroup group : list) {
					sql = "select a.question_id, a.content from question a,questionnaire b where a.parent_id = "+group.getId()+" and a.question_id = b.question_id and b.questionnaire_id = " + id;
					logger.info(sql);
					List<Question> questions = queryRunner.query(conn, sql, new ResultSetHandler<List<Question>>() {
						public List<Question> handle(ResultSet rs) throws SQLException {
							List<Question> list = new ArrayList<Question>();
							while(rs.next()) {
								Question e = new Question();
								e.setId(rs.getLong("question_id"));
								e.setContent(rs.getString("content"));
								e.setOptions(QuestionnaireFactory.getOptions());
								list.add(e);
							}
							return list;
						}
					});
					group.setQuestions(questions);
					_list.add(group);
				}
				if(!_list.isEmpty()) {
					list.clear();
					list.addAll(_list);
				}
			}
			
			blankPaper.setQuestions(list);
			DbUtils.close(conn);
			logger.info(blankPaper.text());
			logger.info("finish...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return blankPaper;
	}

	public boolean saveQuestionnairePaper(QuestionnairePaper paper) {
		boolean bln = false;
		
		try {
			Connection conn = DbHelper.getConnection();
			QueryRunner queryRunner = new QueryRunner();
			
			Responder responder = paper.getResponder();
			long responderId = System.currentTimeMillis();
			String sql = "insert into responder(responder_id,name,gender) values("+responderId+",'"+responder.getName()+"','"+responder.getGender()+"')";
			int record = queryRunner.update(conn, sql);
			logger.info(record);
			
			long paperId = System.currentTimeMillis()+1;
			sql = "insert into questionnaire_paper(paper_id,responder_id,questionnaire_id,sign_date) values("+paperId+","+responderId+","+paper.getId()+",CURRENT_DATE)";
			record = queryRunner.update(conn, sql);
			logger.info(record);
			
			sql = "insert into questionnaire_paper_answer(paper_id,answer_key,answer_value) values(?,?,?)";
			Map<Long, Integer> answers = paper.getAnswers();
			Object[][] params = new Object[answers.size()][3];
			int index = 0;
			for(Iterator<Long> iter = answers.keySet().iterator(); iter.hasNext();) {
				long key = iter.next();
				int value = answers.get(key);
				params[index][0] = paperId;
				params[index][1] = key;
				params[index][2] = value;
				index++;
			}
			int[] records = queryRunner.batch(conn, sql, params);
			logger.info(index + "=" + records.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bln;
	}

	public static void main(String[] arg) throws Exception {
		QueryRunner q = new QueryRunner();
		String sql = "insert into questionnaire_paper_answer(paper_id,answer_key,answer_value) values(1,?,?)";
		Object[][] params = {{1,1},{2,2},{3,3}};
		q.batch(DbHelper.getConnection(), sql, params);
		
		System.out.println("done...");
	}
}
