/**
 * 
 */
package org.dongq.analytics.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;

/**
 * @author eastseven
 *
 */
public class QuestionnaireLoginComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private static final Log logger = LogFactory.getLog(QuestionnaireLoginComposer.class);
	
	Combobox version;
	Combobox responders;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		logger.info(comp);
	}
	
	void init() {
		
	}
	
	public void onClick$submitBtn() {
		this.alert("submitBtn");
	}
	
}
