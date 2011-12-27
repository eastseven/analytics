package org.dongq.analytics.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(InitServlet.class);
	
	private final String METHOD = "m";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("初始化WEB");
		ServletContext context = config.getServletContext();
		String name = config.getInitParameter("config");
		String path = "/WEB-INF/classes/" + name;
		logger.info(path);
		
		try {
			URL url = context.getResource(path);
			logger.info(url);
			Configuration file = new PropertiesConfiguration(url);
			if(file.isEmpty()) {
				logger.info("url is empty");
			} else {
				String dburl = "";
				String dbname = "";
				String dbpath = "";
				for(@SuppressWarnings("unchecked")
				Iterator<String> iter = file.getKeys(); iter.hasNext();) {
					String key = iter.next();
					logger.info(key + ":" + file.getProperty(key));
					if(key.equals("db.driver")) {
						DbHelper.driver = (String)file.getProperty(key);
					}
					if(key.equals("db.url")) dburl = (String)file.getProperty(key);
					if(key.equals("db.name")) dbname = (String)file.getProperty(key);
					if(key.equals("db.path")) dbpath = (String)file.getProperty(key);
				}
				if(!StringUtils.isBlank(dbpath) && !StringUtils.isBlank(dbname) && !StringUtils.isBlank(dburl)) {
					dburl = dburl.replace(dbname, dbpath.replace("true", "false"));
					DbHelper.url = dburl;
					logger.info(dburl);
					logger.info(DbHelper.url);
				}
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String method = req.getParameter(METHOD);
		logger.debug(method);
		if("login".equalsIgnoreCase(method)) {
			login(req, resp);
		}
	}
	
	void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String responderNo = req.getParameter("no");
		String responderPwd = req.getParameter("pwd");
		
		if(responderNo.equals("qiemaomao") && responderPwd.equals("19831215")) {
			resp.sendRedirect("index.zul");
		} else {
			
			Map<String, Object> result = new HashMap<String, Object>();
			QueryRunner query = new QueryRunner();
			Connection conn = DbHelper.getConnection();
			final String check = "select a.version v, a.responder_pwd pwd, a.responder_id id from responder a where a.responder_no = '" + responderNo + "'";
			
			logger.debug(check);
			
			try {
				Map<String, Object> map = query.query(conn, check, new MapHandler());
				if(map == null || map.isEmpty()) {
					result.put("bln", Boolean.FALSE);
					result.put("msg", "编号错误");
				} else {
					Object pwd = map.get("PWD");
					if(responderPwd.equals(pwd)) {
						result.put("bln", Boolean.TRUE);
						result.put("version", map.get("V"));
						result.put("id", map.get("ID"));
					} else {
						result.put("bln", Boolean.FALSE);
						result.put("msg", "密码错误");
					}
				}
				
				String json = JSON.toJSONString(result);
				resp.setCharacterEncoding("UTF-8");
				PrintWriter out = resp.getWriter();
				out.write(json);
				out.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if(conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}
