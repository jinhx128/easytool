package cc.jinhx.process.manager;

import cc.jinhx.process.annotation.Node;
import cc.jinhx.process.enums.NodeFailHandleEnums;
import cc.jinhx.process.node.AbstractNode;
import cc.jinhx.process.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 节点管理器
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class NodeManager {

    private static Map<String, AbstractNode> MAP = new HashMap<>();

    public void addNode(String key, AbstractNode abstractNode) {
        MAP.put(key, abstractNode);
    }

    /**
     * 从全局唯一MAP获取实例，不存在则反射创建返回，并存入MAP
     *
     * @param clazz clazz
     * @param failHandle failHandle
     * @param timeout timeout
     * @return AbstractNode
     */
    public static AbstractNode getNode(Class<? extends AbstractNode> clazz, Integer failHandle, Long timeout) {
        String key = clazz.getName() + failHandle + timeout;
        if (MAP.containsKey(key)){
            return MAP.get(key);
        }

        AbstractNode abstractNode = createNode(clazz, failHandle, timeout);
        if (Objects.nonNull(abstractNode)){
            MAP.put(key, abstractNode);
        }

        return abstractNode;
    }

    /**
     * 反射创建单例，并且自动从spring获取bean注入，指的是同种类型的不同属性，比如相同的timeout属性只会存在一个，不同的会存在多个
     *
     * @param clazz clazz
     * @param failHandle failHandle
     * @param timeout timeout
     * @return AbstractNode
     */
    private static AbstractNode createNode(Class<? extends AbstractNode> clazz, Integer failHandle, Long timeout) {
        try {
            if (Objects.isNull(clazz.getAnnotation(Node.class))) {
                log.error("act=createNode 反射创建单例失败，类上缺少@Node注解 clazz={} failHandle={} timeout={}", clazz, failHandle, timeout);
                return null;
            }

            Constructor<? extends AbstractNode> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            AbstractNode abstractNode = constructor.newInstance();
            if (NodeFailHandleEnums.containsCode(failHandle)){
                Method setFailHandleMethod = clazz.getMethod("setFailHandle", Integer.class);
                // 跳过了访问检查，并提高效率
                setFailHandleMethod.setAccessible(true);
                setFailHandleMethod.invoke(abstractNode, failHandle);
            }
            if (Objects.nonNull(timeout) && timeout > 0L){
                Method setTimeoutMethod = clazz.getMethod("setTimeout", Long.class);
                // 跳过了访问检查，并提高效率
                setTimeoutMethod.setAccessible(true);
                setTimeoutMethod.invoke(abstractNode, timeout);
            }

            for (Field declaredField : clazz.getDeclaredFields()) {
                // 跳过了访问检查，并提高效率
                declaredField.setAccessible(true);
                if (Objects.isNull(declaredField.get(abstractNode))){
                    if (Objects.nonNull(declaredField.getAnnotation(Resource.class))) {
                        declaredField.set(abstractNode, SpringUtils.getBean(declaredField.getName(), declaredField.getType()));
                    } else if (Objects.nonNull(declaredField.getAnnotation(Autowired.class))) {
                        declaredField.set(abstractNode, SpringUtils.getBean(declaredField.getType()));
                    }
                }
            }
            return abstractNode;
        }catch (Exception e){
            log.error("act=createNode 反射创建单例失败 clazz={} failHandle={} timeout={} error={}", clazz, failHandle, timeout, e);
            return null;
        }
    }

}
