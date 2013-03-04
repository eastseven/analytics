package org.dongq.analytics;

import org.dongq.analytics.service.QuestionnairePaperServiceImpl;

import junit.framework.Assert;
import junit.framework.TestCase;

public class QuestionnairePaperServiceTest extends TestCase {

	private QuestionnairePaperServiceImpl testTarget;

	private long version;
	
	public void testFoo() {
		System.out.println("test foo function start...");
		Assert.assertEquals(true, true);
	}

	@Override
	protected void setUp() throws Exception {
		this.testTarget = new QuestionnairePaperServiceImpl();
		this.version = this.testTarget.getOpenPaperVersion();
	}
	
	@Override
	protected void tearDown() throws Exception {
		this.testTarget = null;
	}
	
	public void testGenerateExcelForQuestionnaireMatrixNet() {
		this.testTarget.generateExcelForQuestionnaireMatrixNet(version);
	}
}
