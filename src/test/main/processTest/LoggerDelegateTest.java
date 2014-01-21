package processTest;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.junit.Assert.*;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import processTest.LoggerDelegate;

public class LoggerDelegateTest {

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule();

	@Mock
	public LoggerDelegate loggerDelegate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mocks.register("loggerDelegate", loggerDelegate);
		Mocks.register("processTest.LoggerDelegate", loggerDelegate);
	}

	@After
	public void tearDown() throws Exception {
	}

	/** 
	 * Test process with timer event: start -> wait 1 minute -> execute mocked service task -> end
	 */
	@Test
	@Deployment(resources = { "processWithTimer.bpmn" })
	public void testWithTimer() {
	    processEngineRule.getRuntimeService().startProcessInstanceByKey("sample");
	    // there is now a single job in the bpmn engine which needs to be triggered manually
	    Job job = processEngineRule.getManagementService().createJobQuery().singleResult();
	    processEngineRule.getManagementService().executeJob(job.getId());
	    // verify that delegate has been executed
	    verify(loggerDelegate).doSomething();
	    verifyNoMoreInteractions(loggerDelegate);
	}

	/**
	 * Test process: start -> execute mocked service task (camunda:expression) -> end
	 */
	@Test
	@Deployment(resources = { "processWithoutTimer.bpmn" })
	public void testWithoutTimer() {
	    processEngineRule.getRuntimeService().startProcessInstanceByKey("sample");
	    verify(loggerDelegate).doSomething();
	    verifyNoMoreInteractions(loggerDelegate);
	}

	/**
	 * Test process: start -> execute mocked service task (camunda:class) -> end
	 * Mocking camuda:class is not possible!
	 */
	@Test
	@Deployment(resources = { "mockedClass.bpmn" })
	public void testMockedClass() {
		try {
			processEngineRule.getRuntimeService().startProcessInstanceByKey("sample");
			fail("The process should not be started because it refers to a non-existing class, which cannot be mocked");
		} catch (ProcessEngineException pee) {
			// expected behavior
		}
	}

}
