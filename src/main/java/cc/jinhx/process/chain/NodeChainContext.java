package cc.jinhx.process.chain;

import cc.jinhx.process.enums.ExceptionEnums;
import cc.jinhx.process.exception.ProcessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * 节点链上下文
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@Data
public class NodeChainContext<T> implements Serializable {

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
    protected Boolean exNextNodeGroup = true;

    private NodeChainContext(T contextInfo, String logStr) {
        this.contextInfo = contextInfo;
        setLogStr(logStr);
    }

    private NodeChainContext() {
    }

    /**
     * 初始化数据，并进行数据校验
     *
     * @param contextInfo contextInfo
     * @param logStr logStr
     * @return NodeChainContext
     */
    public static <T> NodeChainContext<T> create(T contextInfo, String logStr) {
        if (Objects.isNull(contextInfo)){
            throw new ProcessException(ExceptionEnums.NODE_CHAIN_CONTEXT_INFO_NOT_NULL);
        }

        if (StringUtils.isEmpty(logStr)){
            throw new ProcessException(ExceptionEnums.NODE_CHAIN_LOG_STR_NOT_NULL);
        }
        return new NodeChainContext<>(contextInfo, logStr + " act=" + Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    public static <T> NodeChainContext<T> create(Class<T> clazz) {
        return new NodeChainContext<>(createNodeChainContext(clazz), "act=" + Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    public static <T> NodeChainContext<T> create(T contextInfo) {
        return new NodeChainContext<>(contextInfo, "act=" + Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    public static <T> NodeChainContext<T> create(Class<T> clazz, String logStr) {
        if (Objects.isNull(clazz)){
            throw new ProcessException(ExceptionEnums.NODE_CHAIN_CLASS_NOT_NULL);
        }

        if (StringUtils.isEmpty(logStr)){
            throw new ProcessException(ExceptionEnums.NODE_CHAIN_LOG_STR_NOT_NULL);
        }
        return new NodeChainContext<>(createNodeChainContext(clazz), logStr + " act=" + Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    /**
     * 拼接日志
     *
     * @param logStr logStr
     */
    public void setLogStr(String logStr){
        if (StringUtils.isEmpty(this.logStr)){
            this.logStr = logStr;
        }else {
            this.logStr += " " + logStr;
        }
    }

    /**
     * 反射创建对象
     *
     * @param clazz clazz
     * @return AbstractNode
     */
    private static <T> T createNodeChainContext(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            // 跳过了访问检查，并提高效率
            constructor.setAccessible(true);
            return constructor.newInstance();
        }catch (Exception e){
            log.error("act=createNodeChainContext 反射创建对象失败 clazz={} error={}", clazz, e);
            return null;
        }
    }

}
