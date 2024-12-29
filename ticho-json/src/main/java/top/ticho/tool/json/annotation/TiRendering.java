package top.ticho.tool.json.annotation;

/**
 * @author zhajianjun
 * @date 2024-12-28 21:50
 */
public interface TiRendering<T, R> {

    R render(T object, String[] params);

    default void errorHandle(Exception e, T object, String[] params) {

    }

    default R defaultValue() {
        return null;
    }

}
