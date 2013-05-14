package cfda.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Factory-Klasse für die Injektion einer Logger-Instanz mittels CDI. Zu beachten ist an dieser Stelle, dass eine
 * Verwendung natürlich nur in vom CDI-Container verwalteten Beans möglich ist.
 */
public class LogFactory {

    @Produces
    @Log
    public Logger createLogger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

}