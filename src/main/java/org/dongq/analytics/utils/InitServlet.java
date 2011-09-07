package org.dongq.analytics.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(InitServlet.class);
	
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
}
