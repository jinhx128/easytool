package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.*;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象链路
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Data
@Slf4j
public abstract class AbstractChain<T> {

    private static final String LOG_PREFIX = "process chainLog ";

    /**
     * 是否校验过
     */
    private boolean isChecked = false;

    /**
     * 首节点class集合
     */
    private Set<Class<? extends AbstractNode>> firstNodeClassSet = new HashSet<>();

    /**
     * 父节点class map
     */
    private Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> parentNodeClassMap = new HashMap<>();

    /**
     * 子节点class map
     */
    private Map<Class<? extends AbstractNode>, Set<Class<? extends AbstractNode>>> childNodeClassMap = new HashMap<>();

    /**
     * 链路节点集合
     */
    private Map<Class<? extends AbstractNode>, ChainNode> chainNodeMap = new HashMap<>();

    public AbstractChain() {
        setNodeInfo();
    }

    protected void addNode(@NonNull Class<? extends AbstractNode> nodeClass) {
        add(nodeClass, null, null, null);
    }

    protected void addNode(@NonNull Class<? extends AbstractNode> nodeClass, ChainNode.FailHandleEnum failHandle) {
        add(nodeClass, failHandle, null, null);
    }

    protected void addNode(@NonNull Class<? extends AbstractNode> nodeClass, long timeout) {
        add(nodeClass, null, null, timeout);
    }

    protected void addNode(@NonNull Class<? extends AbstractNode> nodeClass, ChainNode.FailHandleEnum failHandle, long timeout) {
        add(nodeClass, failHandle, null, timeout);
    }

    protected void addNode(@NonNull Class<? extends AbstractNode> nodeClass, ChainNode.FailHandleEnum failHandle, ChainNode.RetryTimesEnum retryTimes) {
        add(nodeClass, failHandle, retryTimes, null);
    }

    protected void addNode(@NonNull Class<? extends AbstractNode> nodeClass, ChainNode.FailHandleEnum failHandle, ChainNode.RetryTimesEnum retryTimes, long timeout) {
        add(nodeClass, failHandle, retryTimes, timeout);
    }

