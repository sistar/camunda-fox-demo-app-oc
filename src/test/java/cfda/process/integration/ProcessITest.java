package cfda.process.integration;

import cfda.AuditPost;
import cfda.delegates.PostTweetDelegate;
import cfda.util.Log;
import org.camunda.bpm.engine.test.fluent.FluentProcessEngineTestRule;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.inject.Inject;

import static cfda.process.integration.CommonsModulesBase.foxEngineDependencies;

@SuppressWarnings("SpringJavaAutowiringInspection")
public class ProcessITest {
    @Rule
    public FluentProcessEngineTestRule
            bpmnFluentTestRule = new FluentProcessEngineTestRule(this);
    @Inject
    @Log
    Logger logger;

    public static Archive<?> deployProcessAndDependencies() {
        long start = System.currentTimeMillis();
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "twitter-posting-test.war");
        foxEngineDependencies(webArchive);
        webArchive.addClass(ProcessITest.class);
        webArchive.addClass(PostTweetDelegate.class)
                .addAsResource("META-INF/persistence.xml")
                .addClass(AuditPost.class)
                .addAsResource("diagrams/twitter-posting.bpmn");
        long end = System.currentTimeMillis();
        //System.out.println(webArchive.toString(true));
        //System.out.printf("webarchive time millis: %s\n", end - start);
        return webArchive;
    }

    @Before
    public void setUp() throws Exception {
        bpmnFluentTestRule.before();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        bpmnFluentTestRule.after();
    }
}
