package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 链路上下文
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@Data
public class ChainContext<T> implements Serializable {

    private static final long serialVersionUID = -4351960350192549045L;

    /**
     * 上下文信息
     */
    protected T contextInfo;

    /**
     * 日志
     */
    protected String logStr;

    /**
     * 节点耗时map
     */
    protected Map<Class<? extends AbstractNode>, Long> nodeTimeMap = new HashMap<>();


    private ChainContext(T contextInfo, String logStr) {
        this.contextInfo = contextInfo;
        setLogStr(logStr);
    }

    private ChainContext() {
    }

    /**
     * 初始化数据，并进行数据校验
     *
     * @param contextInfo contextInfo
     * @param logStr      logStr
     * @return ChainContext
     */
    public static <T> ChainContext<T> create(@NonNull T contextInfo, @NonNull String logStr) {
        return new ChainContext<>(contextInfo, logStr + " method [" + Thread.currentThread().getStackTrace()[2].getMethodName() + "]");
    }

    public static <T> ChainContext<T> create(@NonNull Class<T> clazz) {
        return new ChainContext<>(createChainContext(clazz), "method [" + Thread.currentThread().getStackTrace()[2].getMethodName() + "]");
    }

    public static <T> ChainContext<T> create(@NonNull T contextInfo) {
        return new ChainContext<>(contextInfo, "method [" + Thread.currentThread().getStackTrace()[2].getMethodName() + "]");
    }

    public static <T> ChainContext<T> create(@NonNull Class<T> clazz, @NonNull String logStr) {
        return new ChainContext<>(createChainContext(clazz), logStr + " method [" + Thread.currentThread().getStackTrace()[2].getMethodName() + "]");
    }

    /**
     * 拼接日志
     *
     * @param logStr logStr
     */
    public void setLogStr(String logStr) {
        if (StringUtils.isEmpty(this.logStr)) {
            this.logStr = logStr;
        } else {
            this.logStr += " " + logStr;
        }
    }

    /**
     * 反射创建对象
     *
     * @param clazz clazz
     * @return AbstractNode
     */
    private static <T> T createChainContext(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            log.info("process createChainContext reflex create object fail clazz={} error=", clazz, e);
            return null;
        }
    }

}