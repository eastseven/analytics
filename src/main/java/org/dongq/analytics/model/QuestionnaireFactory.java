/**
 * 
 */
package org.dongq.analytics.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eastseven
 * 
 */
public class QuestionnaireFactory {
	
	public static void main(String[] args) {
		Questionnaire paper = getSampleQuestionnaire();
		
		paper.print();
	}

	public static Questionnaire getSampleQuestionnaire() {
		Questionnaire paper = new Questionnaire();
		
		List<QuestionGroup> groups = new ArrayList<QuestionGroup>();
		List<Question> list = null;
		QuestionGroup group = null;
		String[] contents = null;
		int index = 1;
		
		contents = new String[] {"我能和他人有效地相处","我善于与人交谈","我能清晰明白地表达我的看法","我能轻松与人交谈不同话题","我口头表达流利","我能轻易察觉交往中的潜规则","我点子多","我直觉很灵敏","我能很快找到问题的核心","我的常识比一般人多"};
		list = new ArrayList<Question>();
		
		for(String content : contents) {
			Question q = new Question(content);
			q.setId(index);
			list.add(q);
			index++;
		}
		group = new QuestionGroup("请根据以下描述与您个人情况相符合的程度进行打分", list);
		group.setOptions(getOptions());
		group.setQuestions(list);
		group.setId(System.currentTimeMillis());
		groups.add(group);
		
		contents = new String[] {"我总是能够相信销售主管的解释和说明","销售主管通常信守其承诺(如遵守公司的销售政策)","销售主管提供的信息往往是准确的","销售主管是真心诚意地对待我的"};
		list = new ArrayList<Question>();
		for(String content : contents) {
			Question q = new Question(content);
			q.setId(index);
			list.add(q);
			index++;
		}
		group = new QuestionGroup("请根据以下描述与您个人情况相符合的程度进行打分", list);
		group.setOptions(getOptions());
		group.setQuestions(list);
		group.setId(System.currentTimeMillis());
		groups.add(group);
		
		paper.setQuestions(groups);
		paper.setId(System.currentTimeMillis());
		
		return paper;
	}
	
	public static List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>();
		
		options.add(new Option(1, "完全不符合"));
		options.add(new Option(2, "基本不符合"));
		options.add(new Option(3, "有点不符合"));
		options.add(new Option(4, "一般"));
		options.add(new Option(5, "有点符合"));
		options.add(new Option(6, "基本符合"));
		options.add(new Option(7, "完全符合"));
		
		return options;
	}
}
