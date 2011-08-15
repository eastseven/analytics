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
		Questionnaire paper = new Questionnaire();
		
		List<Question> list = new ArrayList<Question>();
		list.add(new Question("�������ҵ�������", getOptions(), 4));
		list.add(new Question("��Ȧ�ڵ����Ѷ�ϣ��Ȧ����ʿ��Ԫ�����磺ְҵ������ȵĶ����ԣ�", getOptions(), 4));
		list.add(new Question("��Ȧ�ڵ�����ӵ�й�ͬ������", getOptions(), 3));
		list.add(new Question("�������ҵ����ѵ�������ʵ��", getOptions(), 5));
		list.add(new Question("��Ȧ�ڵ����Ѷ��ܳ���ʧ��", getOptions(), 4));
		//QuestionGroup group = new QuestionGroup("����������������翼�ǵ���������֪����������������ҵ��չ��ص���������Ա������������ҵ�����������������ʱ���������������ҵ��չ��ѯ����Ϊ��ҵ�ṩ�����ȵ���������Ա������������ͬ�¡������顢���ѡ����˵ȣ�����������������������������ϵĳ̶Ƚ��д�֡�", list);
		
		paper.setQuestions(list);
		
		paper.print();
	}

	static List<Option> getOptions() {
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
