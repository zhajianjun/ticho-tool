package top.ticho.tool.json.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.ticho.tool.json.serializer.TiSerializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhajianjun
 * @date 2024-12-28 21:50
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = TiSerializer.class)
@Documented
public @interface TiRender {

    Class<? extends TiRendering<?, ?>> serializer() default TiDefaultRendering.class;

    String[] params() default {};

}
