package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 抽象失败处理
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@Slf4j
public abstract class AbstractFailHandle {

    protected static final String LOG_PREFIX = "process chainLog ";

    /**
     * 获取拼接错误日志
     *
     * @param e e
     * @return 拼接错误日志
     */
    protected String getExceptionLog(Exception e) {
        if (Objects.nonNull(e)) {
            StringBuilder stringBuffer = new StringBuilder("\n");
            if (Objects.nonNull(e.getMessage())) {
                stringBuffer.append("process ").append(e.getMessage()).append("\n");
            }
            if (Objects.nonNull(e.getCause())) {
                StackTraceElement[] stackTrace = e.getCause().getStackTrace();
                if (Objects.nonNull(stackTrace) && stackTrace.length > 0) {
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        stringBuffer.append("process ").append(stackTraceElement.toString()).append("\n");
                    }
                    return stringBuffer.toString();
                }
            }
        }

        return null;
    }

    /**
     * 构建失败结果
     */
    protected <T> ProcessResult<T> buildFailResult(int code, String msg) {
        return new ProcessResult<>(code, msg);
    }

    /**
     * 中断链路
     *
     * @param chainParam   chainParam
     * @param chainNodeMap chainNodeMap
     */
    protected <T> void interruptChain(ChainParam<T> chainParam, Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap) {
        chainParam.getNodeClassStatusMap().putAll(chainNodeMap.entrySet().stream().collect(Collectors.toConcurrentMap(Map.Entry::getKey, v -> ChainParam.NodeStatusEnum.COMPLETED.getCode(), (v1, v2) -> v2)));
        while (chainParam.getSuccessNodeCountDownLatch().getCount() > 0) {
            chainParam.getSuccessNodeCountDownLatch().countDown();
        }
    }

    /**
     * 获取是否最后一次执行该节点
     *
     * @param nodeClass  nodeClass
     * @param chainParam chainParam
     * @param chainNode  chainNode
     * @return 是否最后一次执行该节点
     */
    protected <T> boolean getIsLastTimes(Class<? extends AbstractNode> nodeClass, ChainParam<T> chainParam, ChainNode chainNode) {
        return ChainNode.FailHandleEnum.RETRY.getCode() != chainNode.getFailHandle().getCode() || chainNode.getRetryTimes().getCode() == chainParam.getNodeClassRetryCountMap().get(nodeClass);
    }

    /**
     * 处理失败节点
     */
    protected abstract <T> void dealFailNode(ChainContext<T> chainContext, ExecutorService executorService, Class<? extends AbstractNode> nodeClass,
                                         ChainParam<T> chainParam, Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap,
                                         Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeClassMap,
                                         AbstractChain chain, Throwable throwable);

}