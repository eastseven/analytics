package org.dongq.analytics.service;

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
		Connection conn = DbHelper.getConnection();
		QueryRunner queryRunner = new QueryRunner();
		
		try {
			String sql = "select * from responder a where a.responder_id = " + id;
			Responder responder = queryRunner.query(conn, sql, new ResultSetHandler<Responder>() {
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
			
			blankPaper.setResponder(responder);
			//Group
			blankPaper.setGroup(getQuestionGroupOfVersion(responder.getVersion()));
			//Matrix
			blankPaper.setMatrix(getQuestionsOfVersion(responder.getVersion()));
			//People
			blankPaper.setPeople(getRespondersOfVersion(responder.getVersion()));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blankPaper;
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
				logger.debug(records.length);
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
		}
		
		return bln;
	}
	
	void parseResponderProperty(Sheet responderProperty, long version) throws SQLException {
		int index = 0;
		QueryRunner query = new QueryRunner();
		Connection conn = DbHelper.getConnection();
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
							int record = query.update(conn, sql, p.getId(), p.getName(), p.getDisplay(), p.getValue(), p.getVersion());
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
		Connection conn = DbHelper.getConnection();
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
		logger.debug(list.size());
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
					logger.debug(_index + "." + c);
					_index++;
				}
			} else {
				Cell firstCell = row.getCell(0);
				if(firstCell != null && !StringUtils.isBlank(firstCell.getStringCellValue())) {
					//Responder Object loop
					Responder responder = new Responder();
					for(int columnIndex = 0; columnIndex < attributeIndex; columnIndex++) {
						Cell column = row.getCell(columnIndex);
						String textValue = toConvert(column);
						switch (columnIndex) {
						case 0:
							logger.info(columnIndex+"-"+textValue);
							responder.setName(textValue);
							break;
						default:
							logger.info(columnIndex+"="+textValue);
							break;
						}
					}
					sql = "insert into responder(responder_id,responder_name,version) values(?,?,?)";
					long id = System.currentTimeMillis();
					Thread.sleep(1);
					query.update(conn, sql, id, responder.getName(), version);
				}
			}
			index++;
		}
	}
	
	void parseRequestions(Sheet requestions, long version) throws Exception {
		int index = 0;
		QueryRunner query = new QueryRunner();
		for(Iterator<Row> rowIter = requestions.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && !StringUtils.isBlank(row.getCell(0).getStringCellValue()) && index > 0) {
				logger.debug(index + " : " + row.getCell(0));
				String title = row.getCell(0).getStringCellValue();
				String optionString = row.getCell(1).getStringCellValue();
				long optionGroupId = parseOptions(optionString, version).get(0).getId();
				
				//迭代小题
				int columnIndex = 0;
				for(Iterator<Cell> iter = row.cellIterator(); iter.hasNext();) {
					Cell cell = iter.next();
					
					if(cell == null) continue;
					if(StringUtils.isBlank(cell.getStringCellValue())) continue;
					
					if(columnIndex > 1) {
						Question q = new Question();
						q.setId(System.currentTimeMillis());
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						q.setContent(cell.getStringCellValue());
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
	
	void parseRequestionsOfMatrix(Sheet matrix, long version) throws Exception {
		int index = 0;
		for(Iterator<Row> rowIter = matrix.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && !StringUtils.isBlank(row.getCell(0).getStringCellValue()) && index > 0) {
				Question q = new Question();
				q.setId(System.currentTimeMillis());
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				q.setTitle(row.getCell(0).getStringCellValue());
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

			Connection conn = DbHelper.getConnection();
			
			//get responder of question
			String sql = "select a.responder_id from questionnaire a where a.type = " + Question.TYPE_NORMAL + " and a.version = " + version + " group by a.responder_id";
			logger.debug(sql);
			List<Object> list = query.query(conn, sql, new ColumnListHandler());

			int rowSize = list.size() + 1;
			int columnSize = questions.size() + 1;
			data = new Object[rowSize][columnSize];
			
			data[0][0] = "";
			//表头
			for(int columnIndex = 1; columnIndex < columnSize; columnIndex++) {
				Question q = questions.get(columnIndex-1);
				data[0][columnIndex] = q;
			}
			
			for(int rowIndex = 1; rowIndex < rowSize; rowIndex++) {
				Long responderId = (Long)list.get(rowIndex-1);
				sql = "select a.question_id, a.option_key from questionnaire a where a.type = "+Question.TYPE_NORMAL+" and a.responder_id = " + responderId;
				logger.debug(sql);
				Map<Object, Map<String, Object>> map = query.query(conn, sql, new KeyedHandler());
				if(map.isEmpty()) continue;
				data[rowIndex][0] = responderId;
				for(int columnIndex = 1; columnIndex < columnSize; columnIndex++) {
					Question ref = (Question)data[0][columnIndex];
					long key = ref.getId();
					Map<String, Object> value = map.get(key);
					data[rowIndex][columnIndex] = value.get("OPTION_KEY");
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
		//OutputStream out ;
		try {
			
			Workbook workbook = new HSSFWorkbook();
			Sheet normal = workbook.createSheet("normal");
			int _rowIndex = 0;
			for(Object[] rowData : normalQuestion) {
				Row row = normal.createRow(_rowIndex);
				int colIndex = 0;
				for(Object colData : rowData) {
					logger.debug(colData);
					Cell cell = row.createCell(colIndex);
					Object data = normalQuestion[_rowIndex][colIndex];
					if (data instanceof Question) {
						Question q = (Question) data;
						cell.setCellValue(q.getContent());
					} else {
						cell.setCellValue(data.toString());
					}
					colIndex++;
				}
				
				_rowIndex++;
			}
			
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
//		final String name = System.getProperty("user.dir") + "/src/main/webapp/template.xls";
//		System.out.println(name);
//		InputStream excel = new FileInputStream(name);
//		service.parseQuestionnaireTemplate(excel);
		QueryRunner query = new QueryRunner();
		String sql = "select a.version from question a";
		@SuppressWarnings("rawtypes")
		Map map = query.query(DbHelper.getConnection(), sql, new KeyedHandler());
		Long version = (Long)map.keySet().iterator().next();
		System.out.println(version);
		Object[][] result = service.calculate(version);
		for(int i = 0; i < result.length; i++) {
			String row = "";
			for(int j = 0; j < result[i].length; j++) {
				row += result[i][j] + ",";
			}
			System.out.println(row);
		}
		
		Map<Object, Object[][]> matrix = service.calculateForMatrix(version);
		for(Object[][] data : matrix.values()) {
			for(int i = 0; i < data.length; i++) {
				for(int j = 0; j < data.length; j++) {
					System.out.println("data["+i+"]["+j+"] : " + data[i][j]);
				}
			}
			System.out.println("\n\n\n\n\n\n\n");
		}
		
		FileOutputStream fileOut = new FileOutputStream("workbook.xls");
		Workbook wb = service.generateExcelForQuestionnaire(result, matrix);
		wb.write(fileOut);
	    fileOut.close();
	    System.out.println("done...");
	}
}
