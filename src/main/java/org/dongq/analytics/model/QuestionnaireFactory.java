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
		list.add(new Question("我信任我的生意伙伴", getOptions(), 4));
		list.add(new Question("我圈内的朋友都希望圈内人士多元化（如：职业、年龄等的多样性）", getOptions(), 4));
		list.add(new Question("我圈内的朋友拥有共同的语言", getOptions(), 3));
		list.add(new Question("我相信我的朋友的能力和实力", getOptions(), 5));
		list.add(new Question("我圈内的朋友都能承受失败", getOptions(), 4));
		//QuestionGroup group = new QuestionGroup("以下所述的社会网络考虑的是您所感知到的与您工作或企业发展相关的社会网络成员（包括您在企业管理过程中遇到困难时的情感倾述对象、企业发展咨询对象、为企业提供帮助等的社会网络成员），包括您的同事、生意伙伴、朋友、家人等，请根据以下描述与您个人情况相符合的程度进行打分。", list);
		
		paper.setQuestions(list);
		
		paper.print();
	}

	static List<Option> getOptions() {
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
