package top.ticho.tool.json.annotation;

import top.ticho.tool.json.serializer.TiSerializerFactory;

/**
 * @author zhajianjun
 * @date 2024-12-28 21:50
 */
public class TiDefaultRendering implements TiRendering<Object, Object> {
    private TiRendering<Object, Object> tiRendering;

    public TiDefaultRendering() {
        tiRendering = (object, params) -> object;
    }

    @Override
    public Object render(Object object, String[] params) {
        return tiRendering.render(object, params);
    }

    @Override
    public Object defaultValue() {
        return tiRendering.defaultValue();
    }

    public static void init(TiRendering<Object, Object> tiRendering) {
        TiDefaultRendering serializer = (TiDefaultRendering) TiSerializerFactory.getSerializer(TiDefaultRendering.class);
        serializer.tiRendering = tiRendering;
    }

}
