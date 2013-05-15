package cfda.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Default;
import javax.inject.Named;

@Named
@Default
public class PostTweetDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("i am called by camunda bpm process instance [%s]",execution.getProcessInstanceId());
        execution.setVariable("tweet-result","ok");
    }
}
