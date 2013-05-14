package cfda.process.integration;

import cfda.delegates.PostTweetDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import static cfda.process.test.C.*;
import static org.camunda.bpm.engine.test.fluent.FluentProcessEngineTests.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(Arquillian.class)
public class TwitterPostingProcessFluentITest extends ProcessITest {

    @Spy
    @Produces
    @Named
    public static PostTweetDelegate postTweetDelegate;

    @Deployment
    public static Archive<?> deployProcessAndDependencies() {
        return ProcessITest.deployProcessAndDependencies();
    }

    @Test
    public void testProcessInIntegrationFluent() throws Exception {
        newProcessInstance(TWITTER_POSTING_PROCESS);
        processInstance().start();

        verify(postTweetDelegate).execute(any(DelegateExecution.class));

        assertThat(processInstance().getVariable(TWEET_RESULT)).exists().isDefined().asString().isEqualTo("ok");

        assertThat(processInstance()).isWaitingAt(APPROVE_TWEET_USER_TASK);

        processInstance().task().complete();

        assertThat(processInstance()).isFinished();
    }
}
