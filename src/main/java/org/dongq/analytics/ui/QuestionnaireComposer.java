/**
 * 
 */
package org.dongq.analytics.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.dongq.analytics.service.QuestionnairePaperService;
import org.dongq.analytics.service.QuestionnairePaperServiceImpl;
import org.dongq.analytics.utils.DbHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

/**
 * @author eastseven
 * 
 */
public class QuestionnaireComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(QuestionnaireComposer.class);
	
	Grid grid;

	QuestionnairePaperService service;
	
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
			sql = "select a.version, count(a.question_id) questions from question a where not exists(select 1 from questionnaire_open b where a.version = b.version) group by a.version";
			
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
				final String pattern = "yyyy-MM-dd HH:mm:ss";
				final Long fileName = (Long)map.get("version");
				final String version = DateFormatUtils.format((Long)map.get("version"), pattern);

				QueryRunner query = new QueryRunner();
				String sql = "select a.responder_id, a.responder_name, a.responder_no, a.responder_pwd, a.version, (select min(b.finish_time) from questionnaire b where a.responder_id = b.responder_id) finish_time from responder a where a.version = " + map.get("version");
				logger.debug(sql);
				List<Map<String, Object>> list = query.query(DbHelper.getConnection(), sql, new MapListHandler());
				
				String content = "<table align='center' style='border-collapse:collapse;border: 1px solid black;'>";
				content += "<thead><tr>"; 
				content += "<th width='20%' style='border: 1px solid black;'>编号</th>";
				content += "<th width='20%' style='border: 1px solid black;'>密码</th>";
				content += "<th width='' style='border: 1px solid black;'>姓名</th>";
				content += "<th width='' style='border: 1px solid black;'>操作</th>";
				content += "<th width='' style='border: 1px solid black;'>时间</th>"; 
				content += "</tr></thead>";
				content += "<tbody>";
				for(Map<String, Object> e : list) {
					String id = e.get("responder_id".toUpperCase()).toString();
					String no = e.get("responder_no".toUpperCase()).toString();
					String pwd = e.get("responder_pwd".toUpperCase()).toString();
					String name = e.get("responder_name".toUpperCase()).toString();
					Object v = e.get("version".toUpperCase());
					Object finishTime = e.get("FINISH_TIME");
					String time = finishTime != null ? DateFormatUtils.format((Long)finishTime, pattern) : "";
					
					String link = "<a href='paper.jsp?id="+id+"&v="+v+"' target='_blank'>开始答题</a>";
					link = "<a href='login.jsp' target='_blank'>开始答题</a>";
					
					String _row = "<tr>";
					_row += "<td align='center' style='border: 1px solid black;'>"+no+"</td>";
					_row += "<td align='center' style='border: 1px solid black;'>"+pwd+"</td>";
					_row += "<td align='center' style='border: 1px solid black;'>"+name+"</td>";
					_row += "<td align='center' style='border: 1px solid black;'>";
					_row += StringUtils.isBlank(time) ? link : "<a href='questionnaire.jsp?id="+id+"&v="+v+"&name="+name+"' target='_blank' >答题完毕</a>";
					_row += "</td>";
					_row += "<td align='center' style='border: 1px solid black;'>"+time+"</td>";
					_row += "</tr>";
					
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
				//按钮
				
				Button excelBtn = new Button("生成Excel数据");
				excelBtn.addEventListener("onClick", new EventListener() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						String msg = "TODO:生成Excel数据\n";
						msg += event.getName() + "\n";
						msg += event.getPage() + "\n";
						msg += event.getTarget() + "\n";
						Execution exe = event.getPage().getDesktop().getExecution();
						msg += "\nRemoteAddr:" + exe.getRemoteAddr();
						msg += "\nRemoteHost:" + exe.getRemoteHost();
						msg += "\nServer:" + exe.getServerName() + ":" + exe.getServerPort() + exe.getContextPath();
						msg += "\nNativeRequest:" + exe.getNativeRequest();
						msg += "\nNativeResponse:" + exe.getNativeResponse();
						logger.debug(msg);
						String path = exe.getServerName() + ":" + exe.getServerPort() + exe.getContextPath();
						logger.debug(path);
						try {
							File file = new File(fileName + ".xls");
							logger.debug(file.getAbsolutePath());
							FileOutputStream out = FileUtils.openOutputStream(file);
							FileInputStream in = FileUtils.openInputStream(file);
							String contentType = "application/vnd.ms-excel";
							QuestionnairePaperService service = new QuestionnairePaperServiceImpl();
							Workbook wb = service.generateExcelForQuestionnaire(fileName);
							wb.write(out);
							out.close();
							
							Filedownload.save(in, contentType, fileName + "-1.xls");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
				
				Button editTitleBtn = new Button("编辑问卷标题");
				editTitleBtn.addEventListener("onClick", new EditTilteListener(grid.getParent(), map));
				
				Div div = new Div();
				div.appendChild(excelBtn);
				div.appendChild(editTitleBtn);
				row.appendChild(div);
			}
			
		}
		
	}
	
	class EditTilteListener implements EventListener {

		private Component comp;
		private Map<Object, Object> map;
		
		public EditTilteListener(Component parent, Map<Object, Object> map) {
			this.comp = parent;
			this.map = map;
		}
		
		@Override
		public void onEvent(Event event) throws Exception {
			Window fckWin = (Window)execution.createComponents("questionnaire_fck.zul", comp, map);
			fckWin.doModal();
		}
		
	}
}
