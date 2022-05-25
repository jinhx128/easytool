package cc.jinhx.easytool.process;

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

    /**
     * 从全局唯一MAP获取实例，不存在则反射创建返回，并存入MAP
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    public static AbstractNodeChain getNodeChain(Class<? extends AbstractNodeChain> clazz, AbstractNodeChain.LogLevelEnum logLevel) {
        String key = clazz.getName() + ":";
        if (Objects.isNull(logLevel)) {
            key += null;
        } else {
            key += logLevel.getCode();
        }

        if (MAP.containsKey(key)) {
            return MAP.get(key);
        }

        AbstractNodeChain abstractNodeChain = createNodeChain(clazz, logLevel);
        if (Objects.nonNull(abstractNodeChain)) {
            MAP.put(key, abstractNodeChain);
        }

        return abstractNodeChain;
    }

    /**
     * 反射创建单例，并且自动从spring获取bean注入，指的是同种类型的不同属性，比如相同的logLevel属性只会存在一个，不同的会存在多个
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    private static AbstractNodeChain createNodeChain(Class<? extends AbstractNodeChain> clazz, AbstractNodeChain.LogLevelEnum logLevel) {
        try {
            Constructor<? extends AbstractNodeChain> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            AbstractNodeChain abstractNodeChain = constructor.newInstance();
            Method setNodeInfoMethod = clazz.getDeclaredMethod("setNodeInfo");
            // 跳过了访问检查，并提高效率
            setNodeInfoMethod.setAccessible(true);
            setNodeInfoMethod.invoke(abstractNodeChain);
            if (Objects.nonNull(logLevel) && AbstractNodeChain.LogLevelEnum.containsCode(logLevel.getCode())) {
                Method setLogLevelMethod = clazz.getMethod("setLogLevel", AbstractNodeChain.LogLevelEnum.class);
                // 跳过了访问检查，并提高效率
                setLogLevelMethod.setAccessible(true);
                setLogLevelMethod.invoke(abstractNodeChain, logLevel);
            }

            for (Field declaredField : clazz.getDeclaredFields()) {
                // 跳过了访问检查，并提高效率
                declaredField.setAccessible(true);
                String name = declaredField.getName();
                Class<?> type = declaredField.getType();
                if (Objects.isNull(declaredField.get(abstractNodeChain))) {
                    if (Objects.nonNull(declaredField.getAnnotation(Resource.class))) {
                        Object bean = null;

                        try {
                            if (SpringUtils.containsBean(name) && SpringUtils.isTypeMatch(name, type)){
                                bean = SpringUtils.getBean(name, type);
                            }
                        } catch (Exception e){
                            log.info("process createNodeChain getBeanByNameAndType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        try {
                            bean = SpringUtils.getBean(type);
                        } catch (Exception e){
                            log.info("process createNodeChain getBeanByType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        declaredField.set(abstractNodeChain, bean);
                    } else if (Objects.nonNull(declaredField.getAnnotation(Autowired.class))) {
                        Object bean = null;

                        try {
                            if (SpringUtils.containsBean(name) && SpringUtils.isTypeMatch(name, type)){
                                bean = SpringUtils.getBean(name, type);
                            }
                        } catch (Exception e){
                            log.info("process createNodeChain getBeanByNameAndType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        try {
                            bean = SpringUtils.getBean(type);
                        } catch (Exception e){
                            log.info("process createNodeChain getBeanByType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        declaredField.set(abstractNodeChain, bean);
                    }
                }
            }

            return abstractNodeChain;
        } catch (Exception e) {
            log.info("process createNodeChain reflex create object fail clazz={} logLevel={} error=", clazz, logLevel, e);
            return null;
        }
    }

}
