package cc.jinhx.process.handler;

import cc.jinhx.process.chain.AbstractNodeChain;
import cc.jinhx.process.chain.NodeChainContext;
import cc.jinhx.process.enums.ExceptionEnums;
import cc.jinhx.process.exception.BusinessException;
import cc.jinhx.process.exception.ProcessException;
import cc.jinhx.process.manager.NodeChainManager;
import cc.jinhx.process.result.BaseResult;
import cc.jinhx.process.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.StopWatch;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 抽象逻辑处理器
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
public abstract class AbstractLogicHandler<T> {

    private LogicHandlerBaseInfo logicHandlerBaseInfo;

    protected AbstractLogicHandler() {
        init(new LogicHandlerBaseInfo(), null);
    }

    protected AbstractLogicHandler(LogicHandlerBaseInfo logicHandlerBaseInfo) {
        if (Objects.isNull(logicHandlerBaseInfo)){
            throw new ProcessException(ExceptionEnums.LOGIC_HANDLER_BASE_INFO_NOT_NULL);
        }

        init(logicHandlerBaseInfo, null);
    }

    protected AbstractLogicHandler(LogicHandlerBaseInfo logicHandlerBaseInfo, String logStr) {
        if (Objects.isNull(logicHandlerBaseInfo)){
            throw new ProcessException(ExceptionEnums.LOGIC_HANDLER_BASE_INFO_NOT_NULL);
        }

        if (StringUtils.isEmpty(logStr)){
            throw new ProcessException(ExceptionEnums.LOGIC_HANDLER_LOG_STR_NOT_NULL);
        }

        init(logicHandlerBaseInfo, logStr);
    }

    private void init(LogicHandlerBaseInfo logicHandlerBaseInfo, String logStr){
        this.logicHandlerBaseInfo = logicHandlerBaseInfo;
        if (StringUtils.isEmpty(logStr)){
            logicHandlerBaseInfo.setLogStr("act=" + Thread.currentThread().getStackTrace()[4].getMethodName());
        }else {
            logicHandlerBaseInfo.setLogStr(logStr + " act=" + Thread.currentThread().getStackTrace()[4].getMethodName());
        }
    }

    protected void executeNodeChain(Class<? extends AbstractNodeChain> clazz, NodeChainContext<?> nodeChainContext, ThreadPoolExecutor threadPoolExecutor) {
        executeNodeChain(clazz, null, nodeChainContext, threadPoolExecutor);
    }

    protected void executeNodeChain(Class<? extends AbstractNodeChain> clazz, NodeChainContext<?> nodeChainContext) {
        executeNodeChain(clazz, null, nodeChainContext);
    }

    /**
     * 执行指定节点链
     *
     * @param clazz clazz
     * @param logLevel logLevel
     * @param nodeChainContext nodeChainContext
     * @param threadPoolExecutor threadPoolExecutor
     */
    protected void executeNodeChain(Class<? extends AbstractNodeChain> clazz, Integer logLevel, NodeChainContext<?> nodeChainContext, ThreadPoolExecutor threadPoolExecutor) {
        getNodeChain(clazz, logLevel).execute(nodeChainContext, threadPoolExecutor);
    }

    /**
     * 执行指定节点链
     *
     * @param clazz clazz
     * @param logLevel logLevel
     * @param nodeChainContext nodeChainContext
     */
    protected void executeNodeChain(Class<? extends AbstractNodeChain> clazz, Integer logLevel, NodeChainContext<?> nodeChainContext) {
        getNodeChain(clazz, logLevel).execute(nodeChainContext);
    }

    /**
     * 获取指定节点链
     *
     * @param clazz clazz
     * @param logLevel logLevel
     */
    private AbstractNodeChain getNodeChain(Class<? extends AbstractNodeChain> clazz, Integer logLevel) {
        AbstractNodeChain abstractNodeChain = NodeChainManager.getNodeChain(clazz, logLevel);
        if (Objects.isNull(abstractNodeChain)){
            throw new ProcessException(ExceptionEnums.NODE_CHAIN_UNREGISTERED.getMsg() + "=" + clazz.getName());
        }

        return abstractNodeChain;
    }

    /**
     * 参数校验
     */
    protected abstract void checkParams();

    /**
     * 业务失败
     *
     * @param code code
     * @param msg msg
     */
    protected void businessFail(Integer code, String msg){
        throw new BusinessException(code, msg);
    }

    protected abstract BaseResult<T> process();

    /**
     * 无论成功失败，最后都会执行
     */
    protected void afterProcess() {
    }

    /**
     * 构建成功结果
     */
    protected BaseResult<T> builSuccessResult(T data) {
        return new BaseResult<>(data);
    }

    /**
     * 构建失败结果
     */
    protected BaseResult<T> builFailResult(Integer code, String msg) {
        return new BaseResult<>(code, msg);
    }

    /**
     * 成功时执行
     */
    protected void onSuccess() {
    }

    /**
     * 失败时执行
     */
    protected void onFail() {
    }

    public BaseResult<T> execute() {
        return this.doExecute();
    }

    private BaseResult<T> doExecute() {
        try {
            this.checkParams();
            log.info("handlerLog {} checkParams success req={}", logicHandlerBaseInfo.getLogStr(), JsonUtils.objectToJson(logicHandlerBaseInfo));
        } catch (BusinessException e) {
            log.error("handlerLog {} checkParams business fail req={} msg={}", logicHandlerBaseInfo.getLogStr(), JsonUtils.objectToJson(logicHandlerBaseInfo), ExceptionUtils.getStackTrace(e));
            return builFailResult(e.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error("handlerLog {} checkParams fail req={} msg={}", logicHandlerBaseInfo.getLogStr(), JsonUtils.objectToJson(logicHandlerBaseInfo), ExceptionUtils.getStackTrace(e));
            throw e;
        }

        try {
            // 耗时计算
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            BaseResult<T> result = this.process();

            stopWatch.stop();
            log.info("handlerLog {} execute success time={} rsp={}", logicHandlerBaseInfo.getLogStr(), stopWatch.getTime(), JsonUtils.objectToJson(result));
            this.onSuccess();
            return result;
        }catch (BusinessException e) {
            this.onFail();
            log.error("handlerLog {} execute business fail msg={}", logicHandlerBaseInfo.getLogStr(), ExceptionUtils.getStackTrace(e));
            return builFailResult(e.getCode(), e.getMsg());
        } catch (Throwable e) {
            this.onFail();
            log.error("handlerLog {} execute fail msg={}", logicHandlerBaseInfo.getLogStr(), ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
            this.afterProcess();
        }
    }

}
