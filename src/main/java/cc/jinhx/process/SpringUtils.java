package cc.jinhx.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 加载和获取spring上下文环境，支持xml文件配置方式和静态代码加载方式两种形式
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Component
@Slf4j
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public SpringUtils() {
        log.info("SpringUtils init");
    }

    /**
     * spring配置文件加载方式
     *
     * @param context context
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        SpringUtils.applicationContext = context;
    }

    /**
     * 应用启动时静态加载方式
     *
     * @param context context
     */
    public static void load(ApplicationContext context) {
        SpringUtils.applicationContext = context;
    }

    /**
     * 获取spring上下文 ApplicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        if (Objects.isNull(applicationContext)) {
            throw new ProcessException("ApplicationContext is null");
        }
        return applicationContext;
    }

    /**
     * 根据名称注册bean
     *
     * @param clazz clazz
     * @param beanName beanName
     */
    public static <T> void registerBean(Class<T> clazz, String beanName) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(clazz.getName());
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    /**
     * 根据名称获取bean
     *
     * @param name name
     * @return T
     */
    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz clazz
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 根据类型和名称获取bean
     *
     * @param name name
     * @param clazz clazz
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 获取属性
     *
     * @param key key
     * @return String
     */
    public static String getProperty(String key) {
        return getApplicationContext().getBean(Environment.class).getProperty(key);
    }

}