    protected void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet) {
        adds(nodeClassSet, null, null, null);
    }

    protected void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet, long timeout) {
        adds(nodeClassSet, null, null, timeout);
    }

    protected void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet, ChainNode.FailHandleEnum failHandle) {
        adds(nodeClassSet, failHandle, null, null);
    }

    protected void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet, ChainNode.FailHandleEnum failHandle, long timeout) {
        adds(nodeClassSet, failHandle, null, timeout);
    }

    protected void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet, ChainNode.FailHandleEnum failHandle, ChainNode.RetryTimesEnum retryTimes) {
        adds(nodeClassSet, failHandle, retryTimes, null);
    }

    protected void addNodes(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet, ChainNode.FailHandleEnum failHandle, ChainNode.RetryTimesEnum retryTimes, long timeout) {
        adds(nodeClassSet, failHandle, retryTimes, timeout);
    }

    /**
     * 添加节点
     *
     * @param nodeClassSet nodeClassSet
     * @param failHandle   failHandle
     * @param timeout      timeout
     * @param retryTimes   retryTimes
     */
    private void adds(@NonNull Set<Class<? extends AbstractNode>> nodeClassSet, ChainNode.FailHandleEnum failHandle, ChainNode.RetryTimesEnum retryTimes, Long timeout) {
        if (CollectionUtils.isEmpty(nodeClassSet)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        for (Class<? extends AbstractNode> node : nodeClassSet) {
            add(node, failHandle, retryTimes, timeout);
        }
    }

    /**
     * 添加节点
     *
     * @param nodeClass  nodeClass
     * @param failHandle failHandle
     * @param timeout    timeout
     * @param retryTimes retryTimes
     */
    private void add(Class<? extends AbstractNode> nodeClass, ChainNode.FailHandleEnum failHandle, ChainNode.RetryTimesEnum retryTimes, Long timeout) {
        if (Objects.isNull(nodeClass)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_EMPTY.getMsg() + "=" + this.getClass().getName());
        }

        if (chainNodeMap.containsKey(nodeClass)) {
            throw new ProcessException(ProcessException.MsgEnum.NODE_REPEAT.getMsg() + "=" + nodeClass.getName());
        }
        chainNodeMap.put(nodeClass, ChainNode.create(null, failHandle, timeout, retryTimes));
    }


    /**
     * 校验参数
     *
     * @param chainContext chainContext
     */
    protected abstract void checkParams(ChainContext<T> chainContext);

    /**
     * 设置节点信息，添加节点操作
     */
    protected abstract void setNodeInfo();

    /**
     * 初始化并校验链路完整性
     */
    protected void initChainAndCheckChainComplete() {
        if (!isChecked) {
            initChain();
            checkChainComplete();
            isChecked = true;
        }
    }

    /**
     * 初始化链路
     */
    protected void initChain() {
        chainNodeMap.forEach((nodeClass, chainNode) -> {
            if (Objects.isNull(chainNode.getNode())) {
                AbstractNode node = SpringUtil.getBean(nodeClass);
                if (Objects.isNull(node)) {
                    throw new ProcessException(ProcessException.MsgEnum.NODE_UNREGISTERED.getMsg() + "=" + nodeClass.getName());
                }

                chainNode.setNode(node);

                Set<Class<? extends AbstractNode>> dependsOnNodes = node.getDependsOnNodes();
                parentNodeClassMap.put(nodeClass, dependsOnNodes);

                if (CollectionUtils.isEmpty(dependsOnNodes)) {
                    firstNodeClassSet.add(nodeClass);
                } else {
                    dependsOnNodes.forEach(item -> childNodeClassMap.computeIfAbsent(item, value -> new HashSet<>()).add(nodeClass));
                }
            }
        });
    }

    /**
     * 校验链路完整性
     */
    protected void checkChainComplete() {
        if (!CollectionUtils.isEmpty(parentNodeClassMap)) {
            parentNodeClassMap.forEach((k, v) -> {
                if (!CollectionUtils.isEmpty(v)) {
                    v.forEach(item -> {
                        if (Objects.isNull(chainNodeMap.get(item)) || Objects.isNull(chainNodeMap.get(item).getNode())) {
                            throw new ProcessException(ProcessException.MsgEnum.CHAIN_INCOMPLETE.getMsg() + "=" + item.getName());
                        }
                    });
                }
            });
        }
    }

    /**
     * 校验参数
     *
     * @param chainContext chainContext
     */
    private ProcessResult<T> doCheckParams(ChainContext<T> chainContext) {
        StringBuffer logStr = new StringBuffer(LOG_PREFIX + chainContext.getLogStr());
        try {
            checkParams(chainContext);
            logStr.append(" checkParams success");
            log.info(logStr.toString());
            return null;
        } catch (BusinessException e) {
            onBusinessFail(chainContext, e);
            logStr.append(" checkParams business fail msg=").append(getExceptionLog(e));
            log.info(logStr.toString());
            return buildFailResult(e.getCode(), e.getMsg());
        } catch (Exception e) {
            onUnknowFail(chainContext, e);
            logStr.append(" checkParams unknown fail msg=").append(getExceptionLog(e));
            log.info(logStr.toString());
            return buildFailResult(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), ProcessException.MsgEnum.CHECK_PARAMS.getMsg() + " error=" + getExceptionLog(e));
        }
    }

    /**
     * 执行当前链路，使用默认配置的线程池
     *
     * @param chainContext chainContext
     */
    public ProcessResult<T> execute(@NonNull ChainContext<T> chainContext) {
        return doExecute(chainContext, getThreadPool());
    }

    /**
     * 执行当前链路，使用默认配置的线程池
     *
     * @param chainContext chainContext
     */
    public <R> ProcessResult<R> execute(@NonNull ChainContext<T> chainContext, Function<T, R> getResultData) {
        ProcessResult processResult = execute(chainContext);
        if (processResult.isSuccess()){
            processResult.setData(getResultData.apply((T) processResult.getData()));
            return processResult;
        }

        return processResult;
    }

    /**
     * 执行当前链路，指定线程池，如果为空则使用默认配置的线程池
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     */
    public ProcessResult<T> execute(@NonNull ChainContext<T> chainContext, @NonNull ExecutorService executorService) {
        return doExecute(chainContext, executorService);
    }

    /**
     * 执行当前链路，使用默认配置的线程池
     *
     * @param chainContext chainContext
     */
    public <R> ProcessResult<R> execute(@NonNull ChainContext<T> chainContext, Function<T, R> getResultData, @NonNull ExecutorService executorService) {
        ProcessResult processResult = execute(chainContext, executorService);
        if (processResult.isSuccess()){
            processResult.setData(getResultData.apply((T) processResult.getData()));
            return processResult;
        }

        return processResult;
    }

    /**
     * 执行节点
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     */
    private ProcessResult<T> doExecute(ChainContext<T> chainContext, ExecutorService executorService) {
        // 校验链路
        initChainAndCheckChainComplete();

        // 校验参数
        ProcessResult<T> processResult = doCheckParams(chainContext);
        if (Objects.nonNull(processResult)) {
            return processResult;
        }

        // 获取初始化链路参数
        ChainParam<T> chainParam = getInitChainParam();

        startRunNode(chainContext, executorService, firstNodeClassSet, chainParam);

        // 等待执行完成
        try {
            chainParam.getSuccessNodeCountDownLatch().await();
        } catch (InterruptedException e) {
            chainParam.setProcessResult(buildFailResult(ProcessResult.BaseEnum.UNKNOW_FAIL.getCode(), ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + " error=" + getExceptionLog(e)));
        }

        // 失败
        if (Objects.nonNull(chainParam.getProcessResult())) {
            if (chainParam.isTimeoutFail()) {
                onTimeoutFail(chainContext);
            } else if (chainParam.isBusinessFail()) {
                onBusinessFail(chainContext, (BusinessException) chainParam.getFailException());
            } else {
                onUnknowFail(chainContext, chainParam.getFailException());
            }

            afterExecute(chainContext);
            return chainParam.getProcessResult();
        }

        // 成功
        onSuccess(chainContext);
        afterExecute(chainContext);
        return buildSuccessResult(chainContext.getContextInfo());
    }


    /**
     * 获取初始化链路参数
     *
     * @return ChainParam<T>
     */
    private ChainParam<T> getInitChainParam() {
        ChainParam<T> chainParam = new ChainParam<>();
        // 获取线程上下文配置
        chainParam.setThreadContextInitConfigMap(getThreadContextInitConfigMap());
        chainParam.setThreadContextInitConfigSet(getThreadContextInitConfigs());

        // 初始化所有节点状态
        chainParam.setNodeClassStatusMap(chainNodeMap.entrySet().stream().collect(Collectors.toConcurrentMap(Map.Entry::getKey, v -> false, (v1, v2) -> v2)));
        // 初始化所有节点重试次数
        chainParam.setNodeClassRetryCountMap(chainNodeMap.entrySet().stream().collect(Collectors.toConcurrentMap(Map.Entry::getKey, v -> 0, (v1, v2) -> v2)));
        // 初始化计数器
        chainParam.setSuccessNodeCountDownLatch(new CountDownLatch(chainNodeMap.size()));
        chainParam.setTimeoutFail(false);
        chainParam.setBusinessFail(false);
        return chainParam;
    }

    /**
     * 开始跑节点
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     * @param nodeClasses     nodeClasses
     * @param chainParam      chainParam
     */
    protected void startRunNode(ChainContext<T> chainContext, ExecutorService executorService, Collection<Class<? extends AbstractNode>> nodeClasses, ChainParam<T> chainParam) {
        if (!CollectionUtils.isEmpty(nodeClasses)) {
            nodeClasses.forEach(nodeClass -> {
                ChainNode chainNode = chainNodeMap.get(nodeClass);
                if (Objects.nonNull(chainNode)) {
                    ThreadUtil.within(buildNodeFuture(chainContext, executorService, nodeClass, chainParam), Duration.ofMillis(chainNode.getTimeout()))
                            .thenRun(() -> startRunNode(chainContext, executorService, childNodeClassMap.get(nodeClass), chainParam))
                            .exceptionally(throwable -> {
                                chainNode.getFailHandle().getFailHandle().dealFailNode(chainContext, executorService, nodeClass, chainParam, chainNodeMap, childNodeClassMap, this, throwable);
                                return null;
                            });
                }
            });
        }
    }

    /**
     * 构建节点future
     *
     * @param chainContext    chainContext
     * @param executorService executorService
     * @param nodeClass       nodeClass
     * @param chainParam      chainParam
     * @return CompletableFuture<Void>
     */
    private CompletableFuture<Void> buildNodeFuture(ChainContext<T> chainContext, ExecutorService executorService, Class<? extends AbstractNode> nodeClass, ChainParam<T> chainParam) {
        return CompletableFuture.runAsync(() -> {
            // 不存在的
            if (Objects.isNull(nodeClass)) {
                return;
            }

            ChainNode chainNode = chainNodeMap.get(nodeClass);
            if (Objects.isNull(chainNode)) {
                return;
            }

            // 已经执行过的
            if (chainParam.getNodeClassStatusMap().get(nodeClass)) {
                return;
            }

            // 父节点还有未执行过的
            Set<Class<? extends AbstractNode>> parentNodeClassSet = parentNodeClassMap.get(nodeClass);
            if (!CollectionUtils.isEmpty(parentNodeClassSet) && parentNodeClassSet.stream().anyMatch(item -> !chainParam.getNodeClassStatusMap().get(item))) {
                return;
            }

            // 设置子线程上下文
            initThreadContext(chainParam.getThreadContextInitConfigMap());
            chainNode.getNode().doExecute(chainContext, this.getClass().getName());
            // 移除子线程上下文
            removeThreadContext(chainParam.getThreadContextInitConfigSet());

            // 节点执行成功
            chainParam.getSuccessNodeCountDownLatch().countDown();
            chainParam.getNodeClassStatusMap().put(nodeClass, true);
        }, executorService);
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
     * @param threadContextInitConfigMap threadContextInitConfigMap
     */
    private void initThreadContext(Map<Object, AbstractThreadContextConfig> threadContextInitConfigMap) {
        if (!CollectionUtils.isEmpty(threadContextInitConfigMap)) {
            threadContextInitConfigMap.forEach((k, v) -> {
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
     * @param threadContextInitConfigSet threadContextInitConfigSet
     */
    private void removeThreadContext(Set<AbstractThreadContextConfig> threadContextInitConfigSet) {
        if (!CollectionUtils.isEmpty(threadContextInitConfigSet)) {
            for (AbstractThreadContextConfig item : threadContextInitConfigSet) {
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


    /**
     * 构建成功结果
     */
    protected ProcessResult<T> buildSuccessResult(T data) {
        return new ProcessResult<>(data);
    }

    /**
     * 构建失败结果
     */
    protected ProcessResult<T> buildFailResult(int code, String msg) {
        return new ProcessResult<>(code, msg);
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
     * 成功时执行
     *
     * @param chainContext chainContext
     */
    protected void onSuccess(@NonNull ChainContext<T> chainContext) {
    }

    /**
     * 超时失败时执行
     *
     * @param chainContext chainContext
     */
    protected void onTimeoutFail(@NonNull ChainContext<T> chainContext) {
    }

    /**
     * 业务失败时执行
     *
     * @param chainContext chainContext
     */
    protected void onBusinessFail(@NonNull ChainContext<T> chainContext, @NonNull BusinessException e) {
    }

    /**
     * 未知失败时执行
     *
     * @param chainContext chainContext
     */
    protected void onUnknowFail(@NonNull ChainContext<T> chainContext, @NonNull Exception e) {
    }

    /**
     * 无论成功失败，最后都会执行
     *
     * @param chainContext chainContext
     */
    protected void afterExecute(@NonNull ChainContext<T> chainContext) {
    }

}
