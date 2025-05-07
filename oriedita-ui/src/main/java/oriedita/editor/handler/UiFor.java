package oriedita.editor.handler;

import jakarta.inject.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Target({TYPE, PARAMETER, FIELD})
@Retention(RUNTIME)
@Documented
public @interface UiFor {
    MouseHandlerSettingGroup value() default MouseHandlerSettingGroup.NONE;
}
