package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.core.JsonUtil;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.chain.ChainContext;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 抽象节点
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@Slf4j
public abstract class AbstractNode<T> {

    private final String LOG_PREFIX = "process nodeLog ";
    private final String LOG_END = " execute success";
    private final String CHAIN = " chain ";
    private final String NODE = " node ";
    private final String LOG_SKIP = " skip=";
    private final String LOG_TIME = " time=";
    private final String BEFORE_EXECUTE_PARAMS = " beforeExecuteParams=";
    private final String AFTER_EXECUTE_PARAMS = " afterExecuteParams=";
    private final String TRUE = "true";
    private final String FALSE = "false";

    /**
     * 获取依赖节点集合
     *
     * @return 依赖节点集合
     */
    public abstract Set<Class<? extends AbstractNode>> getDependsOnNodes();

    /**
     * 是否跳过当前节点
     *
     * @param chainContext chainContext
     * @return 是否跳过当前执行方法
     */
    protected abstract boolean isSkip(ChainContext<T> chainContext);

    /**
     * 参数校验
     *
     * @param chainContext chainContext
     */
    protected void checkParams(ChainContext<T> chainContext) {
    }

    /**
     * 节点执行方法
     *
     * @param chainContext chainContext
     */
    protected abstract void process(ChainContext<T> chainContext);

    /**
     * 通用执行方法
     *
     * @param chainContext chainContext
     * @param logLevel     logLevel
     * @param chainName    chainName
     */
    public void execute(@NonNull ChainContext<T> chainContext, ChainNode.LogLevelEnum logLevel, String chainName) {
        String logStr = LOG_PREFIX + chainContext.getLogStr();
        try {
            // 日志
            StringBuilder logInfo = new StringBuilder(logStr);

            buildLogInfo(logInfo, Arrays.asList(LOG_END, CHAIN, "[" + chainName + "]", NODE, "[" + this.getClass().getName() + "]"), logLevel, ChainNode.LogLevelEnum.BASE, false);
            buildLogInfo(logInfo, Arrays.asList(BEFORE_EXECUTE_PARAMS, JsonUtil.objectConvertToJson(chainContext)), logLevel, ChainNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS, false);

            // 耗时计算
            long startTime = System.currentTimeMillis();

            if (isSkip(chainContext)) {
                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, TRUE), logLevel, ChainNode.LogLevelEnum.BASE, false);
            } else {
                try {
                    checkParams(chainContext);
//            log.info(logStr + " checkParams success");
                } catch (ProcessException e) {
//                    log.info(logStr + " checkParams process fail msg=", e);
                    throw e;
                } catch (BusinessException e) {
//                    log.info(logStr + " checkParams business fail msg=", e);
                    throw e;
                } catch (Exception e) {
//                    log.info(logStr + " checkParams fail msg=", e);
                    throw e;
                }

                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, FALSE), logLevel, ChainNode.LogLevelEnum.BASE, false);

                try {
                    process(chainContext);
                } catch (ProcessException e) {
//                    log.info(logStr + " execute process fail msg=", e);
                    throw e;
                } catch (BusinessException e) {
//                    log.info(logStr + " execute business fail node [{}] msg=", nodeName, e);
                    throw e;
                } catch (Exception e) {
//                    log.info(logStr + " execute fail node [{}] msg=", nodeName, e);
                    throw e;
                }
            }

            long endTime = System.currentTimeMillis();

            buildLogInfo(logInfo, Arrays.asList(AFTER_EXECUTE_PARAMS, JsonUtil.objectConvertToJson(chainContext)), logLevel, ChainNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS, false);
            buildLogInfo(logInfo, Arrays.asList(LOG_TIME, endTime - startTime), logLevel, ChainNode.LogLevelEnum.BASE_AND_TIME, true);
        } catch (ProcessException e) {
//                    log.info(logStr + " checkParams business fail msg=", e);
            throw e;
        } catch (BusinessException e) {
//            log.info(logStr + " execute business fail node [{}] msg=", nodeName, e);
            throw e;
        } catch (Exception e) {
//            log.info(logStr + " execute fail node [{}] msg=", nodeName, e);
            throw e;
        }
    }

    /**
     * 通过传进来的节点日志类型判断打印什么日志，太长可能出现YGC频繁
     *
     * @param logInfo      logInfo
     * @param logInfos     logInfos
     * @param logLevel     logLevel
     * @param thisLogLevel thisLogLevel
     * @param print        print
     */
    private void buildLogInfo(StringBuilder logInfo, List<Object> logInfos, ChainNode.LogLevelEnum logLevel, ChainNode.LogLevelEnum thisLogLevel, Boolean print) {
        if (Objects.isNull(logLevel) || !ChainNode.LogLevelEnum.containsCode(logLevel.getCode())) {
            logLevel = ChainNode.LogLevelEnum.BASE_AND_TIME;
        }

        if (thisLogLevel.getCode() <= logLevel.getCode() && ChainNode.LogLevelEnum.NO.getCode() != logLevel.getCode()) {
            logInfos.forEach(logInfo::append);
        }

        if (print && ChainNode.LogLevelEnum.NO.getCode() != logLevel.getCode()) {
            log.info(logInfo.toString());
            // 打印完手动释放内存
            logInfo.setLength(0);
        }
    }

    /**
     * 业务失败
     *
     * @param code code
     * @param msg  msg
     */
    protected void businessFail(int code, String msg) {
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
     * 获取上下文信息
     *
     * @param chainContext chainContext
     * @return T
     */
    protected <T> T getContextInfo(ChainContext<T> chainContext) {
        if (Objects.isNull(chainContext)) {
            return null;
        }
        return chainContext.getContextInfo();
    }

    /**
     * 成功时执行
     *
     * @param chainContext chainContext
     */
    public void onSuccess(@NonNull ChainContext<T> chainContext) {
    }

    /**
     * 超时失败时执行
     *
     * @param chainContext chainContext
     */
    public abstract void onTimeoutFail(@NonNull ChainContext<T> chainContext);

    /**
     * 业务失败时执行
     *
     * @param chainContext chainContext
     */
    public abstract void onBusinessFail(@NonNull ChainContext<T> chainContext, @NonNull BusinessException e);

    /**
     * 未知失败时执行
     *
     * @param chainContext chainContext
     */
    public abstract void onUnknowFail(@NonNull ChainContext<T> chainContext, @NonNull Exception e);

    /**
     * 无论成功失败，最后都会执行
     */
    public void afterProcess(@NonNull ChainContext<T> chainContext) {
    }

}
