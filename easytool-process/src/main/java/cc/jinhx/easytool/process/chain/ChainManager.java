package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.SpringUtil;
import lombok.NonNull;
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
 * 链路管理器
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class ChainManager {

    private static Map<String, AbstractChain> MAP = new HashMap<>();

    public void addChain(String key, AbstractChain abstractChain) {
        MAP.put(key, abstractChain);
    }

    /**
     * 从全局唯一MAP获取实例，不存在则反射创建返回，并存入MAP
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    public static AbstractChain getChain(@NonNull Class<? extends AbstractChain> clazz, AbstractChain.LogLevelEnum logLevel) {
        String key = clazz.getName() + ":";
        if (Objects.isNull(logLevel)) {
            key += null;
        } else {
            key += logLevel.getCode();
        }

        if (MAP.containsKey(key)) {
            return MAP.get(key);
        }

        AbstractChain abstractChain = createChain(clazz, logLevel);
        if (Objects.nonNull(abstractChain)) {
            MAP.put(key, abstractChain);
        }

        return abstractChain;
    }

    /**
     * 反射创建单例，并且自动从spring获取bean注入，指的是同种类型的不同属性，比如相同的logLevel属性只会存在一个，不同的会存在多个
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    private static AbstractChain createChain(Class<? extends AbstractChain> clazz, AbstractChain.LogLevelEnum logLevel) {
        try {
            Constructor<? extends AbstractChain> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            AbstractChain abstractChain = constructor.newInstance();

            Method setNodeInfoMethod = clazz.getDeclaredMethod("setNodeInfo");
            // 跳过了访问检查，并提高效率
            setNodeInfoMethod.setAccessible(true);
            setNodeInfoMethod.invoke(abstractChain);

            Method checkChainCompleteMethod = clazz.getMethod("checkChainComplete");
            // 跳过了访问检查，并提高效率
            checkChainCompleteMethod.setAccessible(true);
            checkChainCompleteMethod.invoke(abstractChain);

            if (Objects.nonNull(logLevel) && AbstractChain.LogLevelEnum.containsCode(logLevel.getCode())) {
                Method setLogLevelMethod = clazz.getMethod("setLogLevel", AbstractChain.LogLevelEnum.class);
                // 跳过了访问检查，并提高效率
                setLogLevelMethod.setAccessible(true);
                setLogLevelMethod.invoke(abstractChain, logLevel);
            }

            for (Field declaredField : clazz.getDeclaredFields()) {
                // 跳过了访问检查，并提高效率
                declaredField.setAccessible(true);
                String name = declaredField.getName();
                Class<?> type = declaredField.getType();
                if (Objects.isNull(declaredField.get(abstractChain))) {
                    if (Objects.nonNull(declaredField.getAnnotation(Resource.class))) {
                        Object bean = null;

                        try {
                            if (SpringUtil.containsBean(name) && SpringUtil.isTypeMatch(name, type)) {
                                bean = SpringUtil.getBean(name, type);
                            }
                        } catch (Exception e) {
                            log.info("process createChain getBeanByNameAndType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        try {
                            bean = SpringUtil.getBean(type);
                        } catch (Exception e) {
                            log.info("process createChain getBeanByType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        declaredField.set(abstractChain, bean);
                    } else if (Objects.nonNull(declaredField.getAnnotation(Autowired.class))) {
                        Object bean = null;

                        try {
                            if (SpringUtil.containsBean(name) && SpringUtil.isTypeMatch(name, type)) {
                                bean = SpringUtil.getBean(name, type);
                            }
                        } catch (Exception e) {
                            log.info("process createChain getBeanByNameAndType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        try {
                            bean = SpringUtil.getBean(type);
                        } catch (Exception e) {
                            log.info("process createChain getBeanByType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        declaredField.set(abstractChain, bean);
                    }
                }
            }

            return abstractChain;
        } catch (Exception e) {
            log.info("process createChain reflex create object fail clazz={} logLevel={} error=", clazz, logLevel, e);
            return null;
        }
    }

}
