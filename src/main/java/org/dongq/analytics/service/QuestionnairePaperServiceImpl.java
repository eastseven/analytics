package org.dongq.analytics.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.KeyedHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dongq.analytics.model.Option;
import org.dongq.analytics.model.OptionGroup;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.QuestionGroup;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.model.ResponderProperty;
import org.dongq.analytics.utils.DbHelper;

public class QuestionnairePaperServiceImpl implements QuestionnairePaperService {

	final static Log logger = LogFactory.getLog(QuestionnairePaperServiceImpl.class);
	
	@Override
	public boolean hasAnswered(long responderId) {
		boolean answered = true;
		
		try {
			QueryRunner query = new QueryRunner();
			String sql = "select count(1) from questionnaire a where a.responder_id = " + responderId;
			List<Object> list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
			if(list.isEmpty()) answered = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return answered;
	}
	
	public Questionnaire getQuestionnaire(long id) {
		Questionnaire blankPaper = new Questionnaire();
		QueryRunner query = new QueryRunner();
		
		try {
			//Responder
			String sql = "select * from responder a where a.responder_id = " + id;
			Responder responder = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<Responder>() {
				@Override
				public Responder handle(ResultSet rs) throws SQLException {
					if(rs.next()) {
						Responder responder = new Responder();
						responder.setId(rs.getLong("responder_id"));
						responder.setName(rs.getString("responder_name"));
						responder.setVersion(rs.getLong("version"));
						return responder;
					}
					return null;
				}
			});
			
			long version = responder.getVersion();
			
			blankPaper.setResponder(responder);
			//Group
			blankPaper.setGroup(getQuestionGroupOfVersion(version));
			//Matrix
			blankPaper.setMatrix(getQuestionsOfVersion(version));
			//People
			blankPaper.setPeople(getRespondersOfVersion(version));
			//OptionGroup
			blankPaper.setOptionGroups(getResponderPropertyOfVersion(version, responder.getId()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return blankPaper;
	}

	@Override
	public Questionnaire getBlankQuestionnaire(long version) {
		Questionnaire blankPaper = new Questionnaire();
		try {
			//Group
			blankPaper.setGroup(getQuestionGroupOfVersion(version));
			//Matrix
			blankPaper.setMatrix(getQuestionsOfVersion(version));
			//People
			blankPaper.setPeople(getRespondersOfVersion(version));
			//Property
			blankPaper.setOptionGroups(getResponderPropertyOfVersion(version));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blankPaper;
	}
	
	List<OptionGroup> getResponderPropertyOfVersion(long version) throws SQLException {

		QueryRunner query = new QueryRunner();
		List<OptionGroup> optionGroups = new ArrayList<OptionGroup>();
		String sql = "select responder_property_key from responder_property where version = " + version + " group by responder_property_key";
		List<Object> list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
		logger.debug(list.size() + ":" + sql);
		for(Object o : list) {
			OptionGroup e = new OptionGroup();
			e.setName(o.toString());
			e.setVersion(version);
			sql = "select a.responder_property_id,a.responder_property_display,a.responder_property_value ";
			sql += " from responder_property a "; 
			sql += "where a.version = ? and a.responder_property_key = ?";
			List<Option> options = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Option>>() {
				@Override
				public List<Option> handle(ResultSet rs) throws SQLException {
					List<Option> list = new ArrayList<Option>();
					while(rs.next()) {
						Option e = new Option();
						e.setId(rs.getLong("responder_property_id"));
						e.setDisplay(rs.getString("responder_property_display"));
						e.setValue(String.valueOf(rs.getInt("responder_property_value")));
						list.add(e);
					}
					return list;
				}
			}, version, e.getName());
			logger.debug(options.size()+":"+sql);
			if(options.isEmpty()) continue;
			e.getOptions().addAll(options);
			logger.debug("getQuestionnaire:"+e);
			optionGroups.add(e);
		}
		return optionGroups;
	
	}
	
	List<OptionGroup> getResponderPropertyOfVersion(long version, long responderId) throws SQLException {
		QueryRunner query = new QueryRunner();
		List<OptionGroup> optionGroups = new ArrayList<OptionGroup>();
		String sql = "select responder_property_key from responder_property where version = " + version + " group by responder_property_key";
		List<Object> list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
		logger.debug(list.size() + ":" + sql);
		for(Object o : list) {
			OptionGroup e = new OptionGroup();
			e.setName(o.toString());
			e.setVersion(version);
			sql = "select a.responder_property_id,a.responder_property_display,a.responder_property_value,b.responder_property_id selected ";
			sql += " from responder_property a left join (select t.responder_property_id from responder_properties t where t.responder_id = ?) b on a.responder_property_id = b.responder_property_id "; 
			sql += "where a.version = ? and a.responder_property_key = ?";
			List<Option> options = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Option>>() {
				@Override
				public List<Option> handle(ResultSet rs) throws SQLException {
					List<Option> list = new ArrayList<Option>();
					while(rs.next()) {
						Option e = new Option();
						e.setId(rs.getLong("responder_property_id"));
						e.setDisplay(rs.getString("responder_property_display"));
						e.setValue(String.valueOf(rs.getInt("responder_property_value")));
						boolean selected = rs.getObject("selected") != null;
						e.setSelected(selected);
						list.add(e);
					}
					return list;
				}
			}, responderId, version, e.getName());
			logger.debug(options.size()+":"+sql);
			if(options.isEmpty()) continue;
			e.getOptions().addAll(options);
			logger.debug("getQuestionnaire:"+e);
			optionGroups.add(e);
		}
		return optionGroups;
	}
	
	List<Responder> getRespondersOfVersion(long version) throws SQLException {
		List<Responder> list = new ArrayList<Responder>();
		String sql = "select * from responder a where a.version = " + version;
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Responder>>() {
			@Override
			public List<Responder> handle(ResultSet rs) throws SQLException {
				List<Responder> list = new ArrayList<Responder>();
				while(rs.next()) {
					Responder e = new Responder();
					e.setId(rs.getLong("responder_id"));
					e.setName(rs.getString("responder_name"));
					e.setVersion(rs.getLong("version"));
					list.add(e);
				}
				return list;
			}
		});
		return list;
	}
	
	List<QuestionGroup> getQuestionGroupOfVersion(long version) throws SQLException {
		List<QuestionGroup> list = new ArrayList<QuestionGroup>();
		String sql = "select a.title, a.option_group_id from question a where a.version = " + version + " and a.type = " + Question.TYPE_NORMAL + " group by a.title, a.option_group_id";
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<QuestionGroup>>() {
			@Override
			public List<QuestionGroup> handle(ResultSet rs) throws SQLException {
				List<QuestionGroup> list = new ArrayList<QuestionGroup>();
				while(rs.next()) {
					QuestionGroup e = new QuestionGroup();
					e.setTitle(rs.getString("title"));
					e.setId(rs.getLong("option_group_id"));
					e.setOptions(getOptionsForQuestion(e.getId()));
					e.setQuestions(getQuestionsOfOptionGroupId(e.getId()));
					list.add(e);
				}
				return list;
			}
			
		});
		
		return list;
	}
	
