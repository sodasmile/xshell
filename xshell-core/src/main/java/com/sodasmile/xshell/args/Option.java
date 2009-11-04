package com.sodasmile.xshell.args;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:runepeter@gmail.com">Rune Peter Bj&oslash;rnstad</a>
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface Option {

    String name() default "";

    String description() default "";

    char delimiter() default ',';

    boolean required() default false;

}
