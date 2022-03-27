package com.jinhx.process.handler;

import com.jinhx.process.enums.ExceptionEnums;
import com.jinhx.process.exception.ProcessException;
import com.jinhx.process.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.StopWatch;

import java.util.Objects;

/**
 * 抽象逻辑处理器
 *
 * @author jinhx
 * @since 2021-08-06
 */
@Slf4j
public abstract class AbstractLogicHandler<T> {

    private LogicHandlerBaseInfo logicHandlerBaseInfo;

    protected AbstractLogicHandler(LogicHandlerBaseInfo logicHandlerBaseInfo) {
        if (Objects.isNull(logicHandlerBaseInfo)){
            throw new ProcessException(ExceptionEnums.LOGIC_HANDLER_BASE_INFO_NOT_NULL);
        }

        init(logicHandlerBaseInfo, "act=" + Thread.currentThread().getStackTrace()[3].getMethodName());
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
        logicHandlerBaseInfo.setLogStr(logStr + " act=" + Thread.currentThread().getStackTrace()[3].getMethodName());
        this.logicHandlerBaseInfo = logicHandlerBaseInfo;
    }

    /**
     * 参数校验
     */
    protected abstract void checkParams();

    protected abstract T process();

    /**
     * 无论成功失败，最后都会执行
     */
    protected void afterProcess() {
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

    public T execute() {
        return this.doExecute();
    }

    private T doExecute() {
        try {
            this.checkParams();
            log.info("handlerLog {} checkParams success req={}", logicHandlerBaseInfo.getLogStr(), JsonUtils.objectToJson(logicHandlerBaseInfo));
        } catch (Exception e) {
            log.error("handlerLog {} checkParams fail req={} msg={}", logicHandlerBaseInfo.getLogStr(), JsonUtils.objectToJson(logicHandlerBaseInfo), ExceptionUtils.getStackTrace(e));
            throw e;
        }

        try {
            // 耗时计算
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            T result = this.process();

            stopWatch.stop();
            log.info("handlerLog {} execute success time={} rsp={}", logicHandlerBaseInfo.getLogStr(), stopWatch.getTime(), JsonUtils.objectToJson(result));
            this.onSuccess();
            return result;
        }catch (Throwable e) {
            this.onFail();
            log.error("handlerLog {} execute fail msg={}", logicHandlerBaseInfo.getLogStr(), ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
            this.afterProcess();
        }
    }

}
