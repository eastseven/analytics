package org.dongq.analytics.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.QuestionGroup;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnaireFactory;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.model.ResponderProperty;
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
		
//		try {
//			Connection conn = DbHelper.getConnection();
//			QueryRunner queryRunner = new QueryRunner();
//			
//			Responder responder = paper.getResponder();
//			long responderId = System.currentTimeMillis();
//			String sql = "insert into responder(responder_id,name,gender) values("+responderId+",'"+responder.getName()+"','"+responder.getGender()+"')";
//			int record = queryRunner.update(conn, sql);
//			logger.info(record);
//			
//			long paperId = System.currentTimeMillis()+1;
//			sql = "insert into questionnaire_paper(paper_id,responder_id,questionnaire_id,sign_date) values("+paperId+","+responderId+","+paper.getId()+",CURRENT_DATE)";
//			record = queryRunner.update(conn, sql);
//			logger.info(record);
//			
//			sql = "insert into questionnaire_paper_answer(paper_id,answer_key,answer_value) values(?,?,?)";
//			Map<Long, Integer> answers = paper.getAnswers();
//			Object[][] params = new Object[answers.size()][3];
//			int index = 0;
//			for(Iterator<Long> iter = answers.keySet().iterator(); iter.hasNext();) {
//				long key = iter.next();
//				int value = answers.get(key);
//				params[index][0] = paperId;
//				params[index][1] = key;
//				params[index][2] = value;
//				index++;
//			}
//			int[] records = queryRunner.batch(conn, sql, params);
//			logger.info(index + "=" + records.length);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return bln;
	}

	@Override
	public boolean parseQuestionnaireTemplate(InputStream excel) {
		boolean bln = false;
		final long version = System.currentTimeMillis();
		try {
			logger.info(version);
			Workbook workbook = WorkbookFactory.create(excel);
			int numberOfSheets = workbook.getNumberOfSheets();
			for(int index = 0; index < numberOfSheets; index++) {
				Sheet sheet = workbook.getSheetAt(index);
				logger.debug(sheet.getSheetName());
			}
			
			//｛参与调查的人员信息属性｝
			Sheet responderProperty = workbook.getSheetAt(0);
			logger.debug(responderProperty.getSheetName());
			//parseResponderProperty(responderProperty, version);
			
			//｛参与调查的人员信息｝
			Sheet responders = workbook.getSheetAt(1);
			logger.info(responders.getSheetName());
			//parseResponders(responders, version);
			
			//{问卷题目}
			Sheet requestions = workbook.getSheetAt(2);
			logger.info(requestions.getSheetName());
			parseRequestions(requestions, version);
			
			//{矩阵题}
			Sheet matrix = workbook.getSheetAt(3);
			logger.info(matrix.getSheetName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bln;
	}
	
	void parseResponderProperty(Sheet responderProperty, long version) throws SQLException {
		int index = 0;
		for(Iterator<Row> rowIter = responderProperty.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && index > 0) {
				logger.info(index + " : " + row.getCell(0));
				//属性名称
				if(row.getCell(1) != null && !StringUtils.isBlank(row.getCell(1).toString())) {
					String name = row.getCell(0).getStringCellValue();
					//选项值
					int value = 1;
					int columnIndex = 1;
					for(Iterator<Cell> cellIter = row.cellIterator(); cellIter.hasNext();) {
						Cell cell = cellIter.next();
						if(!StringUtils.isBlank(cell.getStringCellValue()) && columnIndex > 1) {
							ResponderProperty p = new ResponderProperty();
							p.setId(System.currentTimeMillis() + columnIndex * Math.round(System.currentTimeMillis()) * 1000);
							p.setDisplay(cell.getStringCellValue());
							p.setName(name);
							p.setValue(value);
							p.setVersion(version);
							
							QueryRunner query = new QueryRunner();
							final String sql = "insert into responder_property values(?,?,?,?,?)";
							int record = query.update(DbHelper.getConnection(), sql, p.getId(), p.getName(), p.getDisplay(), p.getValue(), p.getVersion());
							logger.debug(record);
							value++;
						}
						columnIndex++;
					}
				} else {
					logger.debug(row);
				}
				
			}
			index++;
		}
	}
	
	void parseResponders(Sheet responders, long version) throws Exception {
		QueryRunner query = new QueryRunner();
		String sql = "select * from responder_property where version = " + version;
		List<ResponderProperty> list = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<ResponderProperty>>() {
			@Override
			public List<ResponderProperty> handle(ResultSet rs) throws SQLException {
				List<ResponderProperty> list = new ArrayList<ResponderProperty>();
				while(rs.next()) {
					list.add(new ResponderProperty(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getLong(5)));
				}
				return list;
			}
		});
		logger.info(list.size());
		//取表头属性名
		int attributeIndex = 0;
		for(Iterator<Cell> iter = responders.getRow(0).cellIterator(); iter.hasNext(); ) {
			Cell c = iter.next();
			if(c != null && !StringUtils.isBlank(c.getStringCellValue())) {
				attributeIndex++;
			}
		}
		int index = 0;
		for(Iterator<Row> rowIter = responders.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(index == 0) {
				int _index = 1;
				for(Iterator<Cell> iter = row.cellIterator(); iter.hasNext();) {
					Cell c = iter.next();
					logger.info(_index + "." + c);
					_index++;
				}
			} else {
				Cell firstCell = row.getCell(0);
				if(firstCell != null && !StringUtils.isBlank(firstCell.getStringCellValue())) {
					//Responder Object loop
					Responder responder = new Responder();
					for(int columnIndex = 0; columnIndex < attributeIndex; columnIndex++) {
						Cell column = row.getCell(columnIndex);
						switch (columnIndex) {
						case 0:
							logger.info(columnIndex+"-"+column.getStringCellValue());
							responder.setName(column.getStringCellValue());
							break;
						default:
							logger.info(columnIndex+"="+column.getStringCellValue());
							break;
						}
					}
					sql = "insert into responder(responder_id,responder_name,version) values(?,?,?)";
					long id = System.currentTimeMillis() + index * Math.round(System.currentTimeMillis()) * 1000;
					query.update(DbHelper.getConnection(), sql, id, responder.getName(), version);
				}
			}
			index++;
		}
	}
	
	void parseRequestions(Sheet requestions, long version) throws Exception {
		int index = 0;
		for(Iterator<Row> rowIter = requestions.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && index > 0) {
				logger.info(index + " : " + row.getCell(0));
			}
			index++;
		}
	}
	
	public static void main(String[] arg) throws Exception {
		QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
		final String name = System.getProperty("user.dir") + "/src/main/webapp/template.xls";
		System.out.println(name);
		InputStream excel = new FileInputStream(name);
		service.parseQuestionnaireTemplate(excel);
	}
}
