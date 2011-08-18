/**
 * 
 */
package org.dongq.analytics.ui;

import java.util.List;

import org.dongq.analytics.model.Option;
import org.dongq.analytics.model.Question;
import org.dongq.analytics.model.QuestionGroup;
import org.dongq.analytics.model.Questionnaire;
import org.dongq.analytics.model.QuestionnaireFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

/**
 * @author eastseven
 * 
 */
public class QuestionnaireSampleComposer extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;
	
	Rows rows;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		System.out.println(comp);
		Questionnaire questionnaire = QuestionnaireFactory.getSampleQuestionnaire();
		List<QuestionGroup> groups = questionnaire.getQuestions();
		for(QuestionGroup group : groups) {
			Cell cell = new Cell();
			cell.setColspan(2);
			cell.appendChild(new Label(group.getTitle() + "("+group.getOptionsDescribe()+")"));
			Row row = new Row();
			row.appendChild(cell);
			row.setHeight("50px");
			row.setStyle("font-family:Arial,Verdana,Sans-serif;font-weight: bold;font-style:italic;");
			rows.appendChild(row);
			
			List<Question> questions = group.getQuestions();
			for(Question question : questions) {
				row = new Row();
				row.appendChild(new Label(question.getContent()));
				
				Hbox hbox = new Hbox();
				Radiogroup radioGroup = new Radiogroup();
				for(Option option : question.getOptions()) {
					Radio radio = new Radio(String.valueOf(option.getKey()));
					radioGroup.appendChild(radio);
				}
				hbox.appendChild(radioGroup);
				row.appendChild(hbox);
				
				rows.appendChild(row);
			}
		}
		System.out.println(rows);
	}

}
