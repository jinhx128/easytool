package cc.jinhx.easytool.process.topology;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * 拓扑图上下文
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@Data
public class TopologyContext<T> implements Serializable {

    private static final long serialVersionUID = -4351960350192549045L;

    /**
     * 上下文信息
     */
    protected T contextInfo;

    /**
     * 日志
     */
    protected String logStr;

    /*w
     * 是否需要执行下一组节点，默认需要
     */
    protected boolean executeNextNodeGroup;

    private TopologyContext(T contextInfo, String logStr) {
        this.contextInfo = contextInfo;
        this.executeNextNodeGroup = true;
        setLogStr(logStr);
    }

    private TopologyContext() {
        this.executeNextNodeGroup = true;
    }

    public boolean getExecuteNextNodeGroup(){
        return this.executeNextNodeGroup;
    }

    /**
     * 初始化数据，并进行数据校验
     *
     * @param contextInfo contextInfo
     * @param logStr      logStr
     * @return TopologyContext
     */
    public static <T> TopologyContext<T> create(@NonNull T contextInfo, @NonNull String logStr) {
        return new TopologyContext<>(contextInfo, logStr + " method [" + Thread.currentThread().getStackTrace()[4].getMethodName() + "]");
    }

    public static <T> TopologyContext<T> create(@NonNull Class<T> clazz) {
        return new TopologyContext<>(createTopologyContext(clazz), "method [" + Thread.currentThread().getStackTrace()[4].getMethodName() + "]");
    }

    public static <T> TopologyContext<T> create(@NonNull T contextInfo) {
        return new TopologyContext<>(contextInfo, "method [" + Thread.currentThread().getStackTrace()[4].getMethodName() + "]");
    }

    public static <T> TopologyContext<T> create(@NonNull Class<T> clazz, @NonNull String logStr) {
        return new TopologyContext<>(createTopologyContext(clazz), logStr + " method [" + Thread.currentThread().getStackTrace()[4].getMethodName() + "]");
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
    private static <T> T createTopologyContext(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            log.info("process createTopologyContext reflex create object fail clazz={} error=", clazz, e);
            return null;
        }
    }

}
