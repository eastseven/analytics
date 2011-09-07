/**
 * 
 */
package org.dongq.analytics.ui;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;
import org.dongq.analytics.utils.DbHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * @author eastseven
 * 
 */
public class QuestionnaireComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(QuestionnaireComposer.class);
	
	private final String FILE_SUFFIX = "xls,xlsx";
	
	Grid grid;

	QuestionnairePaperService service;
	
	/**
	 * 上传
	 * @param event
	 */
	public void onUpload$file(UploadEvent event) {
		Media file = event.getMedia();
		logger.info(file.getName());
		logger.info(file.getContentType());
		logger.info(file.getFormat());
		if(FILE_SUFFIX.contains(file.getFormat().toLowerCase())) {
			InputStream excel = file.getStreamData();
			logger.info(excel);
			boolean bln = service.parseQuestionnaireTemplate(excel);
			String msg = bln ? "问卷导入成功" : "问卷导入失败";
			try {
				Messagebox.show(msg, "title", Messagebox.OK, "", new EventListener() {
					
					@Override
					public void onEvent(Event event) throws Exception {
//						mainDiv.getChildren().clear();
//						Executions.createComponents("questionnaire_grid.zul", mainDiv, null);
						init();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			alert("非excel格式文件不能上传");
		}
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug(comp);
		service = new QuestionnairePaperServiceImpl();
		if(comp instanceof Grid) grid = (Grid)comp;
		init();
	}
	
	void init() {
		List<Map<Object, Object>> list = getList();
		
		if(grid != null) {
			logger.debug("grid is reloading...");
			grid.setModel(new ListModelList(list));
			grid.setRowRenderer(new QuestionnaireRenderer());
		}
	}
	
	List<Map<Object, Object>> getList() {
		List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();

		try {
			QueryRunner query = new QueryRunner();
			String sql = "select a.version, count(a.question_id) questions from question a group by a.version";
			
			list = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Map<Object, Object>>>() {
				@Override
				public List<Map<Object, Object>> handle(ResultSet rs) throws SQLException {
					List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();
					
					while(rs.next()) {
						Map<Object, Object> map = new HashMap<Object, Object>();
						map.put("version", rs.getLong("version"));
						map.put("questions", rs.getLong("questions"));
						
						list.add(map);
					}
					
					return list;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	class QuestionnaireRenderer implements RowRenderer {

		@Override
		public void render(Row row, Object data) throws Exception {
			if(data instanceof HashMap) {
				@SuppressWarnings("unchecked")
				HashMap<Object, Object> map = (HashMap<Object, Object>)data;
				
				String version = DateFormatUtils.format((Long)map.get("version"), "yyyyMMdd HH:mm:ss");

				QueryRunner query = new QueryRunner();
				String sql = "select * from responder a where a.version = " + map.get("version");
				List<Map<String, String>> list = query.query(DbHelper.getConnection(), sql, new ResultSetHandler<List<Map<String, String>>>() {
					@Override
					public List<Map<String, String>> handle(ResultSet rs) throws SQLException {
						List<Map<String, String>> list = new ArrayList<Map<String,String>>();
						while(rs.next()) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("responder_id", rs.getObject("responder_id").toString());
							map.put("responder_name", rs.getObject("responder_name").toString());
							list.add(map);
						}
						return list;
					}
				});
				String content = "<table>";
				content += "<thead><tr><th >编号</th><th>姓名</th><th>操作</th></tr></thead>";
				content += "<tbody>";
				for(Map<String, String> e : list) {
					String id = e.get("responder_id");
					String name = e.get("responder_name");
					String link = "<a href='questionnaire.jsp?id="+id+"' target='_blank'>开始答题</a>";
					String _row = "<tr><td>"+id+"</td><td>"+name+"</td><td>"+link+"</td></tr>";
					content += _row;
				}
				content += "</tbody></table>";
				Detail detail = new Detail();
				Html html = new Html(content);
				detail.appendChild(html);
				row.appendChild(detail);
				
				row.appendChild(new Label(map.get("version").toString()));
				row.appendChild(new Label(version));
				row.appendChild(new Label(map.get("questions").toString()));
			}
			
		}
		
	}
	
}
