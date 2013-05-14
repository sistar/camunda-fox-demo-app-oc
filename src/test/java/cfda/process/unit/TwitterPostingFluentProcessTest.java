package cfda.process.unit;

import cfda.UnitTest;
import cfda.delegates.PostTweetDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.fluent.FluentProcessEngineTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;

import static cfda.process.test.C.*;
import static org.camunda.bpm.engine.test.fluent.FluentProcessEngineTests.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;

@Category(UnitTest.class)
public class TwitterPostingFluentProcessTest  {
    @Rule
    public ProcessEngineRule activitiRule = new ProcessEngineRule();
    @Rule
    public FluentProcessEngineTestRule bpmnFluentTestRule = new FluentProcessEngineTestRule(this);

    @Mock
    public PostTweetDelegate postTweetDelegate;

    @Test
    @Deployment(resources = "diagrams/twitter-posting.bpmn")
    public void walkThroughProcess() throws Exception {

        doCallRealMethod().when(
                postTweetDelegate).execute(
                any(DelegateExecution.class));

        newProcessInstance(TWITTER_POSTING_PROCESS);

        //verify(postTweetDelegateB).execute(any(DelegateExecution.class));

        assertThat(processInstance().getVariable(TWEET_RESULT)).exists().isDefined().asString().isEqualTo("ok");

        assertThat(processInstance()).isWaitingAt(APPROVE_TWEET_USER_TASK);
        assertThat(processInstance().task()).isAssignedTo("fte");

        processInstance().task().complete();

        assertThat(processInstance().isEnded());
    }
}