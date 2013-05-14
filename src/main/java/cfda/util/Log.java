package cfda.util;

import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Log {
}
