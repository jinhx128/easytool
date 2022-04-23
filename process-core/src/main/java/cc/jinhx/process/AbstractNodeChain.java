package cc.jinhx.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 抽象节点链
 *
 * @author jinhx
 * @since 2022-03-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class AbstractNodeChain extends LinkedHashMap<String, List<AbstractNode>> {

    private static final long serialVersionUID = 4780080785208529405L;

    private static final String LOG_ID = "traceId";

    private LogLevelEnum logLevel = LogLevelEnum.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS;

    private boolean asyncLastNode = false;

    private String lastNodeName;

    public void addSyncNode(Class<? extends AbstractNode> node) {
        addSyncNode(node, null, null, null);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle) {
        addSyncNode(node, failHandle, null, null);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, Long timeout) {
        addSyncNode(node, null, timeout, null);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, AbstractNode.RetryTimesEnum retryTimes) {
        addSyncNode(node, null, null, retryTimes);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout) {
        addSyncNode(node, failHandle, timeout, null);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes) {
        addSyncNode(node, failHandle, null, retryTimes);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        addSyncNode(node, null, timeout, retryTimes);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        add(node.getName(), node, failHandle, timeout, retryTimes);
        if (this.asyncLastNode){
            this.asyncLastNode = false;
        }
    }

    public void addAsyncNode(Class<? extends AbstractNode> node) {
        addAsyncNode(node, null, null, null, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle) {
        addAsyncNode(node, failHandle, null, null, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Long timeout) {
        addAsyncNode(node, null, timeout, null, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNode(node, null, null, retryTimes, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout) {
        addAsyncNode(node, failHandle, timeout, null, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNode(node, failHandle, null, retryTimes, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNode(node, null, timeout, retryTimes, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNode(node, failHandle, timeout, retryTimes, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, boolean restartAsyncGroup) {
        addAsyncNode(node, null, null, null, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, boolean restartAsyncGroup) {
        addAsyncNode(node, failHandle, null, null, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Long timeout, boolean restartAsyncGroup) {
        addAsyncNode(node, null, timeout, null, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        addAsyncNode(node, null, null, retryTimes, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout, boolean restartAsyncGroup) {
        addAsyncNode(node, failHandle, timeout, null, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        addAsyncNode(node, failHandle, null, retryTimes, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Long timeout, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        addAsyncNode(node, null, timeout, retryTimes, restartAsyncGroup);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout,
                             AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        if (restartAsyncGroup && this.asyncLastNode){
            this.asyncLastNode = false;
        }

        if (this.asyncLastNode) {
            add(this.lastNodeName, node, failHandle, timeout, retryTimes);
        } else {
            add(node.getName(), node, failHandle, timeout, retryTimes);
            this.asyncLastNode = true;
            this.lastNodeName = node.getName();
        }
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes) {
        addAsyncNodeList(nodes, null, null, null, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle) {
        addAsyncNodeList(nodes, failHandle, null, null, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, Long timeout) {
        addAsyncNodeList(nodes, null, timeout, null, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNodeList(nodes, null, null, retryTimes, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle, Long timeout) {
        addAsyncNodeList(nodes, failHandle, timeout, null, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNodeList(nodes, failHandle, null, retryTimes, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        addAsyncNodeList(nodes, null, timeout, retryTimes, false);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, null, null, null, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, failHandle, null, null, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, Long timeout, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, null, timeout, null, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, null, null, retryTimes, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle, Long timeout, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, failHandle, timeout, null, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, failHandle, null, retryTimes, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, Long timeout, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        addAsyncNodeList(nodes, null, timeout, retryTimes, restartAsyncGroup);
    }

    public void addAsyncNodeList(List<Class<? extends AbstractNode>> nodes, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes, boolean restartAsyncGroup) {
        if (restartAsyncGroup && this.asyncLastNode){
            this.asyncLastNode = false;
        }

        if (this.asyncLastNode) {
            for (Class<? extends AbstractNode> node : nodes) {
                add(this.lastNodeName, node, failHandle, timeout, retryTimes);
            }
        } else {
            String name = UUID.randomUUID().toString();
            for (Class<? extends AbstractNode> node : nodes) {
                add(name, node, failHandle, timeout, retryTimes);
            }
            this.asyncLastNode = true;
            this.lastNodeName = name;
        }

    }

    /**
     * 添加指定组节点，一个链路按理说只有一类型的节点，如果有多个，默认覆盖前面的，使用最后一个
     *
     * @param groupName groupName
     * @param node node
     * @param failHandle failHandle
     * @param timeout timeout
     * @param retryTimes retryTimes
     */
    private void add(String groupName, Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        AbstractNode abstractNode = NodeManager.getNode(node, failHandle, timeout, retryTimes);
        if (Objects.isNull(abstractNode)){
            throw new ProcessException(ProcessException.MsgEnum.NODE_UNREGISTERED.getMsg() + "=" + node.getName());
        }

        if (this.containsKey(groupName)) {
            this.get(groupName).add(abstractNode);
        } else {
            List<AbstractNode> list = new ArrayList();
            list.add(abstractNode);
            this.put(groupName, list);
        }
    }

    /**
     * 配置节点信息
     * 1. 通过内部addxxx方法，添加节点到节点链，执行顺序按照添加顺序
     * 2. 组内异步，与组外同步
     * 3. 添加一个同步节点，自己属于一个组，且组内只能有自己
     * 4. 添加一个异步节点/节点组
     *   4.1 可通过参数restartAsyncGroup控制是否要加入上一个添加的异步节点/节点组属于同组，默认是
     *   4.2 如果上一个是同步节点，则无法加入，自己只能属于一个新的组，后面添加的异步节点/节点组依然可以通过参数restartAsyncGroup控制
     */
    protected abstract void setNodeInfo();

    /**
     * 执行当前节点链，利用LinkedHashMap特性，按照添加顺序执行，使用默认线程池
     *
     * @param nodeChainContext nodeChainContext
     */
    public void execute(NodeChainContext<?> nodeChainContext) {
        execute(nodeChainContext, getThreadPoolExecutor());
    }

    /**
     * 执行当前节点链，利用LinkedHashMap特性，按照添加顺序执行，指定线程池，如果为空则使用默认配置的线程池
     *
     * @param nodeChainContext nodeChainContext
     * @param threadPoolExecutor threadPoolExecutor
     */
    public void execute(NodeChainContext<?> nodeChainContext, ThreadPoolExecutor threadPoolExecutor) {
        String logId = MDC.get(getMDCLogIdKey());
        if (!StringUtils.isEmpty(logId) && !logId.contains("-process")){
            MDC.put(getMDCLogIdKey(), logId + "-process");
        }
        // 通过节点链日志设置节点日志级别
        AbstractNode.LogLevelEnum nodeLogLevel = null;
        LogLevelEnum nodeChainLogLevel= this.logLevel;
        boolean baseAndTimeAndFirstAndLastNodesParamsLogLevel = false;
        if (LogLevelEnum.NO.getCode().equals(nodeChainLogLevel.getCode())){
            nodeLogLevel = AbstractNode.LogLevelEnum.NO;
        } else if (LogLevelEnum.BASE.getCode().equals(nodeChainLogLevel.getCode())) {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE;
        } else if (LogLevelEnum.BASE_AND_TIME.getCode().equals(nodeChainLogLevel.getCode())) {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME;
        } else if (LogLevelEnum.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode().equals(nodeChainLogLevel.getCode())) {
            baseAndTimeAndFirstAndLastNodesParamsLogLevel = true;
        } else if (LogLevelEnum.BASE_AND_TIME_AND_ALL_NODES_PARAMS.getCode().equals(nodeChainLogLevel.getCode())) {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS;
        } else {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME;
        }

        int count = 1;
        for (Map.Entry<String, List<AbstractNode>> nodesEntry : this.entrySet()) {
            // 通过节点链日志设置节点日志级别
            if (baseAndTimeAndFirstAndLastNodesParamsLogLevel){
                if (count == 1 || count == this.entrySet().size()){
                    nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS;
                }else {
                    nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME;
                }
                count++;
            }

            // 获取整组的执行future
            Map<Future<Void>, AbstractNode> futureMap = getFutureMap(nodeChainContext, threadPoolExecutor, nodesEntry.getValue(), nodeLogLevel);
            // 等待整组的future执行完
            waitFutureExecute(nodeChainContext, threadPoolExecutor, futureMap, new HashMap<>(), nodeLogLevel);

            // 是否需要执行下一组节点
            if(Objects.nonNull(nodeChainContext.getExNextNodeGroup()) && !nodeChainContext.getExNextNodeGroup()){
                return;
            }
        }
    }

    /**
     * 获取整组的执行future
     *
     * @param nodeChainContext nodeChainContext
     * @param threadPoolExecutor threadPoolExecutor
     * @param abstractNodeList abstractNodeList
     * @param nodeLogLevel nodeLogLevel
     * @return Map<Future<Void>, AbstractNode>
     */
    private Map<Future<Void>, AbstractNode> getFutureMap(NodeChainContext<?> nodeChainContext, ThreadPoolExecutor threadPoolExecutor,
                                                         List<AbstractNode> abstractNodeList, AbstractNode.LogLevelEnum nodeLogLevel){
        String processLogId = MDC.get(getMDCLogIdKey());
        Map<Future<Void>, AbstractNode> futureMap = new HashMap<>();
        // 同组单/多个节点并行执行
        for (AbstractNode abstractNode : abstractNodeList) {
            String nodeChainName = this.getClass().getName();
            if (Objects.nonNull(threadPoolExecutor)){
                futureMap.put(CompletableFuture.supplyAsync(() -> {
                    MDC.put(getMDCLogIdKey(), processLogId);
                    abstractNode.execute(nodeChainContext, nodeLogLevel, nodeChainName);
                    MDC.remove(getMDCLogIdKey());
                    return null;
                }, threadPoolExecutor), abstractNode);
            } else if (Objects.nonNull(getThreadPoolExecutor())) {
                futureMap.put(CompletableFuture.supplyAsync(() -> {
                    MDC.put(getMDCLogIdKey(), processLogId);
                    abstractNode.execute(nodeChainContext, nodeLogLevel, nodeChainName);
                    MDC.remove(getMDCLogIdKey());
                    return null;
                }, getThreadPoolExecutor()), abstractNode);
            } else {
                throw new ProcessException(ProcessException.MsgEnum.NODE_CHAIN_THREAD_POOL_EXECUTOR_NOT_NULL.getMsg() + "=" + nodeChainName);
            }
        }

        return futureMap;
    }

    /**
     * 等待整组的future执行完
     *
     * @param nodeChainContext nodeChainContext
     * @param threadPoolExecutor threadPoolExecutor
     * @param futureMap futureMap
     * @param retriedMap retriedMap
     * @param nodeLogLevel nodeLogLevel
     */
    private void waitFutureExecute(NodeChainContext<?> nodeChainContext, ThreadPoolExecutor threadPoolExecutor, Map<Future<Void>, AbstractNode> futureMap,
                          Map<String, Integer> retriedMap, AbstractNode.LogLevelEnum nodeLogLevel) {
        for (Map.Entry<Future<Void>, AbstractNode> futureEntry : futureMap.entrySet()) {
            ProcessException processException = null;
            BusinessException businessException = null;
            Exception exception = null;
            Future<Void> future = futureEntry.getKey();
            AbstractNode abstractNode = futureEntry.getValue();
            Long timeout = abstractNode.getTimeout();
            Integer failHandle = abstractNode.getFailHandle().getCode();
            Integer retryTimes = abstractNode.getRetryTimes().getCode();
            String nodeName = abstractNode.getClass().getName();
            try {
                future.get(timeout, TimeUnit.MILLISECONDS);
                abstractNode.onSuccess(nodeChainContext);
            } catch (TimeoutException e) {
                if (!AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                    abstractNode.onTimeoutFail(nodeChainContext);
                }
                exception = e;
                // 中断超时线程，不一定成功
                boolean cancel = future.cancel(true);
                log.error("nodeChainLog {} execute timeout nodeName={} timeout={} cancel={}", nodeChainContext.getLogStr(), nodeName, timeout, cancel);
                processException = new ProcessException(ProcessException.MsgEnum.NODE_TIMEOUT.getMsg() + "=" + nodeName);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof ProcessException){
                    if (!AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                        abstractNode.onUnknowFail(nodeChainContext);
                    }
                    exception = e;
                    log.error("nodeChainLog {} execute process fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, getExceptionLog(e));
                    processException = (ProcessException) e.getCause();
                }else if (e.getCause() instanceof BusinessException){
                    exception = e;
                    log.error("nodeChainLog {} execute business fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, getExceptionLog(e));
                    if (!AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                        abstractNode.onBusinessFail(nodeChainContext);
                        throw (BusinessException) e.getCause();
                    } else {
                        businessException = (BusinessException) e.getCause();
                    }
                }else {
                    if (!AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                        abstractNode.onUnknowFail(nodeChainContext);
                    }
                    exception = e;
                    String exceptionLog = getExceptionLog(e);
                    log.error("nodeChainLog {} execute fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, exceptionLog);
                    processException = new ProcessException(ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
                }
            } catch (Exception e) {
                if (!AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                    abstractNode.onUnknowFail(nodeChainContext);
                }
                exception = e;
                String exceptionLog = getExceptionLog(e);
                log.error("nodeChainLog {} execute fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, exceptionLog);
                processException = new ProcessException(ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
            } finally {
                if (!AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                    abstractNode.afterProcess(nodeChainContext);
                }
            }

            // 降级处理
            if (Objects.nonNull(processException) || Objects.nonNull(businessException)) {
                if (AbstractNode.FailHandleEnum.INTERRUPT.getCode().equals(failHandle)){
                    log.error("nodeChainLog {} execute fail interrupt nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                    throw processException;
                } else if (AbstractNode.FailHandleEnum.ABANDON.getCode().equals(failHandle)){
                    log.error("nodeChainLog {} execute fail abandon nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                } else if (AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                    List<AbstractNode> retryAbstractNodeList = new ArrayList<>();
                    retryAbstractNodeList.add(abstractNode);

                    // 当前重试次数
                    int nowRetryCount;
                    if (retriedMap.containsKey(nodeName)){
                        if (retriedMap.get(nodeName) >= retryTimes){
                            // 打印上一次重试失败
                            log.error("nodeChainLog {} execute fail retry fail nodeName={} timeout={} retryTimes={} retriedTimes={}", nodeChainContext.getLogStr(), nodeName, timeout, retryTimes, retriedMap.get(nodeName));

                            if (exception instanceof TimeoutException) {
                                abstractNode.onTimeoutFail(nodeChainContext);
                            } else if (exception instanceof ExecutionException) {
                                if (exception.getCause() instanceof ProcessException) {
                                    abstractNode.onUnknowFail(nodeChainContext);
                                } else if (exception.getCause() instanceof BusinessException) {
                                    abstractNode.onBusinessFail(nodeChainContext);
                                    abstractNode.afterProcess(nodeChainContext);
                                    throw (BusinessException) exception.getCause();
                                } else {
                                    abstractNode.onUnknowFail(nodeChainContext);
                                }
                            } else {
                                abstractNode.onUnknowFail(nodeChainContext);
                            }

                            // 直接中断的两种考虑
                            // 1. 既然是需要重试的节点，那么肯定是比较重要的数据，不可缺失
                            // 2. 防止一组里面有多个一样的节点互相影响重试次数，也可以通过清掉key解决
                            abstractNode.afterProcess(nodeChainContext);
                            throw processException;
                        }

                        // 打印上一次重试失败
                        log.info("nodeChainLog {} execute fail retry nodeName={} timeout={} retryTimes={} retriedTimes={}", nodeChainContext.getLogStr(), nodeName, timeout, retryTimes, retriedMap.get(nodeName));
                        retriedMap.put(nodeName, retriedMap.get(nodeName) + 1);
                        nowRetryCount = retriedMap.get(nodeName) + 1;
                        Map<Future<Void>, AbstractNode> retryFutureMap = getFutureMap(nodeChainContext, threadPoolExecutor, retryAbstractNodeList, nodeLogLevel);
                        waitFutureExecute(nodeChainContext, threadPoolExecutor , retryFutureMap, retriedMap, nodeLogLevel);
                    } else {
                        // 第一次重试
                        retriedMap.put(nodeName, 1);
                        nowRetryCount = 1;
                        Map<Future<Void>, AbstractNode> retryFutureMap = getFutureMap(nodeChainContext, threadPoolExecutor, retryAbstractNodeList, nodeLogLevel);
                        waitFutureExecute(nodeChainContext, threadPoolExecutor, retryFutureMap, retriedMap, nodeLogLevel);
                    }

                    if (retriedMap.containsKey(nodeName) && retriedMap.get(nodeName) == nowRetryCount){
                        // 打印本次重试成功
                        log.info("nodeChainLog {} execute fail retry success nodeName={} timeout={} retryTimes={} retriedTimes={}", nodeChainContext.getLogStr(), nodeName, timeout, retryTimes, nowRetryCount);
                        abstractNode.afterProcess(nodeChainContext);
                    }
                } else {
                    // 默认中断
                    log.error("nodeChainLog {} execute fail default interrupt nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                    throw processException;
                }
            }
        }
    }

    /**
     * 拼接错误日志
     *
     * @param e e
     * @return String
     */
    private String getExceptionLog(Exception e){
        if (Objects.nonNull(e)){
            StringBuilder stringBuffer = new StringBuilder("\n");
            if (Objects.nonNull(e.getMessage())){
                stringBuffer.append("[").append(getMDCLogId()).append("]").append("-").append(e.getMessage()).append("\n");
            }
            if (Objects.nonNull(e.getCause())){
                StackTraceElement[] stackTrace = e.getCause().getStackTrace();
                if (Objects.nonNull(stackTrace) && stackTrace.length > 0){
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        stringBuffer.append("[").append(getMDCLogId()).append("]").append("-").append(stackTraceElement.toString()).append("\n");
                    }
                    return stringBuffer.toString();
                }
            }
        }

        return null;
    }

    /**
     * 获取节点链默认线程池，内置异步线程池
     *
     * @return ThreadPoolExecutor
     */
    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return ThreadPoolManager.COMMON_NODE_CHAIN_THREAD_POOL;
    }

    /**
     * 获取MDC日志id的key
     *
     * @return String
     */
    protected String getMDCLogIdKey() {
        return LOG_ID;
    }

    /**
     * 获取MDC日志id
     *
     * @return String
     */
    protected String getMDCLogId() {
        return MDC.get(getMDCLogIdKey());
    }


    @AllArgsConstructor
    @Getter
    public enum LogLevelEnum {

        NO(1, "不打印"),
        BASE(2, "打印基本信息"),
        BASE_AND_TIME(3, "打印基本信息和耗时"),
        BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS(4, "打印基本信息和耗时和第一个和最后一个节点参数"),
        BASE_AND_TIME_AND_ALL_NODES_PARAMS(5, "打印基本信息和耗时和所有节点参数"),
        ;

        private final Integer code;
        private final String msg;

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

}
