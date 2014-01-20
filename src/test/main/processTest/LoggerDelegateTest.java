package processTest;

import static org.mockito.Mockito.verify;

import java.util.Date;

import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.test.TestHelper;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import com.ista.dmi.LoggerDelegate;

public class LoggerDelegateTest {

	@Rule
	public ProcessEngineRule processEngineRule = new ProcessEngineRule();

	@Mock
	public LoggerDelegate loggerDelegate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mocks.register("loggerDelegate", loggerDelegate);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Deployment(resources = { "processWithTimer.bpmn" })
	public void testWithTimer() {
		ProcessEngineImpl processEngine = (ProcessEngineImpl) processEngineRule.getProcessEngine();
	    ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("sample");
		// wait for 2 seconds
		Date now = new Date();
		processEngineRule.setCurrentTime(new Date(now.getTime() + 2 * 60 * 1000 * 1000));
		TestHelper.waitForJobExecutorToProcessAllJobs(processEngine.getProcessEngineConfiguration(), 1000, 100);

	    verify(loggerDelegate).doSomething();
	    verifyNoMoreInteractions(loggerDelegate);
	}

	@Test
	@Deployment(resources = { "processWithoutTimer.bpmn" })
	public void testWithoutTimer() {
	    ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("sample");
	    verify(loggerDelegate).doSomething();
	    verifyNoMoreInteractions(loggerDelegate);
	}

}
