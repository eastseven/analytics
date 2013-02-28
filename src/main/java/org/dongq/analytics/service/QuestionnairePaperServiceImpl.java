package org.dongq.analytics.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.KeyedHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.Frequency;
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
import org.dongq.analytics.model.QuestionnaireMatrixNet;
import org.dongq.analytics.model.QuestionnairePaper;
import org.dongq.analytics.model.Responder;
import org.dongq.analytics.model.ResponderProperty;
import org.dongq.analytics.utils.DbHelper;
import org.dongq.analytics.utils.UUIDGenerator;

public class QuestionnairePaperServiceImpl implements QuestionnairePaperService {

	final static Log logger = LogFactory.getLog(QuestionnairePaperServiceImpl.class);
	
	@Override
	public boolean hasAnswered(String responderId) {
		boolean answered = true;
		
		try {
			QueryRunner query = new QueryRunner();
			String sql = "select count(1) from questionnaire a where a.responder_id = " + responderId;
			List<Object> list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
			if(list.isEmpty()) answered = false;
			else if(list.get(0).equals(0)) answered = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return answered;
	}
	
	@Override
	public Responder login(String no, String pwd) {
		Responder responder = null;
		try {
			QueryRunner query = new QueryRunner();
			String sql = "select responder_id,responder_name,responder_no,responder_pwd,version from responder where responder_no = '"+no+"' and responder_pwd = '"+pwd+"'";
			logger.debug(sql);
			responder = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<Responder>() {

				@Override
				public Responder handle(ResultSet rs) throws SQLException {
					if(rs.next()) {
						Responder r = new Responder();
						r.setId(rs.getString("responder_id"));
						r.setName(rs.getString("responder_name"));
						r.setNo(rs.getString("responder_no"));
						r.setPwd(rs.getString("responder_pwd"));
						r.setVersion(rs.getLong("version"));
						return r;
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responder;
	}
	
	@Override
	public String getQuestionnaireTitle(Object version) {
		Connection conn = DbHelper.getConnection();
		PreparedStatement ps;
		String data = "";
		try {
			if(version == null) version = getOpenPaperVersion();
			String sql = "select version, title from questionnaire_title where version = " + version;
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {  
				//data = getDerbyClobContent(rs.getClob("title"));
				Clob clob = rs.getClob("title");
				data = clob.getSubString(1, (int)clob.length());
			}
			
			DbUtils.close(rs);
			DbUtils.close(ps);
			DbUtils.close(conn);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@Deprecated
	@SuppressWarnings("unused")
	private String getDerbyClobContent(Clob derbyClob) throws Exception {
		BufferedInputStream in = new BufferedInputStream(derbyClob.getAsciiStream());
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(bs);
		byte[] ioBuf = new byte[4096];
		int bytesRead;
		while ((bytesRead = in.read(ioBuf)) != -1) out.write(ioBuf, 0, bytesRead);
		out.close();
		in.close();
		return new String(bs.toString());
	}
	
	public Questionnaire getQuestionnaire(String id) {
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
						responder.setId(rs.getString("responder_id"));
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
			//MatrixNet
			blankPaper.setMatrixNet(getQuestionsOfVersion(version, Question.TYPE_MATRIX_NET, id));
			
			//People
			blankPaper.setPeople(getRespondersOfVersion(version, responder.getId()));
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
			//MatrixNet
			blankPaper.setMatrixNet(getQuestionsOfVersion(version, Question.TYPE_MATRIX_NET, null));
			//People
			blankPaper.setPeople(getRespondersOfVersion(version));
			//Property
			blankPaper.setOptionGroups(getResponderPropertyOfVersion(version));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return blankPaper;
	}
	
	public List<OptionGroup> getResponderPropertyOfVersion(long version) {
		List<OptionGroup> optionGroups = new ArrayList<OptionGroup>();

		QueryRunner query = new QueryRunner();
		String sql = "select responder_property_key from responder_property where version = " + version + " group by responder_property_key";
		List<Object> list;
		try {
			list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
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
							e.setId(rs.getString("responder_property_id"));
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
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return optionGroups;
	
	}
	
	List<OptionGroup> getResponderPropertyOfVersion(long version, String responderId) throws SQLException {
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
						e.setId(rs.getString("responder_property_id"));
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
	
	public List<Responder> getRespondersOfVersion(long version, String exceptSelf) {
		List<Responder> list = new ArrayList<Responder>();
		String sql = "select * from responder a where a.version = " + version;
		if(exceptSelf != null) sql += " and a.responder_id <> '" + exceptSelf + "'";
		logger.debug(sql);
		try {
			list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Responder>>() {
				@Override
				public List<Responder> handle(ResultSet rs) throws SQLException {
					List<Responder> list = new ArrayList<Responder>();
					while(rs.next()) {
						Responder e = new Responder();
						e.setId(rs.getString("responder_id"));
						e.setName(rs.getString("responder_name"));
						e.setVersion(rs.getLong("version"));
						list.add(e);
					}
					return list;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<Responder> getRespondersOfVersion(long version) {
		return getRespondersOfVersion(version, null);
	}
	
	Responder getResponder(String responderId) throws SQLException {
		Responder r = null;
		
		String sql = "select * from responder where responder_id = '" + responderId + "'";
		Map<String, Object> map = new QueryRunner().query(DbHelper.getConnection(), sql, new MapHandler());
		if(map != null && !map.isEmpty()) {
			r = new Responder();
			r.setId((String)map.get("responder_id".toUpperCase()));
			r.setName((String)map.get("responder_name".toUpperCase()));
			r.setVersion((Long)map.get("version".toUpperCase()));
		}
		
		return r;
	}
	
	public List<QuestionGroup> getQuestionGroupOfVersion(long version, int type) {
		List<QuestionGroup> list = new ArrayList<QuestionGroup>();
		try {
			String sql = "select a.title, a.option_group_id from question a where a.version = " + version + " and a.type = " + type + " group by a.title, a.option_group_id";
			
			
			if(type == Question.TYPE_MATRIX) {
				sql = "select a.title, a.question_id from question a where a.version = " + version + " and a.type = " + type ;
				list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<QuestionGroup>>() {
					@Override
					public List<QuestionGroup> handle(ResultSet rs) throws SQLException {
						List<QuestionGroup> list = new ArrayList<QuestionGroup>();
						while(rs.next()) {
							QuestionGroup e = new QuestionGroup();
							e.setTitle(rs.getString("title"));
							e.setId(rs.getString("question_id"));
							list.add(e);
						}
						return list;
					}
				});
			} else {
				list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<QuestionGroup>>() {
					@Override
					public List<QuestionGroup> handle(ResultSet rs) throws SQLException {
						List<QuestionGroup> list = new ArrayList<QuestionGroup>();
						while(rs.next()) {
							QuestionGroup e = new QuestionGroup();
							e.setTitle(rs.getString("title"));
							e.setId(rs.getString("option_group_id"));
							e.setOptions(getOptionsForQuestion(e.getId()));
							e.setQuestions(getQuestionsOfOptionGroupId(e.getId()));
							list.add(e);
						}
						return list;
					}
				});
			}
			logger.debug(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	List<QuestionGroup> getQuestionGroupOfVersion(long version) throws SQLException {
		return getQuestionGroupOfVersion(version, Question.TYPE_NORMAL);
	}
	
	List<Question> getQuestionsOfVersion(long version) throws SQLException {
		return getQuestionsOfVersion(version, Question.TYPE_MATRIX, null);
	}
	
	public List<Question> getQuestionsOfVersion(long version, int type, String responderId) throws SQLException {
		List<Question> list = new ArrayList<Question>();
		QueryRunner query = new QueryRunner();
		String sql = "select * from question a where a.version = " + version + " and a.type = " + type;
		logger.debug(sql);
		list = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Question>>() {
			@Override
			public List<Question> handle(ResultSet rs) throws SQLException {
				List<Question> list = new ArrayList<Question>();
				while(rs.next()) {
					Question e = new Question();
					e.setId(rs.getString("question_id"));
					e.setTitle(rs.getString("title"));
					e.setContent(rs.getString("content"));
					e.setOptionId(rs.getString("option_group_id"));
					e.setOptions(getOptionsForQuestion(e.getOptionId()));
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
	
	Question getQuestion(String questionId) throws SQLException {
		Question q = null;
		String sql = "select * from question a where a.question_id = '" + questionId + "'";
		Map<String, Object> map = new QueryRunner().query(DbHelper.getConnection(), sql, new MapHandler());
		if(map != null && !map.isEmpty()) {
			q = new Question();
			q.setContent((String)map.get("content"));
			q.setId((String)map.get("question_id"));
			q.setOptionId((String)map.get("option_group_id"));
			q.setOptions(getOptionsForQuestion(q.getOptionId()));
			q.setTitle((String)map.get("title"));
			q.setVersion((Long)map.get("version"));
			q.setType((Integer)map.get("type"));
		}
		return q;
	}
	
	List<Question> getQuestionsOfOptionGroupId(String optionGroupId) throws SQLException {

		List<Question> list = new ArrayList<Question>();
		String sql = "select * from question a where a.option_group_id = '" + optionGroupId+"'";
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Question>>() {
			@Override
			public List<Question> handle(ResultSet rs) throws SQLException {
				List<Question> list = new ArrayList<Question>();
				while(rs.next()) {
					Question e = new Question();
					e.setId(rs.getString("question_id"));
					e.setTitle(rs.getString("title"));
					e.setContent(rs.getString("content"));
					e.setOptionId(rs.getString("option_group_id"));
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
	
	List<Option> getOptionsForQuestion(String id) throws SQLException {
		List<Option> list = new ArrayList<Option>();
		String sql = "select * from question_option a where a.option_group_id = '" + id + "'";
		logger.debug(sql);
		list = new QueryRunner().query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Option>>() {
			@Override
			public List<Option> handle(ResultSet rs) throws SQLException {
				List<Option> list = new ArrayList<Option>();
				while(rs.next()) {
					Option e = new Option();
					e.setId(rs.getString("option_group_id"));
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
		
		final String prefix_matrixNet  = "matrixNet_";
		final String prefix_matrixPlus = "matrixPlus_";
		final String prefix_person     = "person";
		
		String type = TYPE_CLOSE;
		
		final long finishTime = System.currentTimeMillis();
		QueryRunner query = new QueryRunner();
		Connection conn = DbHelper.getConnection();
		try {
			
			if(responder.getId() == null) {
				logger.debug(prefix_property);
				responder.setId(UUIDGenerator.generateUUID());
				String add = "insert into responder(responder_id,responder_name,version) values(?,?,?)";
				query.update(conn, add, responder.getId(), responder.getName(), responder.getVersion());
				
				type = TYPE_OPEN;
			}
			
			String responderId = responder.getId();
			long version = responder.getVersion();
			Set<String> keySet = answer.keySet();
			List<QuestionnairePaper> list = new ArrayList<QuestionnairePaper>();
			List<ResponderProperty> properties = new ArrayList<ResponderProperty>();
			List<QuestionnaireMatrixNet> matrixNets = new ArrayList<QuestionnaireMatrixNet>();
			
			List<Responder> personList = new ArrayList<Responder>();
			for(String key : keySet) {
				String value = (String)answer.get(key);
				if(key.startsWith(prefix_person)) {
					String personName = value;
					Responder e = new Responder();
					
					e.setId(UUIDGenerator.generateUUID());
					e.setName(personName);
					e.setPersonNo(key);
					e.setPid(responderId);
					e.setVersion(version);
					personList.add(e);
					saveResponder(e);
				}
			}
			
			for(String key : keySet) {
				String value = (String)answer.get(key);
				if(key.startsWith(prefix_question)) {
					String questionId = key.replaceAll(prefix_question, replacement);
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
				
				if(key.startsWith(prefix_matrixPlus)) {
					String[] persons = key.replaceAll(prefix_matrixPlus, replacement).split("_");
					String personRow = type.equals(TYPE_CLOSE) ? persons[0] : getPersonId(version, persons[0], responderId);
					String personCol = type.equals(TYPE_CLOSE) ? persons[1] : getPersonId(version, persons[1], responderId);
					long optionKey = Long.valueOf(value);
					QuestionnairePaper row = new QuestionnairePaper();
					row.setResponderId(responderId);
					row.setOptionKey(optionKey);
					row.setType(Question.TYPE_MATRIX_PLUS);
					row.setVersion(version);
					row.setQuestionId(personRow);
					//row.setFinishTime(personCol);
					logger.debug("MatrixPlus: "+row);
					list.add(row);
				}
				
				if(key.startsWith(prefix_matrix)) {
					String questionId = key.replaceAll(prefix_matrix, replacement);
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
				
				//单独处理
				if(key.startsWith(prefix_matrixNet)) {
					String[] values = value.split(",");
					String questionId = key.replaceAll(prefix_matrixNet, replacement);
					for(String _value : values) {
						if(StringUtils.isBlank(_value)) continue;
						String[] _values = _value.split("_");
						long optionKey = Long.valueOf(_values[0]);
						String peopleId = type.equals(TYPE_CLOSE) ? _values[1] : getPersonId(version, _values[1], responderId);
						
						QuestionnaireMatrixNet e = new QuestionnaireMatrixNet();
						e.setResponderId(responderId);
						e.setQuestionId(questionId);
						e.setOptionKey(optionKey);
						e.setRelationPersonId(peopleId);
						e.setFinishTime(finishTime);
						e.setVersion(version);
						matrixNets.add(e);
					}
				}
				
				if(key.startsWith(prefix_property)) {
					String responderPropertyId = value;
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
			
			if(!matrixNets.isEmpty()) {
				Object[][] params = new Object[matrixNets.size()][6];
				int index = 0;
				for(QuestionnaireMatrixNet e : matrixNets) {
					params[index][0] = e.getResponderId();
					params[index][1] = e.getQuestionId();
					params[index][2] = e.getRelationPersonId();
					params[index][3] = e.getOptionKey();
					params[index][4] = e.getVersion();
					params[index][5] = e.getFinishTime();
					index++;
				}
				final String insert = "insert into questionnaire_matrixnet values(?,?,?,?,?,?)";
				int[] records = query.batch(DbHelper.getConnection(), insert, params);
				logger.debug("questionnaire matrix net records : "+records.length);
			}
			
			//responder property
			if(!properties.isEmpty()) {
				//Connection conn = DbHelper.getConnection();
				conn.setAutoCommit(false);
				final String delete = "delete from responder_properties where responder_id = '"+responderId+"' and version = " + version;
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
			try {
				DbUtils.rollback(conn);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				DbUtils.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return bln;
	}
	
	@Override
	public boolean parseQuestionnaireTemplate(InputStream excel, String type) {
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
			if(type.equals(QuestionnairePaperService.TYPE_CLOSE)) {
				Sheet responders = workbook.getSheetAt(1);
				logger.info(responders.getSheetName());
				parseResponders(responders, version);
			} else {
				QueryRunner q = new QueryRunner();
				q.update(DbHelper.getConnection(), "insert into questionnaire_open values("+version+")");
			}
			
			//{问卷题目}
			Sheet requestions = workbook.getSheetAt(2);
			logger.info(requestions.getSheetName());
			parseRequestions(requestions, version);
			
			//{矩阵题}
			Sheet matrix = workbook.getSheetAt(3);
			logger.info(matrix.getSheetName());
			parseRequestionsOfMatrix(matrix, version, type);
			
			//{网络题}
			Sheet matrixNet = workbook.getSheetAt(4);
			logger.info(matrixNet.getSheetName());
			parseRequestonsOfMatrixNet(matrixNet, version);
			
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
			final String[] sqls = {"questionnaire_matrixnet", "questionnaire", "question", "question_option", "responder_properties", "responder", "responder_property"};
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
							p.setId(UUIDGenerator.generateUUID());
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
					ResponderProperty e = new ResponderProperty(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getLong(5));
					list.add(e);
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
				responder.setId(UUIDGenerator.generateUUID());
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
									e.setId(rs.getString("responder_property_id"));
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
	
	void parseRequestions(Sheet requestions, final long version, int type) throws Exception {

		int index = 0;
		QueryRunner query = new QueryRunner();
		for(Iterator<Row> rowIter = requestions.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && !StringUtils.isBlank(toConvert(row.getCell(0))) && index > 0) {
				logger.debug(index + " : " + row.getCell(0));
				String title = toConvert(row.getCell(0));
				String optionString = toConvert(row.getCell(1));
				String optionGroupId = parseOptions(optionString, version).get(0).getId();
				
				//迭代小题
				int columnIndex = 0;
				for(Iterator<Cell> iter = row.cellIterator(); iter.hasNext();) {
					Cell cell = iter.next();
					
					if(cell == null) continue;
					if(StringUtils.isBlank(toConvert(cell))) continue;
					String content = toConvert(cell);
					if(columnIndex > 1) {
						Question q = new Question();
						q.setId(UUIDGenerator.generateUUID());
						
						q.setContent(content);
						q.setOptionId(optionGroupId);
						q.setTitle(title);
						q.setVersion(version);
						q.setType(type);
						saveQuestion(q, query);
						logger.info(q);
					}
					columnIndex++;
				}
			}
			index++;
		}
	
	}
	
	void parseRequestions(Sheet requestions, final long version) throws Exception {
		parseRequestions(requestions, version, Question.TYPE_NORMAL);
	}
	
	void parseRequestionsOfMatrix(Sheet matrix, final long version, String type) throws Exception {
		int index = 0;
		for(Iterator<Row> rowIter = matrix.iterator(); rowIter.hasNext();) {
			Row row = rowIter.next();
			if(row.getCell(0) != null && !StringUtils.isBlank(toConvert(row.getCell(0))) && index > 0) {
				
				Question q = new Question();
				q.setId(UUIDGenerator.generateUUID());
				
				q.setTitle(toConvert(row.getCell(0)));
				q.setVersion(version);
				q.setType(Question.TYPE_MATRIX);
				saveQuestion(q, new QueryRunner());
				logger.info(q);
			}
			
			//开放式问卷中，网络题只有一道
			if(type.equals(QuestionnairePaperService.TYPE_OPEN) && index == 1) break;
			index++;
		}
	}
	
	void parseRequestonsOfMatrixNet(Sheet matrixNet, final long version) throws Exception {
		parseRequestions(matrixNet, version, Question.TYPE_MATRIX_NET);
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
		String optionGroupId = UUIDGenerator.generateUUID();
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
	
	String getPersonId(long version, String personNo, String responderId) throws Exception {
		final String sql = "select responder_id rid from responder where version = ? and responder_person = ? and responder_pid = ?";
		Map<String, Object> map = new QueryRunner().query(DbHelper.getConnection(), sql, new MapHandler(), version, personNo, responderId);
		return (String)map.get("RID");
	}
	
	void saveResponder(Responder person) throws Exception {
		final String insertResponder = "insert into responder values(?,?,?,?,?,?,?)";
		QueryRunner query = new QueryRunner();
		query.update(DbHelper.getConnection(), insertResponder, person.getId(), person.getName(), person.getVersion(), person.getNo(), person.getPwd(), person.getPid(), person.getPersonNo());
	}
	
	void saveResponder(Responder r, QueryRunner query, Connection conn) throws Exception {
		final String insertResponder = "insert into responder values(?,?,?,?,?,0,'')";
		final String insertResponderProperty = "insert into responder_properties(responder_id,responder_property_id,version) values(?,?,?)";
		
		boolean bln = true;
		final int count = 6;
		String no = RandomStringUtils.randomNumeric(count);
		while(bln) {
			Map<String, Object> map = query.query(DbHelper.getConnection(), "select count(1) a from responder where responder_no = ?", new MapHandler(), no);
			int noInResponder = (Integer)map.get("A");
			if(noInResponder == 0) {
				bln = false;
			} else {
				no = RandomStringUtils.randomNumeric(count);
			}
		}
		r.setNo(no);
		String pwd = RandomStringUtils.random(count, true, false);
		r.setPwd(pwd);
		int responderRecord = query.update(DbHelper.getConnection(), insertResponder, r.getId(), r.getName(), r.getVersion(), r.getNo(), r.getPwd());
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
			for(Object e : _list) ids += "," + "'" + e + "'";
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
				String responderId = (String)responderAttr[0];
				String responderName = (String)responderAttr[1];
				sql = "select a.question_id, a.option_key from questionnaire a where a.type = "+Question.TYPE_NORMAL+" and a.responder_id = '" + responderId + "'";
				logger.debug(sql);
				Map<Object, Map<String, Object>> map = query.query(DbHelper.getConnection(), sql, new KeyedHandler());
				if(map.isEmpty()) continue;
				data[rowIndex][0] = responderName;
				for(int columnIndex = 1; columnIndex < columnSize; columnIndex++) {
					Question ref = (Question)data[0][columnIndex];
					String key = ref.getId();
					Map<String, Object> value = map.get(key);
					data[rowIndex][columnIndex] = value.get("OPTION_KEY");
				}
			}
			
			for(int rowIndex = 1; rowIndex < rowSize; rowIndex++) {
				Object[] responderAttr = (Object[])list.get(rowIndex-1);
				String responderId = (String)responderAttr[0];
				
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
		Map<Object, Object[][]> matrixQuestion = new HashMap<Object, Object[][]>();//calculateForMatrix(version);
		
		String sql = "select count(version) v from questionnaire_open where version = ?";
		QueryRunner query = new QueryRunner();
		try {
			Map<String, Object> map = query.query(DbHelper.getConnection(), sql, new MapHandler(), version);
			int v = (Integer)map.get("v".toUpperCase());
			if(v == 0) matrixQuestion = calculateForMatrix(version);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return generateExcelForQuestionnaire(normalQuestion, matrixQuestion);
	}
	
	@Override
	public Workbook generateExcelForQuestionnaireMatrixNet(long version) {
		Workbook workbook = new HSSFWorkbook();
		
		//每个答题人一个sheet
		QueryRunner query = new QueryRunner();
		String sql = "select a.responder_id from questionnaire_matrixnet a where a.version = " + version + " group by a.responder_id";
		try {
			List<Object> list = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
			if(list != null && !list.isEmpty()) {
				
				//总人数
				int n = 0;
				
				List<Map<String, Object>> differentList = new ArrayList<Map<String,Object>>();
				
				for(int sheetIndex = 0; sheetIndex < list.size(); sheetIndex++) {
					Responder responder = getResponder((String)list.get(sheetIndex));
					String sheetname = (sheetIndex + 1) + responder.getName();
					Sheet sheet = workbook.createSheet(sheetname);
					
					Map<String, Object> responderDiff = new TreeMap<String, Object>();
					responderDiff.put("responderName", sheetname);
					
					sql = "select question_id from questionnaire_matrixnet where responder_id = '" + responder.getId() + "' group by question_id";
					List<Object> questionIds = query.query(DbHelper.getConnection(), sql, new ColumnListHandler());
					
					String _sql = "select relation_person_id from questionnaire_matrixnet where responder_id = '" + responder.getId() + "' group by relation_person_id ";
					List<Map<String, Object>> relationPersons = query.query(DbHelper.getConnection(), _sql, new MapListHandler());
					n = relationPersons.size();
					logger.debug(relationPersons);
					Row first = sheet.createRow(0);
					int globalRownum = 1;
					for(int column = 0; column < relationPersons.size(); column++) {
						Cell cell = first.createCell(column + 1);
						Map<String, Object> relationPerson = relationPersons.get(column);
						Responder person = getResponder((String)relationPerson.get("relation_person_id".toUpperCase()));
						cell.setCellValue(person.getName());
					}
					first.createCell(first.getLastCellNum()).setCellValue("异质性指标");
					first.createCell(first.getLastCellNum()).setCellValue("平均值");

					for(int rownum = 1; rownum <= questionIds.size(); rownum++,globalRownum++) {
						Question question = getQuestion((String)questionIds.get(rownum-1));
						
						int columns = relationPersons.size();
						int values = 0;
						
						Row row = sheet.createRow(rownum);
						Cell firstCell = row.createCell(0);
						firstCell.setCellValue(question.getDescription());

						Frequency frequency = new Frequency();
						for(int column = 1; column <= columns; column++) {
							Cell cell = row.createCell(column);
							Map<String, Object> relationPerson = query.query(DbHelper.getConnection(), "select * from questionnaire_matrixnet where responder_id = ? and question_id = ? and relation_person_id = ?",new MapHandler(), responder.getId(), question.getId(), relationPersons.get(column-1).get("relation_person_id"));
							long value = (Long)relationPerson.get("option_key".toUpperCase());
							cell.setCellValue(value);
							values += value;
							frequency.addValue(value);
						}
						
						//异质性指标
						//k(n2-Σf2)/n2(k-1) 2是平方的意思
						//其中n为全部个案数目(totality)，k为变项的类别数目，f为每个类别的实际次数。
						Cell formulaCell = row.createCell(row.getLastCellNum());
						String formula = "";
						double k = question.getOptions().size();
						double f = 0;
						for(Option o : question.getOptions()) {
							long count = frequency.getCount(o.getKey());
							f += Math.pow(count, 2);
							formula += "+ (" + count + "*" + count + ")";
							logger.debug("f : " + f + " key : " + o.getKey() + " count : " + count);
						}
						double n2 = Math.pow(n, 2);
						double different = k * (n2 - f) / (n2 * (k - 1));
						formulaCell.setCellValue(different);
						logger.debug("异质性指标:" + different + " f = " + f + " n2 = " + n2 + " k = " + k);
						
						formula = k + "*(" + n2 + "-" + formula.replaceFirst("\\+", "") + ") / " + n2 + "*" + (k-1);
						logger.debug(formula);
						
						responderDiff.put(question.getDescription(), String.valueOf(different));
						
						//平均值
						Cell mediumValueCell =  row.createCell(row.getLastCellNum());
						double a = (double)(new Double(values) / columns);
						logger.debug("平均值:" + values + " / " + columns + " = " + a);
						mediumValueCell.setCellValue(a);
						
					}
					
					//关系程度
					int lastRowNum = globalRownum + 1;
					
					int r = relationPersons.size() + 1;
					int c = relationPersons.size() + 1;
					for(int rownum = 0; rownum < r; rownum++) {
						Row row = sheet.createRow(lastRowNum + rownum);
						
						for(int column = 0; column  < c; column++) {
							Cell cell = row.createCell(column);
							if(rownum == 0 && column == 0) {
								cell.setCellValue("");
								String SQL = "select sum(option_key) sumvalue from questionnaire where version = " + version + " and responder_id = '" + responder.getId() + "' and type = " + Question.TYPE_MATRIX_PLUS ;
								Map<String, Object> map = query.query(DbHelper.getConnection(), SQL, new MapHandler());
								if(map != null && !map.isEmpty()) {
									double sumvalue = (Long)map.get("sumvalue".toUpperCase());
									double personSize = relationPersons.size();
									personSize = personSize * (personSize - 1);
									cell.setCellValue(sumvalue / personSize);
								}
							} else if(rownum == 0 && column > 0) {
								Map<String, Object> relationPerson = relationPersons.get(column - 1);
								Responder person = getResponder((String)relationPerson.get("relation_person_id".toUpperCase()));
								cell.setCellValue(person.getName());
							} else if(rownum > 0 && column == 0) {
								Map<String, Object> relationPerson = relationPersons.get(rownum - 1);
								Responder person = getResponder((String)relationPerson.get("relation_person_id".toUpperCase()));
								cell.setCellValue(person.getName());
							} else if(rownum > 0 && column > 0) {
								Map<String, Object> relationPersonRow = relationPersons.get(rownum - 1);
								Map<String, Object> relationPersonCol = relationPersons.get(column - 1);
								Responder personRow = getResponder((String)relationPersonRow.get("relation_person_id".toUpperCase()));
								Responder personCol = getResponder((String)relationPersonCol.get("relation_person_id".toUpperCase()));
								
								String SQL = "select * from questionnaire where version = " + version + " and responder_id = '" + responder.getId() + "' and type = " + Question.TYPE_MATRIX_PLUS + " and question_id = '" + personRow.getId() + "' and finish_time = '" + personCol.getId() + "'";
								Map<String, Object> map = query.query(DbHelper.getConnection(), SQL, new MapHandler());
								logger.debug(SQL);
								logger.debug(map);
								if(map == null || map.isEmpty()) continue;
								Long value = (Long)map.get("option_key".toUpperCase());
								
								cell.setCellValue(value.toString());
							}
						}
					}
					differentList.add(responderDiff);
				}
				
				//参与调查的人员异质性数值统计sheet
				if(!differentList.isEmpty()) {
					Sheet diffSheet = workbook.createSheet("异质性数值统计");
					
					//表头
					Map<String, Object> map = differentList.get(0);
					Row title = diffSheet.createRow(0);
					int col = 1;
					for(String key : map.keySet()) {
						if(key.equals("responderName")) continue;
						Cell cell =  title.createCell(col);
						cell.setCellValue(key);
						col++;
					}
					
					int index = 1;
					for(Map<String, Object> person : differentList) {
						Row row = diffSheet.createRow(index);
						int column = 0;
						for(String key : person.keySet()) {
							Cell cell = row.createCell(column);
							cell.setCellValue(person.get(key).toString());
							column++;
						}
						
						index++;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return workbook;
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
		
//		String name = System.getProperty("user.dir") + "/src/main/webapp/template.xls";
//		name = "/Users/eastseven/Desktop/template_1.xls";
//		System.out.println(name);
//		InputStream excel = new FileInputStream(name);
//		service.parseQuestionnaireTemplate(excel);
		
		sql = "select a.version from question a order by a.version desc";
		@SuppressWarnings("rawtypes")
		Map map = query.query(conn, sql, new KeyedHandler());
		Long version = (Long)map.keySet().iterator().next();
		version = 1322211909948L;
		System.out.println(version);
		
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
		File file = new File("workbook.xls");
		if(file.exists()) file.delete();
		
		FileOutputStream fileOut = new FileOutputStream("workbook.xls");
		Workbook wb = service.generateExcelForQuestionnaireMatrixNet(version);//service.generateExcelForQuestionnaire(result, matrix);
		wb.write(fileOut);
	    fileOut.close();
	    System.out.println("done...");
	}
	
	@Override
	public Long getOpenPaperVersion() {
		long version = 0;
		
		String sql = "select max(a.version) version from QUESTIONNAIRE_OPEN a";
		QueryRunner q = new QueryRunner();
		try {
			Map<String, Object> result = q.query(DbHelper.getConnection(), sql, new MapHandler());
			version = (Long)result.get("VERSION");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return version;
	}
}
