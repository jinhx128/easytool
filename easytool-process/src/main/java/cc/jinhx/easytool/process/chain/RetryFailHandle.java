package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * 重试失败处理
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class RetryFailHandle extends AbstractFailHandle {

    @Override
    protected <T> void dealFailNode(ChainContext<T> chainContext, ExecutorService executorService, Class<? extends AbstractNode> nodeClass,
                                    ChainParam<T> chainParam, Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap,
                                    Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeClassMap,
                                    AbstractChain chain, Throwable throwable, String logPrefix) {
        StringBuffer logStr = new StringBuffer(logPrefix);
        ChainNode chainNode = chainNodeMap.get(nodeClass);
        boolean isLastTimes = getIsLastTimes(nodeClass, chainParam, chainNode);
        int retryCount = chainParam.getNodeClassRetryCountMap().get(nodeClass);
        try {
            String nodeName = nodeClass.getSimpleName();
            long nodeTimeout = chainNode.getGetNodeTimeout().getAsLong();
            ChainNode.RetryTimesEnum retryTimes = chainNode.getRetryTimes();
            AbstractNode node = chainNode.getNode();
            ProcessResult<T> processResult;
            String exceptionLog = getExceptionLog((Exception) throwable);
            Throwable cause = throwable;
            if (Objects.nonNull(throwable.getCause())) {
                cause = throwable.getCause();
            }

            if (cause instanceof TimeoutException) {
                logStr.append(" node [").append(nodeName).append("] execute timeout fail nodeTimeout=").append(nodeTimeout);
                processResult = buildFailResult(ProcessResult.BaseEnum.TIMEOUT_FAIL.getCode(), ProcessException.MsgEnum.NODE_TIMEOUT.getMsg() + "=" + nodeName);
            } else if (cause instanceof ProcessException) {
                logStr.append(" node [").append(nodeName).append("] execute process fail");
                processResult = buildFailResult(((ProcessException) cause).getCode(), ((ProcessException) cause).getMsg());
            } else if (cause instanceof BusinessException) {
                logStr.append(" node [").append(nodeName).append("] execute business fail");
                processResult = buildFailResult(((BusinessException) cause).getCode(), ((BusinessException) cause).getMsg());
            } else {
                logStr.append(" node [").append(nodeName).append("] execute unknown fail");
                processResult = buildFailResult(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
            }

            if (isLastTimes) {
                logStr.append(" stop retry node interrupt node retryTimes=").append(retryTimes.getCode()).append(" retryCount=").append(retryCount).append(" msg=").append(exceptionLog).append("\n");
            } else {
                logStr.append(" start retry node retryTimes=").append(retryTimes.getCode()).append(" retryCount=").append(retryCount + 1).append(" msg=").append(exceptionLog).append("\n");
            }

            if (isLastTimes) {
                chainParam.setFailException((Exception) cause);

                if (cause instanceof TimeoutException) {
                    node.onTimeoutFail(chainContext);
                    chainParam.setTimeoutFail(true);
                } else if (cause instanceof ProcessException) {
                    node.onUnknowFail(chainContext, (Exception) cause);
                } else if (cause instanceof BusinessException) {
                    node.onBusinessFail(chainContext, (BusinessException) cause);
                    chainParam.setBusinessFail(true);
                } else {
                    node.onUnknowFail(chainContext, (Exception) cause);
                }

                chainParam.setProcessResult(processResult);

                node.afterExecute(chainContext);
            }
        } catch (Exception e) {
            logStr.append(" retry node dealFailNode fail msg=").append(getExceptionLog(e));
        } finally {
            log.info(logStr.toString());

            if (!isLastTimes) {
                // 重试次数加1
                chainParam.getNodeClassRetryCountMap().put(nodeClass, retryCount + 1);
                chainParam.getNodeClassStatusMap().put(nodeClass, ChainParam.NodeStatusEnum.RETRYING.getCode());
                chain.startRunNode(chainContext, executorService, Collections.singleton(nodeClass), chainParam);
            } else {
                interruptChain(chainParam, chainNodeMap);
            }
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
    private <T> boolean getIsLastTimes(Class<? extends AbstractNode> nodeClass, ChainParam<T> chainParam, ChainNode chainNode) {
        return ChainNode.FailHandleEnum.RETRY.getCode() != chainNode.getFailHandle().getCode() || chainNode.getRetryTimes().getCode() == chainParam.getNodeClassRetryCountMap().get(nodeClass);
    }

}