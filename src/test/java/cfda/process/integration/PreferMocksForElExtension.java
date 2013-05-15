package cfda.process.integration;

import ch.qos.logback.classic.Logger;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Extension;
import java.util.Set;

class PreferMocksForElExtension implements Extension {


    public PreferMocksForElExtension() {
    }

    public void shutdownScheduler(@Observes javax.enterprise.inject.spi.ProcessAnnotatedType pat) {
        Set<AnnotatedField> fields = pat.getAnnotatedType().getFields();
        for (AnnotatedField field : fields) {
            if (field.isAnnotationPresent(Mock.class)) ;
            System.out.printf(">>>>>>>>>>>>>>>>>>>>>>> %s %s",pat,field);
        }
    }
}