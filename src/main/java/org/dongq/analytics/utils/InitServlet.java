package org.dongq.analytics.utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(InitServlet.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("≥ı ºªØWEB");
		String name = config.getInitParameter("config");
		logger.info(name);
		
	}
}
