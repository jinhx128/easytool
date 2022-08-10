package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.ProcessException;
import cc.jinhx.easytool.process.ThreadPoolManager;
import cc.jinhx.easytool.process.node.AbstractNode;
import cc.jinhx.easytool.process.node.NodeManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 抽象链路
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@Slf4j
public abstract class AbstractChain {

    private static final String LOG_PREFIX = "process chainLog ";

    /**
     * 首节点集合
     */
    private Map<Class<? extends AbstractNode>, AbstractNode> firstNodeMap;

    /**
     * 父节点map
     */
    private Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> parentNodeMap;

    /**
     * 子节点map
     */
    private Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeMap;

    /**
     * 节点集合
     */
    private Map<Class<? extends AbstractNode>, AbstractNode> nodeMap;

    private LogLevelEnum logLevel = LogLevelEnum.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS;

    public void addNode(@NonNull Class<? extends AbstractNode> node) {
        add(node, null, null, null);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle) {
        add(node, failHandle, null, null);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, Long timeout) {
        add(node, null, timeout, null);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, AbstractNode.RetryTimesEnum retryTimes) {
        add(node, null, null, retryTimes);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout) {
        add(node, failHandle, timeout, null);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes) {
        add(node, failHandle, null, retryTimes);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        add(node, null, timeout, retryTimes);
    }

