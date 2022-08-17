package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * 获取失败处理
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class AbandonFailHandle<T> extends AbstractFailHandle<T> {

    @Override
    protected void dealFailNode(ChainContext<T> chainContext, ExecutorService executorService, Class<? extends AbstractNode> nodeClass,
                                ChainParam<T> chainParam, Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap,
                                Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeClassMap,
                                AbstractChain chain, Throwable throwable) {
        StringBuffer logStr = new StringBuffer(LOG_PREFIX + chainContext.getLogStr());
        ChainNode chainNode = chainNodeMap.get(nodeClass);
        String nodeName = nodeClass.getName();
        long timeout = chainNode.getTimeout();
        AbstractNode node = chainNode.getNode();
        boolean isLastTimes = getIsLastTimes(nodeClass, chainParam, chainNode);
        String exceptionLog = getExceptionLog((Exception) throwable);
        Throwable cause = throwable.getCause();

        if (cause instanceof TimeoutException) {
            logStr.append(" execute timeout fail node [").append(nodeName).append("]").append(" timeout=").append(timeout);
        } else if (cause instanceof ProcessException) {
            logStr.append(" execute process fail node [").append(nodeName).append("]");
        } else if (cause instanceof BusinessException) {
            logStr.append(" execute business fail node [").append(nodeName).append("]");
        } else {
            logStr.append(" execute unknown fail node [").append(nodeName).append("]");
        }

        if (isLastTimes) {
            if (cause instanceof TimeoutException) {
                node.onTimeoutFail(chainContext);
            } else if (cause instanceof ProcessException) {
                node.onUnknowFail(chainContext, (Exception) cause);
            } else if (cause instanceof BusinessException) {
                node.onBusinessFail(chainContext, (BusinessException) cause);
            } else {
                node.onUnknowFail(chainContext, (Exception) cause);
            }

            node.afterExecute(chainContext);
        }

        logStr.append(" abandon node msg=").append(exceptionLog);
        log.info(logStr.toString());

        chainParam.getNodeClassStatusMap().put(nodeClass, true);
        chainParam.getSuccessNodeCountDownLatch().countDown();

        chain.startRunNode(chainContext, executorService, childNodeClassMap.get(nodeClass), chainParam);
    }

}
