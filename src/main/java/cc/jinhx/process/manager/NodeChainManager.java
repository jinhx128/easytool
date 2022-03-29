package cc.jinhx.process.manager;

import cc.jinhx.process.chain.AbstractNodeChain;
import cc.jinhx.process.annotation.NodeChain;
import cc.jinhx.process.enums.NodeChainLogLevelEnums;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 节点链管理器
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class NodeChainManager {

    private static Map<String, AbstractNodeChain> MAP = new HashMap<>();

    public void addNodeChain(String key, AbstractNodeChain abstractNodeChain) {
        MAP.put(key, abstractNodeChain);
    }

    public static AbstractNodeChain getNodeChain(Class<? extends AbstractNodeChain> clazz) {
        return getNodeChain(clazz, null);
    }

    /**
     * 从全局唯一MAP获取实例，不存在则反射创建返回，并存入MAP
     *
     * @param clazz clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    public static AbstractNodeChain getNodeChain(Class<? extends AbstractNodeChain> clazz, Integer logLevel) {
        String key = clazz.getName() + logLevel;
        if (MAP.containsKey(key)){
            return MAP.get(key);
        }

        AbstractNodeChain abstractNodeChain = createNodeChain(clazz, logLevel);
        if (Objects.nonNull(abstractNodeChain)){
            MAP.put(key, abstractNodeChain);
        }

        return abstractNodeChain;
    }

    /**
     * 反射创建单例，并且自动从spring获取bean注入，指的是同种类型的不同属性，比如相同的logLevel属性只会存在一个，不同的会存在多个
     *
     * @param clazz clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    private static AbstractNodeChain createNodeChain(Class<? extends AbstractNodeChain> clazz, Integer logLevel) {
        try {
            if (Objects.isNull(clazz.getAnnotation(NodeChain.class))) {
                log.error("act=createNodeChain 反射创建单例失败，类上缺少@NodeChain注解 clazz={} logLevel={}", clazz, logLevel);
                return null;
            }

            Constructor<? extends AbstractNodeChain> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            AbstractNodeChain abstractNodeChain = constructor.newInstance();
            if (NodeChainLogLevelEnums.containsCode(logLevel)){
                Method method = clazz.getMethod("setLogLevel", Integer.class);
                // 跳过了访问检查，并提高效率
                method.setAccessible(true);
                method.invoke(abstractNodeChain, logLevel);
            }
            return abstractNodeChain;
        }catch (Exception e){
            log.error("act=createNodeChain 反射创建单例失败 clazz={} logLevel={} error={}", clazz, logLevel, e);
            return null;
        }
    }

}