    public void addNode(@NonNull Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        add(node, failHandle, timeout, retryTimes);
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, null, null, null);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, Long timeout) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, null, timeout, null);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, AbstractNode.RetryTimesEnum retryTimes) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, null, null, retryTimes);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, AbstractNode.FailHandleEnum failHandle) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, failHandle, null, null);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, AbstractNode.FailHandleEnum failHandle, Long timeout) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, failHandle, timeout, null);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, AbstractNode.FailHandleEnum failHandle, AbstractNode.RetryTimesEnum retryTimes) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, failHandle, null, retryTimes);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, null, timeout, retryTimes);
        }
    }

    public void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeSet, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        if (CollectionUtils.isEmpty(nodeSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeSet) {
            add(node, failHandle, timeout, retryTimes);
        }
    }

    /**
     * 添加节点
     *
     * @param node       node
     * @param failHandle failHandle
     * @param timeout    timeout
     * @param retryTimes retryTimes
     */
    private void add(Class<? extends AbstractNode> node, AbstractNode.FailHandleEnum failHandle, Long timeout, AbstractNode.RetryTimesEnum retryTimes) {
        if (Objects.isNull(node)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        AbstractNode abstractNode = NodeManager.getNode(node, failHandle, timeout, retryTimes);
        if (Objects.isNull(abstractNode)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_UNREGISTERED.getMsg() + "=" + node.getName());
        }

        if (nodeMap.containsKey(node)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_REPEAT.getMsg() + "=" + node.getName());
        }

        nodeMap.put(node, abstractNode);

        Set<Class<? extends AbstractNode>> dependsOnNodes = abstractNode.getDependsOnNodes();
        parentNodeMap.put(node, dependsOnNodes);

        if (CollectionUtils.isEmpty(dependsOnNodes)) {
            firstNodeMap.put(node, abstractNode);
        } else {
            dependsOnNodes.forEach(item -> childNodeMap.computeIfAbsent(item, value -> new HashSet<>()).add(node));
        }
    }

    /**
     * 设置节点信息，添加节点操作
     */
    protected abstract void setNodeInfo();

    /**
     * 执行当前链路，使用默认线程池
     *
     * @param chainContext chainContext
     */
    public void execute(@NonNull ChainContext<?> chainContext) {
        executeNode(chainContext, getThreadPool(), firstNodeMap.values(), new HashMap<>(), new HashMap<>());
    }

    /**
     * 执行当前链路，指定线程池，如果为空则使用默认配置的线程池
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     */
    public void execute(@NonNull ChainContext<?> chainContext, @NonNull ExecutorService executorService) {
        executeNode(chainContext, executorService, firstNodeMap.values(), new HashMap<>(), new HashMap<>());
    }

    /**
     * 校验链路完整性
     */
    private void checkChainComplete() {
        if (!CollectionUtils.isEmpty(parentNodeMap)){
            parentNodeMap.forEach((k, v) -> {
                if (!CollectionUtils.isEmpty(v)){
                    v.forEach(item -> {
                        if (Objects.isNull(nodeMap.get(item))){
                            throw new ProcessException(ProcessException.MsgEnum.CHAIN_INCOMPLETE.getMsg() + "=" + item.getName());
                        }
                    });
                }
            });
        }
    }

    /**
     * 执行节点
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     * @param abstractNodes   abstractNodes
     * @param nodesStatusMap  nodesStatusMap
     * @param retriedMap      retriedMap
     */
    private void executeNode(ChainContext<?> chainContext, ExecutorService executorService, Collection<AbstractNode> abstractNodes,
                             Map<Class<? extends AbstractNode>, Boolean> nodesStatusMap, Map<String, Integer> retriedMap) {
        // 处理线程上下文配置
        Map<Object, AbstractThreadContextConfig> paramMap = getThreadContextInitConfigMap();
        Set<AbstractThreadContextConfig> threadContextInitConfigs = getThreadContextInitConfigs();

        // 组装future
        Map<Future<Void>, AbstractNode> futureMap = new HashMap<>();
        for (AbstractNode abstractNode : abstractNodes) {
            futureMap.put(CompletableFuture.supplyAsync(() -> {
                initThreadContext(paramMap);
                abstractNode.execute(chainContext, getAbstractNodeLogLevel(abstractNode.getClass()), this.getClass().getName());
                removeThreadContext(threadContextInitConfigs);
                return null;
            }, executorService), abstractNode);
        }

        String logStr = LOG_PREFIX + chainContext.getLogStr();
        for (Map.Entry<Future<Void>, AbstractNode> futureEntry : futureMap.entrySet()) {
            ProcessException processException = null;
            BusinessException businessException = null;
            Exception exception = null;
            Future<Void> future = futureEntry.getKey();
            AbstractNode abstractNode = futureEntry.getValue();
            Long timeout = abstractNode.getTimeout();
            int failHandle = abstractNode.getFailHandle().getCode();
            int retryTimes = abstractNode.getRetryTimes().getCode();
            Class<? extends AbstractNode> astractNodeClass = abstractNode.getClass();
            String nodeName = astractNodeClass.getName();

            // 执行节点
            try {
                future.get(timeout, TimeUnit.MILLISECONDS);
                abstractNode.onSuccess(chainContext);
            } catch (TimeoutException e) {
                if (AbstractNode.FailHandleEnum.RETRY.getCode() != failHandle) {
                    abstractNode.onTimeoutFail(chainContext);
                }
                exception = e;
                // 中断超时线程，不一定成功
                boolean cancel = future.cancel(true);
                log.info("{} execute timeout node [{}] timeout={} cancel={}", logStr, nodeName, timeout, cancel);
                processException = new ProcessException(ProcessException.MsgEnum.NODE_TIMEOUT.getMsg() + "=" + nodeName);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof ProcessException) {
                    if (AbstractNode.FailHandleEnum.RETRY.getCode() != failHandle) {
                        abstractNode.onUnknowFail(chainContext, (Exception) e.getCause());
                    }
                    exception = e;
                    log.info("{} execute process fail node [{}] msg={}", logStr, nodeName, getExceptionLog(e));
                    processException = (ProcessException) e.getCause();
                } else if (e.getCause() instanceof BusinessException) {
                    exception = e;
                    log.info("{} execute business fail node [{}] msg={}", logStr, nodeName, getExceptionLog(e));
                    if (AbstractNode.FailHandleEnum.RETRY.getCode() != failHandle) {
                        abstractNode.onBusinessFail(chainContext, (BusinessException) e.getCause());
                        throw (BusinessException) e.getCause();
                    } else {
                        businessException = (BusinessException) e.getCause();
                    }
                } else {
                    if (AbstractNode.FailHandleEnum.RETRY.getCode() != failHandle) {
                        abstractNode.onUnknowFail(chainContext, (Exception) e.getCause());
                    }
                    exception = e;
                    String exceptionLog = getExceptionLog(e);
                    log.info("{} execute fail node [{}] msg={}", logStr, nodeName, exceptionLog);
                    processException = new ProcessException(ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
                }
            } catch (Exception e) {
                if (AbstractNode.FailHandleEnum.RETRY.getCode() != failHandle) {
                    abstractNode.onUnknowFail(chainContext, e);
                }
                exception = e;
                String exceptionLog = getExceptionLog(e);
                log.info("{} execute fail node [{}] msg={}", logStr, nodeName, exceptionLog);
                processException = new ProcessException(ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + exceptionLog);
            } finally {
                if (AbstractNode.FailHandleEnum.RETRY.getCode() != failHandle) {
                    abstractNode.afterProcess(chainContext);
                }
            }

            // 失败处理
            if (Objects.nonNull(processException) || Objects.nonNull(businessException)) {
                failHandle(chainContext, executorService, failHandle, logStr, nodeName, timeout, processException, exception, abstractNode, nodesStatusMap, retriedMap, retryTimes);
            }

            // 节点执行完
            nodesStatusMap.put(astractNodeClass, true);

            // 执行子节点
            if (chainContext.getExecuteChildNode()) {
                executeChildNode(chainContext, executorService, astractNodeClass, nodesStatusMap, retriedMap);
            }
        }
    }

    /**
     * 失败处理
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     * @param failHandle      failHandle
     * @param logStr          logStr
     * @param nodeName        nodeName
     * @param timeout         timeout
     * @param exception       exception
     * @param abstractNode    abstractNode
     * @param nodesStatusMap  nodesStatusMap
     * @param retriedMap      retriedMap
     * @param retryTimes      retryTimes
     */
    private void failHandle(ChainContext<?> chainContext, ExecutorService executorService, int failHandle, String logStr,
                            String nodeName, Long timeout, ProcessException processException, Exception exception, AbstractNode abstractNode,
                            Map<Class<? extends AbstractNode>, Boolean> nodesStatusMap, Map<String, Integer> retriedMap, int retryTimes) {
        if (AbstractNode.FailHandleEnum.INTERRUPT.getCode() == failHandle) {
            log.info("{} execute fail interrupt node [{}] timeout={}", logStr, nodeName, timeout);
            throw processException;
        } else if (AbstractNode.FailHandleEnum.ABANDON.getCode() == failHandle) {
            log.info("{} execute fail abandon node [{}] timeout={}", logStr, nodeName, timeout);
        } else if (AbstractNode.FailHandleEnum.RETRY.getCode() == failHandle) {
            Set<AbstractNode> retryAbstractNodeSet = new HashSet<>();
            retryAbstractNodeSet.add(abstractNode);

            // 当前重试次数
            int nowRetryCount;
            if (retriedMap.containsKey(nodeName)) {
                if (retriedMap.get(nodeName) >= retryTimes) {
                    // 打印上一次重试失败
                    log.info("{} execute fail retry fail node [{}] timeout={} retryTimes={} retriedTimes={}", logStr, nodeName, timeout, retryTimes, retriedMap.get(nodeName));

                    if (exception instanceof TimeoutException) {
                        abstractNode.onTimeoutFail(chainContext);
                    } else if (exception instanceof ExecutionException) {
                        if (exception.getCause() instanceof ProcessException) {
                            abstractNode.onUnknowFail(chainContext, (Exception) exception.getCause());
                        } else if (exception.getCause() instanceof BusinessException) {
                            abstractNode.onBusinessFail(chainContext, (BusinessException) exception.getCause());
                            abstractNode.afterProcess(chainContext);
                            throw (BusinessException) exception.getCause();
                        } else {
                            abstractNode.onUnknowFail(chainContext, (Exception) exception.getCause());
                        }
                    } else {
                        abstractNode.onUnknowFail(chainContext, exception);
                    }

                    // 直接中断的两种考虑
                    // 1. 既然是需要重试的节点，那么肯定是比较重要的数据，不可缺失
                    // 2. 防止一组里面有多个一样的节点互相影响重试次数，也可以通过清掉key解决
                    abstractNode.afterProcess(chainContext);
                    throw processException;
                }

                // 打印上一次重试失败
                log.info("{} execute fail retry node [{}] timeout={} retryTimes={} retriedTimes={}", logStr, nodeName, timeout, retryTimes, retriedMap.get(nodeName));
                retriedMap.put(nodeName, retriedMap.get(nodeName) + 1);
                nowRetryCount = retriedMap.get(nodeName) + 1;
                executeNode(chainContext, executorService, retryAbstractNodeSet, nodesStatusMap, retriedMap);
            } else {
                // 第一次重试
                retriedMap.put(nodeName, 1);
                nowRetryCount = 1;
                executeNode(chainContext, executorService, retryAbstractNodeSet, nodesStatusMap, retriedMap);
            }

            if (retriedMap.containsKey(nodeName) && retriedMap.get(nodeName) == nowRetryCount) {
                // 打印本次重试成功
                log.info("{} execute fail retry success node [{}] timeout={} retryTimes={} retriedTimes={}", logStr, nodeName, timeout, retryTimes, nowRetryCount);
                abstractNode.afterProcess(chainContext);
            }
        } else {
            // 默认中断
            log.info("{} execute fail default interrupt node [{}] timeout={}", logStr, nodeName, timeout);
            throw processException;
        }
    }

    /**
     * 执行子节点
     *
     * @param chainContext     chainContext
     * @param executorService  executorService
     * @param astractNodeClass astractNodeClass
     * @param nodesStatusMap   nodesStatusMap
     * @param retriedMap       retriedMap
     */
    private void executeChildNode(ChainContext<?> chainContext, ExecutorService executorService, Class<? extends AbstractNode> astractNodeClass,
                                  Map<Class<? extends AbstractNode>, Boolean> nodesStatusMap, Map<String, Integer> retriedMap) {
        nodesStatusMap.put(astractNodeClass, true);
        Set<Class<? extends AbstractNode>> dependentNodeSet = parentNodeMap.get(astractNodeClass);
        if (!CollectionUtils.isEmpty(dependentNodeSet)) {
            Set<AbstractNode> toExecuteDependentNodeSet = new HashSet<>();
            for (Class<? extends AbstractNode> dependentNode : dependentNodeSet) {
                if ((Objects.isNull(nodesStatusMap.get(dependentNode)) || !nodesStatusMap.get(dependentNode)) && childNodeMap.get(astractNodeClass).stream().allMatch(nodesStatusMap::get)) {
                    toExecuteDependentNodeSet.add(nodeMap.get(dependentNode));
                    nodesStatusMap.put(astractNodeClass, false);
                }
            }
            executeNode(chainContext, executorService, toExecuteDependentNodeSet, nodesStatusMap, retriedMap);
        }
    }

    /**
     * 获取节点日志级别
     *
     * @param astractNodeClass astractNodeClass
     * @return 节点日志级别
     */
    private AbstractNode.LogLevelEnum getAbstractNodeLogLevel(Class<? extends AbstractNode> astractNodeClass) {
        // 处理节点日志级别
        AbstractNode.LogLevelEnum nodeLogLevel = null;
        LogLevelEnum chainLogLevel = this.logLevel;
        boolean baseAndTimeAndFirstAndLastNodesParamsLogLevel = false;
        if (LogLevelEnum.NO.getCode() == chainLogLevel.getCode()) {
            nodeLogLevel = AbstractNode.LogLevelEnum.NO;
        } else if (LogLevelEnum.BASE.getCode() == chainLogLevel.getCode()) {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE;
        } else if (LogLevelEnum.BASE_AND_TIME.getCode() == chainLogLevel.getCode()) {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME;
        } else if (LogLevelEnum.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode() == chainLogLevel.getCode()) {
            baseAndTimeAndFirstAndLastNodesParamsLogLevel = true;
        } else if (LogLevelEnum.BASE_AND_TIME_AND_ALL_NODES_PARAMS.getCode() == chainLogLevel.getCode()) {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS;
        } else {
            nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME;
        }

        if (baseAndTimeAndFirstAndLastNodesParamsLogLevel) {
            if (CollectionUtils.isEmpty(parentNodeMap.get(astractNodeClass)) || CollectionUtils.isEmpty(childNodeMap.get(astractNodeClass))) {
                nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS;
            } else {
                nodeLogLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME;
            }
        }

        return nodeLogLevel;
    }

    /**
     * 获取线程上下文配置map
     *
     * @return 线程上下文配置map
     */
    private Map<Object, AbstractThreadContextConfig> getThreadContextInitConfigMap() {
        Map<Object, AbstractThreadContextConfig> paramMap = new HashMap<>();
        Set<AbstractThreadContextConfig> threadContextInitConfigs = getThreadContextInitConfigs();
        if (!CollectionUtils.isEmpty(threadContextInitConfigs)) {
            for (AbstractThreadContextConfig item : threadContextInitConfigs) {
                if (Objects.nonNull(item)) {
                    if (item instanceof KeyThreadContextConfig) {
                        paramMap.put(((KeyThreadContextConfig) item).getGetContextByKey().apply(((KeyThreadContextConfig) item).getKey()), item);
                    } else if (item instanceof SingletonThreadContextConfig) {
                        paramMap.put(((SingletonThreadContextConfig) item).getGetContext().get(), item);
                    }
                }
            }
        }
        return paramMap;
    }

    /**
     * 获取拼接错误日志
     *
     * @param e e
     * @return 拼接错误日志
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

    /**
     * 获取执行线程池
     *
     * @return ExecutorService
     */
    @NonNull
    protected ExecutorService getThreadPool() {
        return ThreadPoolManager.COMMON_CHAIN_THREAD_POOL;
    }

    /**
     * 获取线程上下文配置
     *
     * @return String
     */
    protected Set<AbstractThreadContextConfig> getThreadContextInitConfigs() {
        return Collections.emptySet();
    }

    /**
     * 初始化线程上下文配置
     *
     * @param paramMap paramMap
     */
    private void initThreadContext(Map<Object, AbstractThreadContextConfig> paramMap) {
        if (!CollectionUtils.isEmpty(paramMap)) {
            paramMap.forEach((k, v) -> {
                if (Objects.nonNull(v)) {
                    if (v instanceof KeyThreadContextConfig) {
                        ((KeyThreadContextConfig) v).getSetContextByKey().accept(((KeyThreadContextConfig) v).getKey(), k);
                    } else if (v instanceof SingletonThreadContextConfig) {
                        ((SingletonThreadContextConfig) v).getSetContext().accept(k);
                    }
                }
            });
        }
    }

    /**
     * 清除线程上下文配置
     *
     * @param threadContextInitConfigs threadContextInitConfigs
     */
    private void removeThreadContext(Set<AbstractThreadContextConfig> threadContextInitConfigs) {
        if (!CollectionUtils.isEmpty(threadContextInitConfigs)) {
            for (AbstractThreadContextConfig item : threadContextInitConfigs) {
                if (Objects.nonNull(item)) {
                    if (item instanceof KeyThreadContextConfig) {
                        ((KeyThreadContextConfig) item).getRemoveContextByKey().accept(((KeyThreadContextConfig) item).getKey());
                    } else if (item instanceof SingletonThreadContextConfig) {
                        ((SingletonThreadContextConfig) item).getRemoveContext().run();
                    }
                }
            }
        }
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

}
