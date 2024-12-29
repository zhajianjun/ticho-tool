package top.ticho.tool.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import top.ticho.tool.json.annotation.TiRender;
import top.ticho.tool.json.annotation.TiRendering;

import java.io.IOException;

/**
 * @author zhajianjun
 * @date 2024-12-28 23:57
 */
public class TiSerializer extends StdSerializer<Object> implements ContextualSerializer {
    private TiRendering<Object, Object> tiRendering;
    private String[] params;

    public TiSerializer() {
        super(Object.class);
    }

    public void setDictRendering(TiRendering<Object, Object> tiRendering) {
        this.tiRendering = tiRendering;
    }

    public void setParams(String... params) {
        this.params = params;
    }

    @Override
    public JsonSerializer<Object> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            TiRender tiRender = beanProperty.getAnnotation(TiRender.class);
            if (tiRender == null) {
                tiRender = beanProperty.getContextAnnotation(TiRender.class);
            }
            if (tiRender != null) {
                TiSerializer tiSerializer = new TiSerializer();
                tiSerializer.setDictRendering((TiRendering<Object, Object>) TiSerializerFactory.getSerializer(tiRender.serializer()));
                tiSerializer.setParams(tiRender.params());
                return tiSerializer;
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }


    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (tiRendering == null) {
            gen.writeObject(value);
            return;
        }
        try {
            Object render = tiRendering.render(value, params);
            gen.writeObject(render);
        } catch (Exception e) {
            tiRendering.errorHandle(e, value, params);
            gen.writeObject(value);
        }
    }

}
