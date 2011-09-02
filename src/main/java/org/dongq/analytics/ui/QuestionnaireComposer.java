/**
 * 
 */
package org.dongq.analytics.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;

/**
 * @author eastseven
 * 
 */
public class QuestionnaireComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	
	private static final Log logger = LogFactory.getLog(QuestionnaireComposer.class);
	
	Div mainDiv;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.info(comp);
	}
	
}
