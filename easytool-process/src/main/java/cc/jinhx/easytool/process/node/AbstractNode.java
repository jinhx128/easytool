package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.core.JsonUtil;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.chain.ChainContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

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
     * 节点失败处理
     */
    private FailHandleEnum failHandle = FailHandleEnum.INTERRUPT;

    /**
     * 节点执行超时时间
     */
    private Long timeout = TimeoutEnum.COMMONLY.getCode();

    /**
     * 重试次数
     */
    private RetryTimesEnum retryTimes = RetryTimesEnum.ONE;

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
    public void execute(@NonNull ChainContext<T> chainContext, LogLevelEnum logLevel, String chainName) {
        String logStr = LOG_PREFIX + chainContext.getLogStr();
        try {
            // 日志
            StringBuilder logInfo = new StringBuilder(logStr);

            buildLogInfo(logInfo, Arrays.asList(LOG_END, CHAIN, "[" + chainName + "]", NODE, "[" + this.getClass().getName() + "]"), logLevel, LogLevelEnum.BASE, false);
            buildLogInfo(logInfo, Arrays.asList(BEFORE_EXECUTE_PARAMS, JsonUtil.objectConvertToJson(chainContext)), logLevel, LogLevelEnum.BASE_AND_TIME_AND_PARAMS, false);

            // 耗时计算
            long startTime = System.currentTimeMillis();

            if (isSkip(chainContext)) {
                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, TRUE), logLevel, LogLevelEnum.BASE, false);
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

                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, FALSE), logLevel, LogLevelEnum.BASE, false);

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

            buildLogInfo(logInfo, Arrays.asList(AFTER_EXECUTE_PARAMS, JsonUtil.objectConvertToJson(chainContext)), logLevel, LogLevelEnum.BASE_AND_TIME_AND_PARAMS, false);
            buildLogInfo(logInfo, Arrays.asList(LOG_TIME, endTime - startTime), logLevel, LogLevelEnum.BASE_AND_TIME, true);
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
    private void buildLogInfo(StringBuilder logInfo, List<Object> logInfos, LogLevelEnum logLevel, LogLevelEnum thisLogLevel, Boolean print) {
        if (Objects.isNull(logLevel) || !LogLevelEnum.containsCode(logLevel.getCode())) {
            logLevel = LogLevelEnum.BASE_AND_TIME;
        }

        if (thisLogLevel.getCode() <= logLevel.getCode() && LogLevelEnum.NO.getCode() != logLevel.getCode()) {
            logInfos.forEach(logInfo::append);
        }

        if (print && LogLevelEnum.NO.getCode() != logLevel.getCode()) {
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


    @AllArgsConstructor
    @Getter
    public enum TimeoutEnum {

        SHORT(50L, "短"),
        SHORTER(100L, "较短"),
        COMMONLY(200L, "一般"),
        LONGER(500L, "较长"),
        LONG(1000L, "长"),
        ;

        private final Long code;
        private final String msg;

        private static final Map<Long, TimeoutEnum> MAP;

        static {
            MAP = Arrays.stream(TimeoutEnum.values()).collect(Collectors.toMap(TimeoutEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(Long code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(Long code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static TimeoutEnum getEnum(Long code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

    @AllArgsConstructor
    @Getter
    public enum LogLevelEnum {

        NO(1, "不打印"),
        BASE(2, "打印基本信息"),
        BASE_AND_TIME(3, "打印基本信息和耗时"),
        BASE_AND_TIME_AND_PARAMS(4, "打印基本信息和耗时和参数"),
        ;

        private final int code;
        private final String msg;

        private static final Map<Integer, LogLevelEnum> MAP;

        static {
            MAP = Arrays.stream(LogLevelEnum.values()).collect(Collectors.toMap(LogLevelEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(int code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static LogLevelEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

    @AllArgsConstructor
    @Getter
    public enum FailHandleEnum {

        INTERRUPT(1, "中断链路"),
        ABANDON(2, "抛弃节点"),
        RETRY(3, "重试节点"),
        ;

        private final int code;
        private final String msg;

        private static final Map<Integer, FailHandleEnum> MAP;

        static {
            MAP = Arrays.stream(FailHandleEnum.values()).collect(Collectors.toMap(FailHandleEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(int code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static FailHandleEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

    @AllArgsConstructor
    @Getter
    public enum RetryTimesEnum {

        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        TEN(10);

        private final int code;

        private static final Map<Integer, RetryTimesEnum> MAP;

        static {
            MAP = Arrays.stream(RetryTimesEnum.values()).collect(Collectors.toMap(RetryTimesEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(int code) {
            return MAP.containsKey(code);
        }

        public static RetryTimesEnum getEnum(int code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

}
