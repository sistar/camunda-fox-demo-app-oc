package cfda.process.integration;

import cfda.AuditPost;
import cfda.delegates.PostTweetDelegate;
import cfda.process.test.C;
import cfda.util.Log;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static cfda.process.integration.CommonsModulesBase.foxEngineDependencies;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@SuppressWarnings("SpringJavaAutowiringInspection")
@RunWith(Arquillian.class)
public class TwitterPostingProcessITest2 {

    @Inject
    @Log
    Logger logger;

    @Inject
    RuntimeService runtimeService;
    @Inject
    TaskService taskService;
    @Inject
    HistoryService historyService;

    private cfda.process.integration.Mocks mocks;

    @Deployment
    public static Archive<?> deployProcessAndDependencies() {
        long start = System.currentTimeMillis();
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "twitter-posting-test.war");
        foxEngineDependencies(webArchive);
        webArchive.addClass(PostTweetDelegate.class).addClass(Mocks.class)
                .addAsResource("META-INF/persistence.xml")
                .addClass(AuditPost.class)
                .addAsResource("diagrams/twitter-posting.bpmn");
        long end = System.currentTimeMillis();
        System.out.println(webArchive.toString(true));
        System.out.printf("webarchive time millis: %s\n", end - start);
        return webArchive;
    }

    @Before
    public void init() {
        this.mocks = new cfda.process.integration.Mocks();

        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processDefinitionKey(C.TWITTER_POSTING_PROCESS).active().list();
        for (ProcessInstance processInstance : processInstances) {
            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(),"cleanup before test");
        }
    }

    @Test
    public void arquillianRunThroughProcessTest() throws Exception {
        long start = System.currentTimeMillis();
        ProcessInstance twitterPosting = runtimeService.startProcessInstanceByKey(C.TWITTER_POSTING_PROCESS);
        String processInstanceId = twitterPosting.getProcessInstanceId();


        HistoricVariableInstance tweetResult = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).orderByVariableName().desc().variableName(C.TWEET_RESULT).singleResult();

        verify(mocks.postTweetDelegate).execute(any(DelegateExecution.class));

        assertThat((String) tweetResult.getValue(), is(equalTo("ok")));

        assertThat(task(processInstanceId).getAssignee(), is(equalTo("fte")));

        taskService.complete(task(processInstanceId).getId());

        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey(C.TWITTER_POSTING_PROCESS).count());

        long end = System.currentTimeMillis();
        System.out.printf("actual test time millis: %s\n", end - start);
    }

    private Task task(String processInstanceId) {
        return taskService
                    .createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
    }
}
