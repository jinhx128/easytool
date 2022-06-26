package cc.jinhx.easytool.process.topology;

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
 * 拓扑图管理器
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class TopologyManager {

    private static Map<String, AbstractTopology> MAP = new HashMap<>();

    public void addTopology(String key, AbstractTopology abstractTopology) {
        MAP.put(key, abstractTopology);
    }

    /**
     * 从全局唯一MAP获取实例，不存在则反射创建返回，并存入MAP
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    public static AbstractTopology getTopology(@NonNull Class<? extends AbstractTopology> clazz, AbstractTopology.LogLevelEnum logLevel) {
        String key = clazz.getName() + ":";
        if (Objects.isNull(logLevel)) {
            key += null;
        } else {
            key += logLevel.getCode();
        }

        if (MAP.containsKey(key)) {
            return MAP.get(key);
        }

        AbstractTopology abstractTopology = createTopology(clazz, logLevel);
        if (Objects.nonNull(abstractTopology)) {
            MAP.put(key, abstractTopology);
        }

        return abstractTopology;
    }

    /**
     * 反射创建单例，并且自动从spring获取bean注入，指的是同种类型的不同属性，比如相同的logLevel属性只会存在一个，不同的会存在多个
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     * @return AbstractNode
     */
    private static AbstractTopology createTopology(Class<? extends AbstractTopology> clazz, AbstractTopology.LogLevelEnum logLevel) {
        try {
            Constructor<? extends AbstractTopology> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            AbstractTopology abstractTopology = constructor.newInstance();
            Method setNodeInfoMethod = clazz.getDeclaredMethod("setNodeInfo");
            // 跳过了访问检查，并提高效率
            setNodeInfoMethod.setAccessible(true);
            setNodeInfoMethod.invoke(abstractTopology);
            if (Objects.nonNull(logLevel) && AbstractTopology.LogLevelEnum.containsCode(logLevel.getCode())) {
                Method setLogLevelMethod = clazz.getMethod("setLogLevel", AbstractTopology.LogLevelEnum.class);
                // 跳过了访问检查，并提高效率
                setLogLevelMethod.setAccessible(true);
                setLogLevelMethod.invoke(abstractTopology, logLevel);
            }

            for (Field declaredField : clazz.getDeclaredFields()) {
                // 跳过了访问检查，并提高效率
                declaredField.setAccessible(true);
                String name = declaredField.getName();
                Class<?> type = declaredField.getType();
                if (Objects.isNull(declaredField.get(abstractTopology))) {
                    if (Objects.nonNull(declaredField.getAnnotation(Resource.class))) {
                        Object bean = null;

                        try {
                            if (SpringUtil.containsBean(name) && SpringUtil.isTypeMatch(name, type)){
                                bean = SpringUtil.getBean(name, type);
                            }
                        } catch (Exception e){
                            log.info("process createTopology getBeanByNameAndType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        try {
                            bean = SpringUtil.getBean(type);
                        } catch (Exception e){
                            log.info("process createTopology getBeanByType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        declaredField.set(abstractTopology, bean);
                    } else if (Objects.nonNull(declaredField.getAnnotation(Autowired.class))) {
                        Object bean = null;

                        try {
                            if (SpringUtil.containsBean(name) && SpringUtil.isTypeMatch(name, type)){
                                bean = SpringUtil.getBean(name, type);
                            }
                        } catch (Exception e){
                            log.info("process createTopology getBeanByNameAndType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        try {
                            bean = SpringUtil.getBean(type);
                        } catch (Exception e){
                            log.info("process createTopology getBeanByType fail clazz={} name={} error=", clazz.getName(), name, e);
                        }

                        declaredField.set(abstractTopology, bean);
                    }
                }
            }

            return abstractTopology;
        } catch (Exception e) {
            log.info("process createTopology reflex create object fail clazz={} logLevel={} error=", clazz, logLevel, e);
            return null;
        }
    }

}
