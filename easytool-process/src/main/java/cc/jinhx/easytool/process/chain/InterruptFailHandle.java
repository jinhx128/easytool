package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ProcessResult;
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
 * 中断失败处理
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class InterruptFailHandle extends AbstractFailHandle {

    @Override
    protected <T> void dealFailNode(ChainContext<T> chainContext, ExecutorService executorService, Class<? extends AbstractNode> nodeClass,
                                    ChainParam<T> chainParam, Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap,
                                    Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeClassMap,
                                    AbstractChain chain, Throwable throwable, String logPrefix) {
        StringBuffer logStr = new StringBuffer(logPrefix);
        try {
            ChainNode chainNode = chainNodeMap.get(nodeClass);
            String nodeName = nodeClass.getSimpleName();
            long nodeTimeout = chainNode.getGetNodeTimeout().getAsLong();
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
                logStr.append(" node [").append(nodeName).append(" execute process fail");
                processResult = buildFailResult(((ProcessException) cause).getCode(), ((ProcessException) cause).getMsg());
            } else if (cause instanceof BusinessException) {
                logStr.append(" node [").append(nodeName).append("] execute business fail");
                processResult = buildFailResult(((BusinessException) cause).getCode(), ((BusinessException) cause).getMsg());
            } else {
                logStr.append(" node [").append(nodeName).append("]").append(" execute unknown fail");
                processResult = buildFailResult(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
            }

            logStr.append(" interrupt node msg=").append(exceptionLog).append("\n");

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
        } catch (Exception e) {
            logStr.append(" interrupt node dealFailNode fail msg=").append(getExceptionLog(e));
        } finally {
            log.info(logStr.toString());

            interruptChain(chainParam, chainNodeMap);
        }
    }

}