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
		
		contents = new String[] {"���ܺ�������Ч���ദ","���������˽�̸","�����������׵ر���ҵĿ���","�����������˽�̸��ͬ����","�ҿ�ͷ�������","�������ײ�������е�Ǳ����","�ҵ��Ӷ�","��ֱ��������","���ܺܿ��ҵ�����ĺ���","�ҵĳ�ʶ��һ���˶�"};
		list = new ArrayList<Question>();
		
		for(String content : contents) {
			Question q = new Question(content);
			q.setId(index);
			list.add(q);
			index++;
		}
		group = new QuestionGroup("����������������������������ϵĳ̶Ƚ��д��", list);
		group.setOptions(getOptions());
		group.setQuestions(list);
		group.setId(System.currentTimeMillis());
		groups.add(group);
		
		contents = new String[] {"�������ܹ������������ܵĽ��ͺ�˵��","��������ͨ���������ŵ(�����ع�˾����������)","���������ṩ����Ϣ������׼ȷ��","�������������ĳ���ضԴ��ҵ�"};
		list = new ArrayList<Question>();
		for(String content : contents) {
			Question q = new Question(content);
			q.setId(index);
			list.add(q);
			index++;
		}
		group = new QuestionGroup("����������������������������ϵĳ̶Ƚ��д��", list);
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
		
		options.add(new Option(1, "��ȫ������"));
		options.add(new Option(2, "����������"));
		options.add(new Option(3, "�е㲻����"));
		options.add(new Option(4, "һ��"));
		options.add(new Option(5, "�е����"));
		options.add(new Option(6, "��������"));
		options.add(new Option(7, "��ȫ����"));
		
		return options;
	}
}
