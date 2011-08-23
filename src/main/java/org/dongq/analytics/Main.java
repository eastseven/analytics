package org.dongq.analytics;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dongq.analytics.model.Option;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.QuestionGroup;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnaireFactory;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;
import org.dongq.analytics.utils.DbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

	final static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	final static String url = "jdbc:derby:sample;create=true";

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
//		List<QuestionnairePaper> papers = new ArrayList<QuestionnairePaper>();
//		int count = 0;
//		while(count < 10) {
//			papers.add(getQuestionnairePaper());
//			count++;
//		}
//
//		excel(papers);
		
		db();
		
		new QuestionnairePaperServiceImpl().getQuestionnaire(0);
	}

	public static QuestionnairePaper getQuestionnairePaper() throws Exception {
		QuestionnairePaper paper = new QuestionnairePaper();
		
		paper.setId(System.currentTimeMillis());
		paper.setQuestionnaire(QuestionnaireFactory.getSampleQuestionnaire());
		//填写问卷
		Map<Long, Integer> answers = new HashMap<Long, Integer>();
		List<QuestionGroup> list = paper.getQuestionnaire().getQuestions();
		for(QuestionGroup group : list) {
			for(Question q : group.getQuestions()) {
				int item = RandomUtils.nextInt(group.getOptions().size()) + 1;
				answers.put(q.getId(), item);
//				String answer = "题号："+q.getId()+"选择："+item;
//				logger.info(answer);
			}
		}
		paper.setAnswers(answers);
		
		Responder responder = new Responder();
		responder.setId(System.currentTimeMillis());
		paper.setResponder(responder);
		
		return paper;
	}
	
	public static void excel(List<QuestionnairePaper> papers) throws Exception {
		Questionnaire questionnaire = papers.get(0).getQuestionnaire();
		List<Question> questions = questionnaire.getAllQuestions();
		logger.info("questions is empty : "+questions.isEmpty());
		Workbook wb = new HSSFWorkbook();
		
	    Sheet sheet = wb.createSheet("Questionnaire No." + questionnaire.getId());

	    //打印问卷题号
	    Row title = sheet.createRow(0);
	    title.createCell(0).setCellValue("No.");
	    for(int index = 0; index < questions.size(); index++) {
	    	Question q = questions.get(index);
	    	Cell cell = title.createCell(index+1);//第一列空出来做答卷人编号
	    	cell.setCellValue(String.valueOf(q.getId()));
	    }
	    
	    logger.info("title cell number is " + title.getRowNum() + "|" + title.getPhysicalNumberOfCells());
	    
	    //打印本组问卷集
	    int rownum = 1;
	    for(QuestionnairePaper paper : papers) {
	    	Responder person = paper.getResponder();
	    	Map<Long, Integer> answers = paper.getAnswers();
	    	
	    	Row row = sheet.createRow(rownum);
	    	Cell personNo = row.createCell(0);
	    	personNo.setCellValue(String.valueOf(person.getId()));
	    	int index = 1;

	    	for(Iterator<Question> iter = questions.iterator(); iter.hasNext();) {
	    		Question q = iter.next();
	    		int answer = answers.get(q.getId());
	    		//Cell c = title.getCell(index);
	    		//logger.debug("问卷题号：["+c.getStringCellValue()+"|"+q.getId()+"]选择值："+answer);
	    		row.createCell(index).setCellValue(String.valueOf(answer));
	    		index++;
	    	}
	    	
	    	rownum++;
	    }

	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream("workbook.xls");
	    wb.write(fileOut);
	    fileOut.close();
	    logger.info("create excel file done...");
	}
	
	public static void db() throws Exception {
		final String sql = "select a.* from questionnaire_paper_answer a ";
		Connection conn = DbHelper.getConnection();
		QueryRunner query = new QueryRunner();
		query.query(conn, sql, new ResultSetHandler<Option>() {

			public Option handle(ResultSet rs) throws SQLException {
				while(rs.next()) {
					//logger.info(rs.getObject(1) + "-" + rs.getObject(2) + "-" + rs.getObject(3) + "-" +rs.getObject(4));
					logger.info(rs.getObject(1) + "-" + rs.getObject(2));
				}
				return null;
			}
			
		});
	}
}
