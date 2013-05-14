package cfda.process.unit;

import cfda.UnitTest;
import cfda.delegates.PostTweetDelegate;
import cfda.process.test.C;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@Category(UnitTest.class)
public class TwitterPostingProcessTest {

    @Rule
    public ProcessEngineRule activitiRule = new ProcessEngineRule();
    PostTweetDelegate postTweetDelegate = spy(new PostTweetDelegate());

    @Before
    public void setUp() throws Exception {
        Mocks.register("postTweetDelegate", postTweetDelegate);
    }

    @Test
    @Deployment(resources = "diagrams/twitter-posting.bpmn")
    public void spyThatTweetResultIsOk() throws Exception {
        ProcessInstance twitterPosting = activitiRule.getRuntimeService()
                .startProcessInstanceByKey(C.TWITTER_POSTING_PROCESS);
        String processInstanceId = twitterPosting.getProcessInstanceId();

        verify(postTweetDelegate).execute(any(DelegateExecution.class));

        HistoricVariableInstance tweetResult = activitiRule.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByVariableName()
                .desc()
                .variableName("tweet-result")
                .singleResult();

        assertThat((String) tweetResult.getValue(), is(equalTo("ok")));

        Task task = activitiRule.getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        assertThat(task.getAssignee(), is(equalTo("fte")));

        assertThat(activitiRule.getRuntimeService().createProcessInstanceQuery()
                .processDefinitionKey(C.TWITTER_POSTING_PROCESS).count(), is(equalTo(1L)));

        activitiRule.getTaskService()
                .complete(task.getId());

        assertThat(activitiRule.getRuntimeService().createProcessInstanceQuery()
                .processDefinitionKey(C.TWITTER_POSTING_PROCESS).count(), is(equalTo(0L)));
    }
}