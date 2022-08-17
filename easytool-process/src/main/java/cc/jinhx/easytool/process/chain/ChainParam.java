package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 链路参数
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@Data
public class ChainParam<T> {

    /**
     * 线程上下文配置
     */
    private Map<Object, AbstractThreadContextConfig> threadContextInitConfigMap;

    /**
     * 线程上下文配置
     */
    private Set<AbstractThreadContextConfig> threadContextInitConfigSet;

    /**
     * 所有节点状态
     */
    private Map<Class<? extends AbstractNode>, Boolean> nodeClassStatusMap;

    /**
     * 所有节点重试次数
     */
    private Map<Class<? extends AbstractNode>, Integer> nodeClassRetryCountMap;

    /**
     * 成功节点计数器
     */
    private CountDownLatch successNodeCountDownLatch;

    /**
     * 结果
     */
    private ProcessResult<T> processResult;

    /**
     * 失败异常
     */
    private Exception failException;

    /**
     * 是否是业务失败
     */
    private boolean isBusinessFail;

    /**
     * 是否是超时失败
     */
    private boolean isTimeoutFail;

}
