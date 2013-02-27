import org.activiti.engine.RuntimeService;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyBusinessProcessTest {

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment  (resources = "diagrams/twitter-posting.bpmn")
    public void ruleUsageExample() {
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        runtimeService.startProcessInstanceByKey("TwitterPosting");


        assertEquals(0, runtimeService.createProcessInstanceQuery().count());
    }
}