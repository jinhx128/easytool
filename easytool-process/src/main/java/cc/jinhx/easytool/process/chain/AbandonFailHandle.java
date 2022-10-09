package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * 抛弃失败处理
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class AbandonFailHandle extends AbstractFailHandle {

    @Override
    protected <T> void dealFailNode(ChainContext<T> chainContext, ExecutorService executorService, Class<? extends AbstractNode> nodeClass,
                                    ChainParam<T> chainParam, Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap,
                                    Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeClassMap,
                                    AbstractChain chain, Throwable throwable, String logPrefix) {
        StringBuffer logStr = new StringBuffer(logPrefix);
        try {
            ChainNode chainNode = chainNodeMap.get(nodeClass);
            String nodeName = nodeClass.getSimpleName();
            long timeout = chainNode.getGetTimeout().getAsLong();
            AbstractNode node = chainNode.getNode();
            String exceptionLog = getExceptionLog((Exception) throwable);
            Throwable cause = throwable;
            if (Objects.nonNull(throwable.getCause())) {
                cause = throwable.getCause();
            }


            if (cause instanceof TimeoutException) {
                logStr.append(" node [").append(nodeName).append("] execute timeout fail timeout=").append(timeout);
            } else if (cause instanceof ProcessException) {
                logStr.append(" node [").append(nodeName).append("] execute process fail");
            } else if (cause instanceof BusinessException) {
                logStr.append(" node [").append(nodeName).append("] execute business fail");
            } else {
                logStr.append(" node [").append(nodeName).append("] execute unknown fail");
            }

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

            logStr.append(" abandon node msg=").append(exceptionLog);

        } catch (Exception e) {
            logStr.append(" abandon node dealFailNode fail msg=").append(getExceptionLog(e));
        } finally {
            chainParam.getNodeClassStatusMap().put(nodeClass, ChainParam.NodeStatusEnum.COMPLETED.getCode());
            chainParam.getCompletedNodeCountDownLatch().countDown();

            log.info(logStr.toString());

            chain.startRunNode(chainContext, executorService, childNodeClassMap.get(nodeClass), chainParam);
        }
    }

}