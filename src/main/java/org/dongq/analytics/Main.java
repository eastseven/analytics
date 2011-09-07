package org.dongq.analytics;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;
import org.dongq.analytics.utils.DbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public final class Main {

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
		QueryRunner query = new QueryRunner();
		String sql = "select min(responder_id) from responder";
		Long id = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<Long>() {
			@Override
			public Long handle(ResultSet rs) throws SQLException {
				if(rs.next()) return rs.getLong(1);
				return null;
			}
		});
		Questionnaire q = service.getQuestionnaire(id);
		String json = JSON.toJSONString(q);
		logger.info(json);
	}

}
