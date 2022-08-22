package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
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
                                AbstractChain chain, Throwable throwable) {
        StringBuffer logStr = new StringBuffer(LOG_PREFIX + chainContext.getLogStr());
        ChainNode chainNode = chainNodeMap.get(nodeClass);
        String nodeName = nodeClass.getName();
        long timeout = chainNode.getTimeout();
        AbstractNode node = chainNode.getNode();
        ProcessResult<T> processResult;
        String exceptionLog = getExceptionLog((Exception) throwable);
        Throwable cause = throwable.getCause();

        if (cause instanceof TimeoutException) {
            logStr.append(" execute timeout fail node [").append(nodeName).append("]").append(" timeout=").append(timeout);
            processResult = buildFailResult(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), ProcessException.MsgEnum.NODE_TIMEOUT.getMsg() + "=" + nodeName);
        } else if (cause instanceof ProcessException) {
            logStr.append(" execute process fail node [").append(nodeName).append("]");
            processResult = buildFailResult(((ProcessException) cause).getCode(), ((ProcessException) cause).getMsg());
        } else if (cause instanceof BusinessException) {
            logStr.append(" execute business fail node [").append(nodeName).append("]");
            processResult = buildFailResult(((BusinessException) cause).getCode(), ((BusinessException) cause).getMsg());
        } else {
            logStr.append(" execute unknown fail node [").append(nodeName).append("]");
            processResult = buildFailResult(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
        }

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

        logStr.append(" interrupt node msg=").append(exceptionLog);
        log.info(logStr.toString());

        interruptChain(chainParam, chainNodeMap);
    }

}