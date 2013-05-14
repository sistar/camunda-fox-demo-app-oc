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
import org.camunda.bpm.engine.test.fluent.FluentProcessEngineTestRule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
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

    @Spy
    @Produces
    @Named(value = "postTweetDelegate")
    public static PostTweetDelegate postTweetDelegate;
    @Rule
    public FluentProcessEngineTestRule
            bpmnFluentTestRule = new FluentProcessEngineTestRule(this);
    @Inject
    @Log
    Logger logger;
    @Inject
    RuntimeService runtimeService;
    @Inject
    TaskService taskService;
    @Inject
    HistoryService historyService;

    @Deployment
    public static Archive<?> deployProcessAndDependencies() {
        long start = System.currentTimeMillis();
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "twitter-posting-test.war");
        foxEngineDependencies(webArchive);
        webArchive.addClass(PostTweetDelegate.class)
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
        MockitoAnnotations.initMocks(this);
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processDefinitionKey(C.TWITTER_POSTING_PROCESS).active().list();
        for (ProcessInstance processInstance : processInstances) {
            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(),"cleanup before test");
        }
    }

    @Test
    public void testProcessInIntegrationJpa() throws Exception {
        long start = System.currentTimeMillis();
        ProcessInstance twitterPosting = runtimeService.startProcessInstanceByKey(C.TWITTER_POSTING_PROCESS);
        String processInstanceId = twitterPosting.getProcessInstanceId();


        HistoricVariableInstance tweetResult = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId).orderByVariableName().desc().variableName(C.TWEET_RESULT).singleResult();

        verify(postTweetDelegate).execute(any(DelegateExecution.class));

        assertThat((String) tweetResult.getValue(), is(equalTo("ok")));

        Task task = taskService
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        assertThat(task.getAssignee(), is(equalTo("fte")));

        taskService.complete(task.getId());

        assertEquals(0, runtimeService.createProcessInstanceQuery().processDefinitionKey("TwitterPosting").count());

        long end = System.currentTimeMillis();
        System.out.printf("actual test time millis: %s\n", end - start);
    }
}
