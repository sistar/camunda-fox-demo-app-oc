import cfda.delegates.PostTweetDelegate;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.activiti.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MyBusinessProcessTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();
    PostTweetDelegate postTweetDelegate = spy(new PostTweetDelegate());

    @Before
    public void setUp() throws Exception {
        Mocks.register("postTweetDelegate", postTweetDelegate);

    }

    @Test
    @Deployment(resources = "diagrams/twitter-posting.bpmn")
    public void spyThatTweetResultIsOk() throws Exception {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance twitterPosting = runtimeService.startProcessInstanceByKey("TwitterPosting");
        String processInstanceId = twitterPosting.getProcessInstanceId();

        HistoricProcessInstance historicProcessInstance =
                activitiRule.getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        HistoricVariableInstance tweetResult = activitiRule.getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).orderByVariableName().desc().variableName("tweet-result").singleResult();

        verify(postTweetDelegate).execute(any(DelegateExecution.class));

        assertThat((String) tweetResult.getValue(), is(equalTo("ok")));
        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }
}