	List<Question> getQuestionsOfVersion(long version) throws SQLException {
		List<Question> list = new ArrayList<Question>();
		String sql = "select * from question a where a.version = " + version + " and a.type = " + Question.TYPE_MATRIX;
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Question>>() {
			@Override
			public List<Question> handle(ResultSet rs) throws SQLException {
				List<Question> list = new ArrayList<Question>();
				while(rs.next()) {
					Question e = new Question();
					e.setId(rs.getLong("question_id"));
					e.setTitle(rs.getString("title"));
					e.setContent(rs.getString("content"));
					e.setOptionId(rs.getLong("option_group_id"));
					e.setVersion(rs.getLong("version"));
					e.setType(rs.getInt("type"));
					logger.debug(e);
					list.add(e);
				}
				return list;
			}
			
		});
		
		return list;
	}
	
	List<Question> getQuestionsOfOptionGroupId(long optionGroupId) throws SQLException {

		List<Question> list = new ArrayList<Question>();
		String sql = "select * from question a where a.option_group_id = " + optionGroupId;
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Question>>() {
			@Override
			public List<Question> handle(ResultSet rs) throws SQLException {
				List<Question> list = new ArrayList<Question>();
				while(rs.next()) {
					Question e = new Question();
					e.setId(rs.getLong("question_id"));
					e.setTitle(rs.getString("title"));
					e.setContent(rs.getString("content"));
					e.setOptionId(rs.getLong("option_group_id"));
					e.setVersion(rs.getLong("version"));
					e.setType(rs.getInt("type"));
					e.setOptions(getOptionsForQuestion(e.getOptionId()));
					list.add(e);
				}
				return list;
			}
			
		});
		
		return list;
	
	}
	
	List<Option> getOptionsForQuestion(long id) throws SQLException {
		List<Option> list = new ArrayList<Option>();
		String sql = "select * from question_option a where a.option_group_id = " + id;
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Option>>() {
			@Override
			public List<Option> handle(ResultSet rs) throws SQLException {
				List<Option> list = new ArrayList<Option>();
				while(rs.next()) {
					Option e = new Option();
					e.setId(rs.getLong("option_group_id"));
					e.setKey(rs.getInt("option_key"));
					e.setValue(rs.getString("option_value"));
					e.setVersion(rs.getLong("version"));
					list.add(e);
				}
				return list;
			}
			
		});
		return list;
	}
	
	public boolean saveQuestionnairePaper(QuestionnairePaper paper) {
		boolean bln = false;
		
		return bln;
	}

	@Override
	public boolean saveQuestionnairePaper(Responder responder, Map<String, Object> answer) {
		boolean bln = false;
		
		final String replacement     = "";
		final String prefix_question = "question_";
		final String prefix_matrix   = "matrix_";
		final String prefix_property = "property_";
		final long finishTime = System.currentTimeMillis();
		QueryRunner query = new QueryRunner();
		try {
			
			if(responder.getId() == 0) {
				//TODO add responder object to the database and generate a responder id
				logger.debug(prefix_property);
			}
			
			long responderId = responder.getId();
			long version = responder.getVersion();
			Set<String> keySet = answer.keySet();
			List<QuestionnairePaper> list = new ArrayList<QuestionnairePaper>();
			List<ResponderProperty> properties = new ArrayList<ResponderProperty>();
			for(String key : keySet) {
				String value = (String)answer.get(key);
				if(key.startsWith(prefix_question)) {
					long questionId = Long.valueOf(key.replaceAll(prefix_question, replacement));
					long optionKey = Long.valueOf(value);
					QuestionnairePaper row = new QuestionnairePaper();
					row.setResponderId(responderId);
					row.setQuestionId(questionId);
					row.setOptionKey(optionKey);
					row.setType(Question.TYPE_NORMAL);
					row.setVersion(version);
					row.setFinishTime(finishTime);
					logger.debug("Normal: "+row);
					list.add(row);
				}
				if(key.startsWith(prefix_matrix)) {
					long questionId = Long.valueOf(key.replaceAll(prefix_matrix, replacement));
					String[] values = value.split(",");
					for(String v : values) {
						if(StringUtils.isBlank(v)) continue;
						long optionKey = Long.valueOf(v);
						QuestionnairePaper row = new QuestionnairePaper();
						row.setResponderId(responderId);
						row.setQuestionId(questionId);
						row.setOptionKey(optionKey);
						row.setType(Question.TYPE_MATRIX);
						row.setVersion(version);
						row.setFinishTime(finishTime);
						logger.debug("Matrix: "+row);
						list.add(row);
					}
				}
				if(key.startsWith(prefix_property)) {
					long responderPropertyId = Long.valueOf(value);
					ResponderProperty e = new ResponderProperty(responderPropertyId);
					properties.add(e);
				}
			}
			
			if(!list.isEmpty()) {
				Object[][] params = new Object[list.size()][6];
				int index = 0;
				for(QuestionnairePaper e : list) {
					params[index][0] = e.getResponderId();
					params[index][1] = e.getQuestionId();
					params[index][2] = e.getType();
					params[index][3] = e.getOptionKey();
					params[index][4] = e.getVersion();
					params[index][5] = e.getFinishTime();
					index++;
				}
				final String insert = "insert into questionnaire values(?,?,?,?,?,?)";
				int[] records = query.batch(DbHelper.getConnection(), insert, params);
				logger.debug("questionnaire records : "+records.length);
			}
			
			//responder property
			if(!properties.isEmpty()) {
				Connection conn = DbHelper.getConnection();
				conn.setAutoCommit(false);
				final String delete = "delete from responder_properties where responder_id = "+responderId+" and version = " + version;
				int deleteRecord = query.update(DbHelper.getConnection(), delete);
				logger.debug(deleteRecord+":"+delete);
				
				int index = 0;
				Object[][] p = new Object[properties.size()][3];
				final String insert = "insert into responder_properties(responder_id,responder_property_id,version) values(?,?,?)";
				for(ResponderProperty e : properties) {
					p[index][0] = responderId;
					p[index][1] = e.getId();
					p[index][2] = version;
					logger.debug("responder property records insert : responderId="+responderId+",propertyId="+e.getId()+",version="+version);
					index++;
				}
				int[] records = query.batch(conn, insert, p);
				logger.debug(records.length);
				conn.commit();
				conn.close();
			}
			
			bln = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			parseResponderProperty(responderProperty, version);
			
			//｛参与调查的人员信息｝
			Sheet responders = workbook.getSheetAt(1);
			logger.info(responders.getSheetName());
			parseResponders(responders, version);
			
			//{问卷题目}
			Sheet requestions = workbook.getSheetAt(2);
			logger.info(requestions.getSheetName());
			parseRequestions(requestions, version);
			
			//{矩阵题}
			Sheet matrix = workbook.getSheetAt(3);
			logger.info(matrix.getSheetName());
			parseRequestionsOfMatrix(matrix, version);
			
			bln = true;
		} catch (Exception e) {
			e.printStackTrace();
			// clear data for version
			clearDataForVersion(version);
		}
		
		return bln;
	}
	
	void clearDataForVersion(final long version) {
		Connection conn = DbHelper.getConnection();
		QueryRunner query = new QueryRunner();
		try {
			final String[] sqls = {"questionnaire", "question", "question_option", "responder_properties", "responder", "responder_property"};
			for(String sql : sqls) {
				String delete = "delete from "+sql+" where version = " + version;
				int count = query.update(conn, delete);
				logger.debug(count+":"+delete);
			}
			conn.close();
			logger.info("clear data for version complete...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	synchronized void parseResponderProperty(Sheet responderProperty, final long version) throws SQLException {
		int index = 0;
		QueryRunner query = new QueryRunner();
		for(Iterator<Row> rowIter = responderProperty.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			Cell cell = row.getCell(0);
			if(cell != null && !StringUtils.isBlank(toConvert(cell)) && index > 0) {
				logger.debug(index + " : " + row.getCell(0));
				//属性名称
				
				if(row.getCell(1) != null && !StringUtils.isBlank(toConvert(row.getCell(1)))) {
					String name = toConvert(row.getCell(0));
					//选项值
					int value = 1;
					int columnIndex = 1;
					for(Iterator<Cell> cellIter = row.cellIterator(); cellIter.hasNext();) {
						cell = cellIter.next();
						String textValue = toConvert(cell);
						if(!StringUtils.isBlank(textValue) && columnIndex > 1) {
							ResponderProperty p = new ResponderProperty();
							p.setId(System.currentTimeMillis());
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							p.setDisplay(textValue);
							p.setName(name);
							p.setValue(value);
							p.setVersion(version);
							
							final String sql = "insert into responder_property values(?,?,?,?,?)";
							int record = query.update(DbHelper.getConnection(), sql, p.getId(), p.getName(), p.getDisplay(), p.getValue(), p.getVersion());
							logger.debug(sql+" : "+record);
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
	
	synchronized void parseResponders(Sheet responders, final long version) throws Exception {
		QueryRunner query = new QueryRunner();
		String sql = "select * from responder_property where version = " + version;
		List<ResponderProperty> properties = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<ResponderProperty>>() {
			@Override
			public List<ResponderProperty> handle(ResultSet rs) throws SQLException {
				List<ResponderProperty> list = new ArrayList<ResponderProperty>();
				while(rs.next()) {
					ResponderProperty e = new ResponderProperty(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getLong(5));
					list.add(e);
					//logger.debug("parseResponders:"+e);
				}
				return list;
			}
		});
		logger.debug("parseResponders:"+properties.size());

		//取列数
		int colnum = 0;
		Row firstRow = responders.getRow(0);
		if(firstRow != null) {
			for(Iterator<Cell> iter = firstRow.cellIterator(); iter.hasNext();) {
				Cell cell = iter.next();
				String value = toConvert(cell);
				if(StringUtils.isBlank(value)) break;
				colnum++;
			}
		}
		logger.debug("parseResponders colnum :"+colnum);
		//取行数
		int rownum = 0;
		for(Iterator<Row> iter = responders.iterator(); iter.hasNext();) {
			Row row = iter.next();
			if(row == null) continue;
			if(row.getCell(0) == null) continue;
			if(StringUtils.isBlank(toConvert(row.getCell(0)))) continue;
			rownum++;
		}
		logger.debug("parseResponders rownum :"+rownum);
		//转换成数组
		Object[][] matrix = new Object[rownum][colnum];
		for(int rowIndex = 0; rowIndex < rownum; rowIndex++) {
			Row row = responders.getRow(rowIndex);
			for(int colIndex = 0; colIndex < colnum; colIndex++) {
				Cell cell = row.getCell(colIndex);
				String value = toConvert(cell);
				matrix[rowIndex][colIndex] = value;
				logger.debug("["+rowIndex+"]["+colIndex+"]="+value);
			}
		}
		logger.debug("parseResponders matrix complete");
		//
		for(int rowIndex = 0; rowIndex < rownum; rowIndex++) {
			if(rowIndex != 0) {
				Responder responder = new Responder();
				responder.setVersion(version);
				responder.setId(System.currentTimeMillis());
				Thread.sleep(1);
				for(int colIndex = 0; colIndex < colnum; colIndex++) {
					String key = matrix[0][colIndex].toString();
					String display = matrix[rowIndex][colIndex].toString();
					if(colIndex == 0) {
						responder.setName(matrix[rowIndex][colIndex].toString());
					} else {
						if(StringUtils.isBlank(key)) continue;
						if(StringUtils.isBlank(display)) continue;
						sql = "select * from responder_property where responder_property_key = '"+key+"' and responder_property_display = '"+display+"' and version = " + version;
						List<ResponderProperty> list = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<ResponderProperty>>() {
							@Override
							public List<ResponderProperty> handle(ResultSet rs) throws SQLException {
								List<ResponderProperty> list = new ArrayList<ResponderProperty>();
								while(rs.next()) {
									ResponderProperty e = new ResponderProperty();
									e.setId(rs.getLong("responder_property_id"));
									e.setDisplay(rs.getString("responder_property_display"));
									e.setName(rs.getString("responder_property_key"));
									e.setValue(rs.getInt("responder_property_value"));
									e.setVersion(rs.getLong("version"));
									list.add(e);
								}
								return list;
							}
						});
						if(list.isEmpty()) continue;
						logger.debug("parseResponders:"+list.size()+":"+sql);
						responder.getProperties().addAll(list);
					}
				}
				logger.debug("parseResponders:"+responder);
				//save responder
				saveResponder(responder, query, DbHelper.getConnection());
			}
		}
	}
	
	void parseRequestions(Sheet requestions, final long version) throws Exception {
		int index = 0;
		QueryRunner query = new QueryRunner();
		for(Iterator<Row> rowIter = requestions.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && !StringUtils.isBlank(toConvert(row.getCell(0))) && index > 0) {
				logger.debug(index + " : " + row.getCell(0));
				String title = toConvert(row.getCell(0));
				String optionString = toConvert(row.getCell(1));
				long optionGroupId = parseOptions(optionString, version).get(0).getId();
				
				//迭代小题
				int columnIndex = 0;
				for(Iterator<Cell> iter = row.cellIterator(); iter.hasNext();) {
					Cell cell = iter.next();
					
					if(cell == null) continue;
					if(StringUtils.isBlank(toConvert(cell))) continue;
					String content = toConvert(cell);
					if(columnIndex > 1) {
						Question q = new Question();
						q.setId(System.currentTimeMillis());
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						q.setContent(content);
						q.setOptionId(optionGroupId);
						q.setTitle(title);
						q.setVersion(version);
						q.setType(Question.TYPE_NORMAL);
						saveQuestion(q, query);
						logger.info(q);
					}
					columnIndex++;
				}
			}
			index++;
		}
	}
	
	void parseRequestionsOfMatrix(Sheet matrix, final long version) throws Exception {
		int index = 0;
		for(Iterator<Row> rowIter = matrix.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && !StringUtils.isBlank(toConvert(row.getCell(0))) && index > 0) {
				Question q = new Question();
				q.setId(System.currentTimeMillis());
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				q.setTitle(toConvert(row.getCell(0)));
				q.setVersion(version);
				q.setType(Question.TYPE_MATRIX);
				saveQuestion(q, new QueryRunner());
				logger.info(q);
			}
			
			index++;
		}
	}
	
	String toConvert(Cell cell) {
		String value = "";
		if(cell == null) return value;
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC :
				value = String.valueOf((int)cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING :
				value = cell.getStringCellValue();
				break;
			default:
				value = "";
				break;
		}
		
		return value;
	}
	
	List<Option> parseOptions(String optionString, long version) throws Exception {
		long optionGroupId = System.currentTimeMillis();
		List<Option> list = new ArrayList<Option>();
		
		logger.debug(optionString);
		QueryRunner query = new QueryRunner();
		String insert = "insert into question_option values(?,?,?,?)";
		String[] array = optionString.split(",");
		for(String e : array) {
			String[] opt = e.split("=");
			Option o = new Option();
			o.setId(optionGroupId);
			o.setVersion(version);
			o.setKey(Integer.parseInt(opt[0]));
			o.setValue(opt[1]);
			list.add(o);
			
			logger.debug(o);
			query.update(DbHelper.getConnection(), insert, o.getId(), o.getKey(), o.getValue(), o.getVersion());
		}
		
		return list;
	}
	
	int saveQuestion(Question q, QueryRunner query) throws Exception {
		String insert = "insert into question values(?,?,?,?,?,?)";
		int update = query.update(DbHelper.getConnection(), insert, q.getId(), q.getTitle(), q.getContent(), q.getOptionId(), q.getVersion(), q.getType());
		return update;
	}
	
	void saveResponder(Responder r, QueryRunner query, Connection conn) throws Exception {
		final String insertResponder = "insert into responder values(?,?,?)";
		final String insertResponderProperty = "insert into responder_properties(responder_id,responder_property_id,version) values(?,?,?)";
		
		int responderRecord = query.update(DbHelper.getConnection(), insertResponder, r.getId(), r.getName(), r.getVersion());
		logger.debug(responderRecord+":"+r);
		if(responderRecord != 0) {
			Set<ResponderProperty> p = r.getProperties();
			Object[][] params = new Object[p.size()][3];
			int rowIndex = 0;
			for(Iterator<ResponderProperty> iter = p.iterator(); iter.hasNext();) {
				ResponderProperty rp = iter.next();
				params[rowIndex][0] = r.getId();
				params[rowIndex][1] = rp.getId();
				params[rowIndex][2] = r.getVersion();
				rowIndex++;
			}
			int[] responderPropertiesRecord = query.batch(DbHelper.getConnection(), insertResponderProperty, params);
			logger.debug("responderPropertiesRecord:"+responderPropertiesRecord.length);
		}
	}
	
	@Override
	public Object[][] calculate(long version) {
		Object[][] data = null;
		QueryRunner query = new QueryRunner();
		try {
			Questionnaire template = getBlankQuestionnaire(version);
			List<Question> questions = new ArrayList<Question>();
			for(QuestionGroup g : template.getGroup()) {
				questions.addAll(g.getQuestions());
			}
			List<OptionGroup> options = template.getOptionGroups();
			
			//get responder of question
			String sql = "select a.responder_id from questionnaire a where a.type = " + Question.TYPE_NORMAL + " and a.version = " + version + " group by a.responder_id";
			List<Object> _list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
			String ids = "";
			for(Object e : _list) ids += "," + e;
			ids = ids.replaceFirst(",", "");
			sql = "select a.responder_id, a.responder_name from responder a where a.responder_id in ("+ids+") order by a.responder_id asc";
			List<Object[]> list = query.query(DbHelper.getConnection(), sql, new ArrayListHandler());
			logger.debug(list.size()+":"+sql);

			//get property of responder
			
			
			int rowSize = list.size() + 1;
			int columnSizeOfQuestion = questions.size();
			int columnSizeOfProperty = options.size();
			int columnSize = columnSizeOfQuestion + 1;
			data = new Object[rowSize][questions.size() + 1 + options.size()];
			
			data[0][0] = "";
			//表头 : 试题
			for(int columnIndex = 1; columnIndex < columnSize; columnIndex++) {
				Question q = questions.get(columnIndex-1);
				data[0][columnIndex] = q;
			}
			//表头 : 受访者属性
			for(int columnIndex = 0; columnIndex < columnSizeOfProperty; columnIndex++) {
				OptionGroup o = options.get(columnIndex);
				data[0][columnSize + columnIndex] = o;
			}
			
			for(int rowIndex = 1; rowIndex < rowSize; rowIndex++) {
				Object[] responderAttr = (Object[])list.get(rowIndex-1);
				Long responderId = (Long)responderAttr[0];
				String responderName = (String)responderAttr[1];
				sql = "select a.question_id, a.option_key from questionnaire a where a.type = "+Question.TYPE_NORMAL+" and a.responder_id = " + responderId;
				logger.debug(sql);
				Map<Object, Map<String, Object>> map = query.query(DbHelper.getConnection(), sql, new KeyedHandler());
				if(map.isEmpty()) continue;
				data[rowIndex][0] = responderName;
				for(int columnIndex = 1; columnIndex < columnSize; columnIndex++) {
					Question ref = (Question)data[0][columnIndex];
					long key = ref.getId();
					Map<String, Object> value = map.get(key);
					data[rowIndex][columnIndex] = value.get("OPTION_KEY");
				}
			}
			
			for(int rowIndex = 1; rowIndex < rowSize; rowIndex++) {
				Object[] responderAttr = (Object[])list.get(rowIndex-1);
				Long responderId = (Long)responderAttr[0];
				
				for(int columnIndex = 0; columnIndex < columnSizeOfProperty; columnIndex++) {
					OptionGroup ref = (OptionGroup)data[0][columnSize + columnIndex];
					String key = ref.getName();
					sql = "select responder_property_key,responder_property_value from responder_property a, responder_properties b where a.responder_property_id = b.responder_property_id";
					sql += " and b.responder_id = ? and a.responder_property_key = ?";
					Map<Object, Map<String, Object>> map = query.query(DbHelper.getConnection(), sql, new KeyedHandler(), responderId, key);
					Map<String, Object> value = map.get(key);
					if(value == null) continue;
					data[rowIndex][columnSize + columnIndex] = value.get("responder_property_value".toUpperCase());
					logger.debug("人员属性["+rowIndex+"]["+(columnSize + columnIndex)+"]:" + value);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@Override
	public Map<Object, Object[][]> calculateForMatrix(long version) {
		Map<Object, Object[][]> map = new HashMap<Object, Object[][]>();
		
		try {
			
			List<Responder> people = getRespondersOfVersion(version);
			
			List<Question> matrixQuestion = getQuestionsOfVersion(version);
			for(Question question : matrixQuestion) {
				question.setPeople(people);
				map.put(question, calculateForMatrix(question));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	Object[][] calculateForMatrix(Question question) throws Exception {
		List<Responder> people = question.getPeople();
		int rowSize = people.size() + 1;
		int columnSize = rowSize;
		Object[][] data = new Object[rowSize][columnSize];
		
		QueryRunner query = new QueryRunner();
		Connection conn = DbHelper.getConnection();
		
		//第一行赋值
		for(int index = 1; index < rowSize; index++) {
			data[0][index] = people.get(index - 1);
		}

		//第一列赋值
		for(int index = 1; index < columnSize; index++) {
			Responder responder = people.get(index - 1);
			data[index][0] = responder;
		}
		
		for(int rowIndex = 1; rowIndex < columnSize; rowIndex++) {
			Responder responder = people.get(rowIndex - 1);
			
			final String sql = "select a.option_key from questionnaire a where a.type = " + Question.TYPE_MATRIX + " and a.question_id = " + question.getId() + " and a.responder_id = " + responder.getId();
			List<Object> list = query.query(conn, sql, new ColumnListHandler());
			logger.debug(sql);
			logger.debug(list);
			for(int columnIndex = 1; columnIndex < columnSize; columnIndex++) {
				Responder person = (Responder)data[0][columnIndex];
				for(Object object : list) {
					if(object.equals(person.getId())) {
						data[rowIndex][columnIndex] = 1;
					}
					logger.debug("pId:"+person.getId()+","+object+"["+rowIndex+"]["+columnIndex+"]="+data[rowIndex][columnIndex]);
				}
			}
		}
		
		if(logger.isDebugEnabled()) {
			int i = 0;
			for(Object[] o : data) {
				int j = 0;
				for(Object e : o) {
					System.out.println("matrix_data["+i+"]["+j+"] : " + e);
					j++;
				}
				i++;
			}
		}
		
		return data;
	}
	
	@Override
	public Workbook generateExcelForQuestionnaire(long version) {
		Object[][] normalQuestion = calculate(version);
		Map<Object, Object[][]> matrixQuestion = calculateForMatrix(version);
		return generateExcelForQuestionnaire(normalQuestion, matrixQuestion);
	}
	
	@Override
	public Workbook generateExcelForQuestionnaire(Object[][] normalQuestion, Map<Object, Object[][]> matrixQuestion) {
		Workbook excel = null;
		try {
			
			Workbook workbook = new HSSFWorkbook();
			Sheet normal = workbook.createSheet("normal");
			int _rowIndex = 0;
			//普通卷
			for(Object[] rowData : normalQuestion) {
				Row row = normal.createRow(_rowIndex);
				int colIndex = 0;
				for(Object colData : rowData) {
					logger.debug(colData);
					if(colData == null) continue;
					Cell cell = row.createCell(colIndex);
					Object data = normalQuestion[_rowIndex][colIndex];
					if(data == null) continue;
					if (data instanceof Question) {
						Question q = (Question) data;
						cell.setCellValue(q.getContent());
					} else if(data instanceof OptionGroup) {
						OptionGroup o = (OptionGroup) data;
						cell.setCellValue(o.getName());
					} else {
						cell.setCellValue(data.toString());
					}
					colIndex++;
				}
				
				_rowIndex++;
			}
			
			//矩阵题
			for(Iterator<Object> iter = matrixQuestion.keySet().iterator(); iter.hasNext();) {
				Object key = iter.next();
				Sheet sheet = null;
				if (key instanceof Question) {
					Question q = (Question) key;
					logger.debug(q);
					sheet = workbook.createSheet(q.getTitle());
				}
				Object[][] matrixData = matrixQuestion.get(key);
				int rownum = matrixData.length;
				int colnum = rownum;
				for(int rowIndex = 0; rowIndex < rownum; rowIndex++) {
					Row row = sheet.createRow(rowIndex);
					for(int colIndex = 0; colIndex < colnum; colIndex++) {
						Cell cell = row.createCell(colIndex);
						Object data = matrixData[rowIndex][colIndex];
						if(data != null) {
							if (data instanceof Responder) {
								Responder r = (Responder) data;
								cell.setCellValue(r.getName());
							} else {
								cell.setCellValue(data.toString());
							}
						} else {
							cell.setCellValue("");
						}
					}
				}
				
			}
			excel = workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return excel;
	}
	
	public static void main(String[] arg) throws Exception {
		QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
		
		Connection conn = DbHelper.getConnection();
		QueryRunner query = new QueryRunner();
		String sql = "";
		
		String name = System.getProperty("user.dir") + "/src/main/webapp/template.xls";
		name = "/Users/eastseven/Desktop/template_1.xls";
		System.out.println(name);
		InputStream excel = new FileInputStream(name);
		service.parseQuestionnaireTemplate(excel);
		
//		sql = "select a.version from question a";
//		@SuppressWarnings("rawtypes")
//		Map map = query.query(conn, sql, new KeyedHandler());
//		Long version = (Long)map.keySet().iterator().next();
//		long version = 1316957340854l;
//		System.out.println(version);
//		Object[][] result = service.calculate(version);
//		for(int i = 0; i < result.length; i++) {
//			String row = "";
//			for(int j = 0; j < result[i].length; j++) {
//				row += result[i][j] + ",";
//			}
//			System.out.println(row);
//		}
//		
//		Map<Object, Object[][]> matrix = service.calculateForMatrix(version);
//		for(Object[][] data : matrix.values()) {
//			for(int i = 0; i < data.length; i++) {
//				for(int j = 0; j < data.length; j++) {
//					System.out.println("data["+i+"]["+j+"] : " + data[i][j]);
//				}
//			}
//			System.out.println("\n\n\n\n\n\n\n");
//		}
//		
//		FileOutputStream fileOut = new FileOutputStream("workbook.xls");
//		Workbook wb = service.generateExcelForQuestionnaire(result, matrix);
//		wb.write(fileOut);
//	    fileOut.close();
	    System.out.println("done...");
	}
}
