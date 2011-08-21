package org.dongq.analytics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.QuestionGroup;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnaireFactory;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

	final static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	final static String url = "jdbc:derby:sample;create=true";

	final static Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		List<QuestionnairePaper> papers = new ArrayList<QuestionnairePaper>();
		int count = 0;
		while(count < 10) {
			papers.add(getQuestionnairePaper());
			count++;
		}

		excel(papers);
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
				String answer = "题号："+q.getId()+"选择："+item;
				logger.info(answer);
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
	    for(Iterator<Cell> iter = title.cellIterator(); iter.hasNext();) {
	    	logger.info(iter.next().getStringCellValue());
	    }
	    
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
	    		Cell c = title.getCell(index);
	    		logger.debug("问卷题号：["+c.getStringCellValue()+"|"+q.getId()+"]选择值："+answer);
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
		Base.open(driver, url, null);
		
		String drop = "DROP TABLE people";
		String create  = "CREATE TABLE people (name VARCHAR(56) NOT NULL,last_name VARCHAR(56), createTime TIME)";
		try {
			Base.exec(drop);
		} catch (Exception e) {
			logger.info("people表不存在，将自动创建");
		}
		Base.exec(create);
		
		Base.close();
		logger.info("done...");
		
		InputStream input = new FileInputStream("/Users/eastseven/Desktop/网络数据1-26-交流频率已反向.xls");
		POIFSFileSystem fs = new POIFSFileSystem(input);  
		HSSFWorkbook wb = new HSSFWorkbook(fs);  
		int numberOfSheets = wb.getNumberOfSheets();
		logger.info("Number Of Sheets "+numberOfSheets);
		HSSFSheet sheet = null;
		for(int index = 0; index < numberOfSheets; index++) {
			sheet = wb.getSheetAt(index);
			logger.info(sheet.getSheetName());
		}
		
	}
}
