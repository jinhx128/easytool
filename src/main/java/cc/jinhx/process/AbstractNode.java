package cc.jinhx.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    private final String NODE_LOG = "nodeLog ";
    private final String LOG_END = " execute success";
    private final String NODE_CHAIN_NAME = " nodeChainName=";
    private final String NODE_NAME = " nodeName=";
    private final String LOG_SKIP = " skip=";
    private final String LOG_TIME = " time=";
    private final String BEFORE_EXECUTE_PARAMS = " beforeExecuteParams=";
    private final String AFTER_EXECUTE_PARAMS = " afterExecuteParams=";
    private final String TRUE = "true";
    private final String FALSE = "false";

    /**
     * 节点失败处理
     */
    private Integer failHandle = AbstractNode.FailHandleEnum.INTERRUPT.getCode();

    /**
     * 节点执行超时时间
     */
    private Long timeout = AbstractNode.TimeoutEnum.COMMONLY.getCode();

    /**
     * 获取上下文信息
     *
     * @param nodeChainContext nodeChainContext
     */
    protected <T> T getContextInfo(NodeChainContext<T> nodeChainContext){
        return nodeChainContext.getContextInfo();
    }

    /**
     * 业务失败
     *
     * @param code code
     * @param msg msg
     */
    protected void businessFail(Integer code, String msg){
        throw new BusinessException(code, msg);
    }

    /**
     * 业务失败
     *
     * @param msg msg
     */
    protected void businessFail(String msg){
        throw new BusinessException(ProcessResult.BaseEnum.FAIL.getCode(), msg);
    }

    /**
     * 参数校验
     */
    protected void checkParams(){
    }

    /**
     * 节点执行方法
     *
     * @param nodeChainContext nodeChainContext
     */
    protected abstract void process(NodeChainContext<T> nodeChainContext);

    /**
     * 通用执行方法
     *
     * @param nodeChainContext nodeChainContext
     * @param logLevel logLevel
     */
    public void execute(NodeChainContext<T> nodeChainContext, Integer logLevel, String nodeChainName) {
        String logStr = NODE_LOG + nodeChainContext.getLogStr();
        String nodeName = this.getClass().getName();
        try {
            // 日志
            StringBuilder logInfo = new StringBuilder(logStr);

            buildLogInfo(logInfo, Arrays.asList(LOG_END, NODE_CHAIN_NAME, nodeChainName, NODE_NAME, nodeName), logLevel, LogLevelEnum.BASE.getCode(), false);
            buildLogInfo(logInfo, Arrays.asList(BEFORE_EXECUTE_PARAMS, nodeChainContext.toString()), logLevel, LogLevelEnum.BASE_AND_TIME_AND_PARAMS.getCode(), false);

            // 耗时计算
            long startTime = System.currentTimeMillis();
            beforeLog();

            if (isSkip(nodeChainContext)) {
                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, TRUE), logLevel, LogLevelEnum.BASE.getCode(), false);
            } else {
                try {
                    this.checkParams();
//            log.info(logStr + " checkParams success");
                } catch (BusinessException e) {
                    log.error(logStr + " checkParams business fail msg=", e);
                    throw e;
                } catch (Exception e) {
                    log.error(logStr + " checkParams fail msg=", e);
                    throw e;
                }

                buildLogInfo(logInfo, Arrays.asList(LOG_SKIP, FALSE), logLevel, LogLevelEnum.BASE.getCode(), false);

                try {
                    process(nodeChainContext);
                } catch (BusinessException e) {
                    log.error(logStr + " execute business fail nodeName={} msg=", nodeName, e);
                    throw e;
                } catch (Exception e) {
                    log.error(logStr + " execute fail nodeName={} msg=", nodeName, e);
                    throw e;
                }
            }

            afterLog();
            long endTime = System.currentTimeMillis();

            buildLogInfo(logInfo, Arrays.asList(AFTER_EXECUTE_PARAMS, nodeChainContext.toString()), logLevel, LogLevelEnum.BASE_AND_TIME_AND_PARAMS.getCode(), false);
            buildLogInfo(logInfo, Arrays.asList(LOG_TIME, endTime - startTime), logLevel, LogLevelEnum.BASE_AND_TIME.getCode(), true);
        } catch (BusinessException e) {
            log.error(logStr + " execute business fail nodeName={} msg=", nodeName, e);
            throw e;
        } catch (Exception e) {
            log.error(logStr + " execute fail nodeName={} msg=", nodeName, e);
            throw e;
        }
    }

    /**
     * 通过传进来的节点日志类型判断打印什么日志，太长可能出现YGC频繁
     *
     * @param logInfo logInfo
     * @param logInfos logInfos
     * @param print print
     */
    private void buildLogInfo(StringBuilder logInfo, List<Object> logInfos, Integer logLevel, Integer thisLogLevel, Boolean print) {
        if (!LogLevelEnum.containsCode(logLevel)){
            logLevel = LogLevelEnum.BASE_AND_TIME.getCode();
        }

        if (thisLogLevel <= logLevel && !LogLevelEnum.NO.getCode().equals(logLevel)){
            logInfos.forEach(logInfo::append);
        }

        if (print && !LogLevelEnum.NO.getCode().equals(logLevel)){
            log.info(logInfo.toString());
            // 打印完手动释放内存
            logInfo.setLength(0);
        }
    }

    /**
     * 节点执行后打印日志，执行失败则不打印
     */
    protected void afterLog() {
    }

    /**
     * 节点执行前打印日志
     */
    protected void beforeLog() {
    }

    /**
     * 是否跳过当前执行方法，默认不跳过
     *
     * @param nodeChainContext nodeChainContext
     * @return 是否跳过当前执行方法
     */
    protected boolean isSkip(NodeChainContext<T> nodeChainContext) {
        return false;
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

        private Long code;
        private String msg;

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

        private Integer code;
        private String msg;

        private static final Map<Integer, LogLevelEnum> MAP;

        static {
            MAP = Arrays.stream(LogLevelEnum.values()).collect(Collectors.toMap(LogLevelEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(Integer code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(Integer code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static LogLevelEnum getEnum(Integer code) {
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
        // todo 重试
//    RETRY(3, "重试节点"),
        ;

        private Integer code;
        private String msg;

        private static final Map<Integer, AbstractNode.FailHandleEnum> MAP;

        static {
            MAP = Arrays.stream(AbstractNode.FailHandleEnum.values()).collect(Collectors.toMap(AbstractNode.FailHandleEnum::getCode, obj -> obj));
        }

        public static Boolean containsCode(Integer code) {
            return MAP.containsKey(code);
        }

        public static String getMsg(Integer code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code).getMsg();
        }

        public static AbstractNode.FailHandleEnum getEnum(Integer code) {
            if (!MAP.containsKey(code)) {
                return null;
            }

            return MAP.get(code);
        }

    }

}