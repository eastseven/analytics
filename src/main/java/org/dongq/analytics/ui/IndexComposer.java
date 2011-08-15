/**
 * 
 */
package org.dongq.analytics.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.zk.Version;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;

/**
 * @author eastseven
 * 
 */
public class IndexComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(IndexComposer.class);
	
	Div mainDiv;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		log.info(comp);
	}
	
	public void onClick$newBtn() {
		this.alert("new event");
	}

	public void onClick$openBtn() {
		this.alert("open event");
	}

	public void onClick$about() {
		this.alert(Version.RELEASE);
	}
}
