package top.ticho.tool.json.serializer;

import top.ticho.tool.json.annotation.TiRendering;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2024-12-29 00:05
 */
public class TiSerializerFactory {
    private static final Map<Class<?>, TiRendering<?, ?>> map = new ConcurrentHashMap<>();

    public static TiRendering<?, ?> getSerializer(Class<? extends TiRendering<?, ?>> clazz) {
        if (clazz.isInterface()) {
            throw new UnsupportedOperationException("Serializer is interface, what is expected is an implementation class !");
        }
        return map.computeIfAbsent(
            clazz,
            key -> {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new UnsupportedOperationException(e.getMessage(), e);
                }
            }
        );
    }

}
