package foolkey.controller.GR_test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by GR on 2017/5/3.
 */
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface TestG1 {
}
