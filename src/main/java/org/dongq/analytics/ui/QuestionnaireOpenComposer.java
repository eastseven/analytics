package org.dongq.analytics.ui;

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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Detail;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

public class QuestionnaireOpenComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(QuestionnaireOpenComposer.class);

	Grid openGrid;

	QuestionnairePaperService service;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.debug(comp);
		service = new QuestionnairePaperServiceImpl();
		if(comp instanceof Grid) openGrid = (Grid)comp;
		init();
	}
	
	void init() {
		List<Map<Object, Object>> list = getList();
		
		if(openGrid != null) {
			logger.debug("grid is reloading...");
			openGrid.setModel(new ListModelList(list));
			openGrid.setRowRenderer(new QuestionnaireRenderer());
		}
	}
	
	List<Map<Object, Object>> getList() {
		List<Map<Object, Object>> list = new ArrayList<Map<Object,Object>>();

		try {
			QueryRunner query = new QueryRunner();
			String sql = "select a.version, count(a.question_id) questions from question a group by a.version";
			sql = "select a.version, count(a.question_id) questions from question a where exists(select 1 from questionnaire_open b where a.version = b.version) group by a.version";
			
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
				final String version = DateFormatUtils.format((Long)map.get("version"), pattern);
				final Long fileName = (Long)map.get("version");
				
				Detail detail = new Detail();
				Include src = new Include("demo.zul");
				src.setDynamicProperty("version", map.get("version").toString());
				detail.appendChild(src);
				row.appendChild(detail);
				row.appendChild(new Label(map.get("version").toString()));
				row.appendChild(new Label(version));
				row.appendChild(new Label(map.get("questions").toString()));
				
				Button excelBtn = new Button("生成Excel数据");
				excelBtn.addEventListener("onClick", new ExcelGenerateEventListener(fileName, 1));
				
				Button matrixBtn = new Button("生成网络数据");
				matrixBtn.addEventListener("onClick", new ExcelGenerateEventListener(fileName, 2));
				
				Button editTitleBtn = new Button("编辑问卷标题");
				editTitleBtn.addEventListener("onClick", new EditTilteListener(openGrid.getParent(), map));
				
				Div div = new Div();
				div.appendChild(excelBtn);
				div.appendChild(matrixBtn);
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
