package cc.jinhx.easytool.process.handler;

import cc.jinhx.easytool.core.JsonUtil;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.topology.AbstractTopology;
import cc.jinhx.easytool.process.topology.TopologyContext;
import cc.jinhx.easytool.process.topology.TopologyManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 抽象处理器
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public abstract class AbstractHandler<T> {

    private static final String LOG_PREFIX = "process handlerLog ";

    private HandlerBaseInfo handlerBaseInfo;

    protected AbstractHandler() {
        init(new HandlerBaseInfo(), null);
    }

    protected AbstractHandler(@NonNull HandlerBaseInfo handlerBaseInfo) {
        init(handlerBaseInfo, null);
    }

    protected AbstractHandler(@NonNull HandlerBaseInfo handlerBaseInfo, @NonNull String logStr) {
        init(handlerBaseInfo, logStr);
    }

    /**
     * 初始化，将当前方法名写入日志字段
     *
     * @param handlerBaseInfo handlerBaseInfo
     * @param logStr               logStr
     */
    private void init(HandlerBaseInfo handlerBaseInfo, String logStr) {
        this.handlerBaseInfo = handlerBaseInfo;
        if (StringUtils.isEmpty(logStr)) {
            handlerBaseInfo.setLogStr("method [" + Thread.currentThread().getStackTrace()[4].getMethodName() + "]");
        } else {
            handlerBaseInfo.setLogStr(logStr + " method [" + Thread.currentThread().getStackTrace()[4].getMethodName() + "]");
        }
    }

    /**
     * 参数校验
     */
    protected abstract void checkParams();

    /**
     * 执行方法
     */
    protected abstract ProcessResult<T> process();

    public ProcessResult<T> execute() {
        return doExecute();
    }

    private ProcessResult<T> doExecute() {
        String logStr = LOG_PREFIX + handlerBaseInfo.getLogStr();
        try {
            checkParams();
            log.info("{} checkParams success req={}", logStr, JsonUtil.objectConvertToJson(handlerBaseInfo));
        } catch (ProcessException e) {
            log.info("{} execute process fail msg={}", logStr, e.getMsg());
            onUnknowFail(e);
            return buildFailResult(e.getCode(), e.getMsg());
        } catch (BusinessException e) {
            log.info("{} checkParams business fail req={} msg={}", logStr, JsonUtil.objectConvertToJson(handlerBaseInfo), e.getMsg());
            onBusinessFail(e);
            return buildBusinessFailResult(e.getCode(), e.getMsg());
        } catch (Exception e) {
            String exceptionLog = getExceptionLog(e);
            log.info("{} checkParams fail req={} msg={}", logStr, JsonUtil.objectConvertToJson(handlerBaseInfo), exceptionLog);
            onUnknowFail(e);
            return buildUnknownFailResult(exceptionLog);
        }

        try {
            // 耗时计算
            long startTime = System.currentTimeMillis();

            ProcessResult<T> result = process();

            long endTime = System.currentTimeMillis();
            log.info("{} execute success time={} rsp={}", logStr, endTime - startTime, JsonUtil.objectConvertToJson(result));
            onSuccess();
            return result;
        } catch (ProcessException e) {
            // 用拓扑图的情况
            log.info("{} execute process fail msg={}", logStr, e.getMsg());
            onUnknowFail(e);
            return buildFailResult(e.getCode(), e.getMsg());
        } catch (BusinessException e) {
            // 用拓扑图的情况
            log.info("{} execute business fail msg={}", logStr, e.getMsg());
            onBusinessFail(e);
            return buildBusinessFailResult(e.getCode(), e.getMsg());
        } catch (Exception e) {
            // 没用拓扑图的情况
            String exceptionLog = getExceptionLog(e);
            log.info("{} execute fail msg={}", logStr, exceptionLog);
            onUnknowFail(e);
            return buildUnknownFailResult(exceptionLog);
        } finally {
            afterProcess();
        }
    }

    /**
     * 拼接错误日志
     *
     * @param e e
     * @return String
     */
    private String getExceptionLog(Exception e) {
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

    protected void executeTopology(@NonNull Class<? extends AbstractTopology> clazz, @NonNull TopologyContext<?> topologyContext, @NonNull ExecutorService executorService) {
        executeTopology(clazz, null, topologyContext, executorService);
    }

    protected void executeTopology(@NonNull Class<? extends AbstractTopology> clazz, @NonNull TopologyContext<?> topologyContext) {
        executeTopology(clazz, null, topologyContext);
    }

    /**
     * 执行指定拓扑图
     *
     * @param clazz              clazz
     * @param logLevel           logLevel
     * @param topologyContext   topologyContext
     * @param executorService executorService
     */
    protected void executeTopology(@NonNull Class<? extends AbstractTopology> clazz, AbstractTopology.LogLevelEnum logLevel, @NonNull TopologyContext<?> topologyContext, @NonNull ExecutorService executorService) {
        getTopology(clazz, logLevel).execute(topologyContext, executorService);
    }

    /**
     * 执行指定拓扑图
     *
     * @param clazz            clazz
     * @param logLevel         logLevel
     * @param topologyContext topologyContext
     */
    protected void executeTopology(@NonNull Class<? extends AbstractTopology> clazz, AbstractTopology.LogLevelEnum logLevel, @NonNull TopologyContext<?> topologyContext) {
        getTopology(clazz, logLevel).execute(topologyContext);
    }

    /**
     * 获取指定拓扑图
     *
     * @param clazz    clazz
     * @param logLevel logLevel
     */
    private AbstractTopology getTopology(Class<? extends AbstractTopology> clazz, AbstractTopology.LogLevelEnum logLevel) {
        AbstractTopology abstractTopology = TopologyManager.getTopology(clazz, logLevel);
        if (Objects.isNull(abstractTopology)) {
            throw new ProcessException(ProcessException.MsgEnum.TOPOLOGY_UNREGISTERED.getMsg() + "=" + clazz.getName());
        }

        return abstractTopology;
    }

    /**
     * 业务失败
     *
     * @param code code
     * @param msg  msg
     */
    protected void businessFail(Integer code, String msg) {
        throw new BusinessException(code, msg);
    }

    /**
     * 业务失败
     *
     * @param msg msg
     */
    protected void businessFail(String msg) {
        throw new BusinessException(ProcessResult.BaseEnum.BUSINESS_FAIL.getCode(), msg);
    }

    /**
     * 无论成功失败，最后都会执行
     */
    protected void afterProcess() {
    }

    /**
     * 构建上下文
     */
    protected <T> TopologyContext<T> buildTopologyContext(Class<T> clazz) {
        return TopologyContext.create(clazz);
    }

    /**
     * 构建成功结果
     */
    protected ProcessResult<T> buildSuccessResult(T data) {
        return new ProcessResult<>(data);
    }

    /**
     * 构建失败结果
     */
    protected ProcessResult<T> buildFailResult(Integer code, String msg) {
        return new ProcessResult<>(code, msg);
    }

    /**
     * 构建未知失败结果
     */
    protected ProcessResult<T> buildUnknownFailResult(String msg) {
        return new ProcessResult<>(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), msg);
    }

    /**
     * 构建业务失败结果
     */
    protected ProcessResult<T> buildBusinessFailResult(String msg) {
        return new ProcessResult<>(ProcessResult.BaseEnum.BUSINESS_FAIL.getCode(), msg);
    }

    /**
     * 构建业务失败结果
     */
    protected ProcessResult<T> buildBusinessFailResult(Integer code, String msg) {
        return new ProcessResult<>(code, msg);
    }

    /**
     * 成功时执行
     */
    protected void onSuccess() {
    }

    /**
     * 业务失败时执行
     */
    protected void onBusinessFail(BusinessException e) {
    }

    /**
     * 未知失败时执行
     */
    protected void onUnknowFail(Exception e) {
    }

}
