package cc.jinhx.process;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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

    private Integer logLevel = AbstractNodeChain.LogLevelEnum.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode();

    private boolean asyncLastNode = false;

    private String lastNodeName;

    public void addSyncNode(Class<? extends AbstractNode> node) {
        addSyncNode(node, null, null);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, Integer failHandle) {
        addSyncNode(node, failHandle, null);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, Long timeout) {
        addSyncNode(node, null, timeout);
    }

    public void addSyncNode(Class<? extends AbstractNode> node, Integer failHandle, Long timeout) {
        add(node.getName(), node, failHandle, timeout);
        if (this.asyncLastNode){
            this.asyncLastNode = false;
        }
    }

    public void addAsyncNode(Class<? extends AbstractNode> node) {
        addAsyncNode(node, null, null, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Integer failHandle) {
        addAsyncNode(node, failHandle, null, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Long timeout) {
        addAsyncNode(node, null, timeout, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Integer failHandle, Long timeout) {
        addAsyncNode(node, failHandle, timeout, false);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, boolean restartAsyncNode) {
        addAsyncNode(node, null, null, restartAsyncNode);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Integer failHandle, boolean restartAsyncNode) {
        addAsyncNode(node, failHandle, null, restartAsyncNode);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Long timeout, boolean restartAsyncNode) {
        addAsyncNode(node, null, timeout, restartAsyncNode);
    }

    public void addAsyncNode(Class<? extends AbstractNode> node, Integer failHandle, Long timeout, boolean restartAsyncNode) {
        if (restartAsyncNode && this.asyncLastNode){
            this.asyncLastNode = false;
        }

        if (this.asyncLastNode) {
            add(this.lastNodeName, node, failHandle, timeout);
        } else {
            add(node.getName(), node, failHandle, timeout);
            this.asyncLastNode = true;
            this.lastNodeName = node.getName();
        }
    }

    public void addAsyncNodeGroup(List<Class<? extends AbstractNode>> nodes) {
        addAsyncNodeGroup(nodes, null, null);
    }

    public void addAsyncNodeGroup(List<Class<? extends AbstractNode>> nodes, Integer failHandle) {
        addAsyncNodeGroup(nodes, failHandle, null);
    }

    public void addAsyncNodeGroup(List<Class<? extends AbstractNode>> nodes, Long timeout) {
        addAsyncNodeGroup(nodes, null, timeout);
    }

    public void addAsyncNodeGroup(List<Class<? extends AbstractNode>> nodes, Integer failHandle, Long timeout) {
        int i = nodes.hashCode();
        for (Class<? extends AbstractNode> node : nodes) {
            add(String.valueOf(i), node, failHandle, timeout);
        }

        if (this.asyncLastNode){
            this.asyncLastNode = false;
        }
    }

    /**
     * 添加指定组节点，一个链路按理说只有一类型的节点，如果有多个，默认覆盖前面的，使用最后一个
     *
     * @param groupName groupName
     * @param node node
     * @param failHandle failHandle
     * @param timeout timeout
     */
    private void add(String groupName, Class<? extends AbstractNode> node, Integer failHandle, Long timeout) {
        AbstractNode abstractNode = NodeManager.getNode(node, failHandle, timeout);
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
     * 4. 添加一组异步节点
     * 5. 添加一个异步节点
     *   5.1 与上一个添加的异步节点属于同组
     *   5.2 如果上一个是同步节点，则自己属于新的组，后面添加的异步节点属于这个组
     *   5.3 也可以通过参数restartAsyncNode指定新开一个组，后面添加的
     */
    protected abstract void setNodeInfo();

    /**
     * 获取节点链默认线程池
     *
     * @return ThreadPoolExecutor
     */
    protected abstract ThreadPoolExecutor getThreadPoolExecutor();

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
     */
    public void execute(NodeChainContext<?> nodeChainContext, ThreadPoolExecutor threadPoolExecutor) {
        // 通过节点链日志设置节点日志级别
        Integer logLevel = this.logLevel;
        boolean baseAndTimeAndFirstAndLastNodesParamsLogLevel = false;
        if (LogLevelEnum.NO.getCode().equals(logLevel)){
            logLevel = AbstractNode.LogLevelEnum.NO.getCode();
        } else if (LogLevelEnum.BASE.getCode().equals(logLevel)) {
            logLevel = AbstractNode.LogLevelEnum.BASE.getCode();
        } else if (LogLevelEnum.BASE_AND_TIME.getCode().equals(logLevel)) {
            logLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME.getCode();
        } else if (LogLevelEnum.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode().equals(logLevel)) {
            baseAndTimeAndFirstAndLastNodesParamsLogLevel = true;
        } else if (LogLevelEnum.BASE_AND_TIME_AND_ALL_NODES_PARAMS.getCode().equals(logLevel)) {
            logLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS.getCode();
        } else {
            logLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME.getCode();
        }

        int count = 1;
        for (Map.Entry<String, List<AbstractNode>> nodesEntry : this.entrySet()) {
            // 通过节点链日志设置节点日志级别
            if (baseAndTimeAndFirstAndLastNodesParamsLogLevel){
                if (count == 1 || count == this.entrySet().size()){
                    logLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME.getCode();
                }else {
                    logLevel = AbstractNode.LogLevelEnum.BASE_AND_TIME_AND_PARAMS.getCode();
                }
                count++;
            }

            List<AbstractNode> abstractNodeList = nodesEntry.getValue();
            Map<Future<Void>, AbstractNode> futureMap = new HashMap<>();
            // 多个node节点的组合节点，并行执行
            for (AbstractNode abstractNode : abstractNodeList) {
//                AbstractNode<T> abstractNode = SpringUtils.getBean(nodeClass);
//                futureList.add(ThreadPoolEnum.ARTICLE.getThreadPoolExecutor().submit(() -> {
//                    abstractNode.execute(context, finalLogLevel);
//                    return null;
//                }));
                Integer finalLogLevel = logLevel;
                String nodeChainName = this.getClass().getName();
                if (Objects.nonNull(threadPoolExecutor)){
                    futureMap.put(CompletableFuture.supplyAsync(() -> {
                        abstractNode.execute(nodeChainContext, finalLogLevel, nodeChainName);
                        return null;
                    }, threadPoolExecutor), abstractNode);
                } else if (Objects.nonNull(getThreadPoolExecutor())) {
                    futureMap.put(CompletableFuture.supplyAsync(() -> {
                        abstractNode.execute(nodeChainContext, finalLogLevel, nodeChainName);
                        return null;
                    }, getThreadPoolExecutor()), abstractNode);
                } else {
                    throw new ProcessException(ProcessException.MsgEnum.NODE_CHAIN_THREAD_POOL_EXECUTOR_NOT_NULL.getMsg() + "=" + nodeChainName);
                }
            }

            ProcessException processException = null;
            for (Map.Entry<Future<Void>, AbstractNode> futureEntry : futureMap.entrySet()) {
                Future<Void> future = futureEntry.getKey();
                AbstractNode abstractNode = futureEntry.getValue();
                Long timeout = abstractNode.getTimeout();
                Integer failHandle = abstractNode.getFailHandle();
                String nodeName = abstractNode.getClass().getName();
                try {
                    future.get(timeout, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    // todo 中断超时线程，不一定成功
                    boolean cancel = future.cancel(true);
                    log.error("nodeChainLog {} execute timeout nodeName={} timeout={} cancel={}", nodeChainContext.getLogStr(), nodeName, timeout, cancel);
                    processException = new ProcessException(ProcessException.MsgEnum.NODE_TIMEOUT.getMsg() + "=" + nodeName);
                } catch (ProcessException e) {
                    log.error("nodeChainLog {} execute fail nodeName={} msg=", nodeChainContext.getLogStr(), nodeName, e);
                    processException = e;
                } catch (BusinessException e) {
                    log.error("nodeChainLog {} execute business fail nodeName={} msg=", nodeChainContext.getLogStr(), nodeName, e);
                    throw e;
                } catch (Exception e) {
                    log.error("nodeChainLog {} execute fail nodeName={} msg=", nodeChainContext.getLogStr(), nodeName, e);
                    processException = new ProcessException(ProcessException.MsgEnum.NODE_UNKNOWN.getMsg() + "=" + nodeName + " error=" + e.getMessage());
                }

                // 降级处理
                if (Objects.nonNull(processException)) {
                    if (AbstractNode.FailHandleEnum.INTERRUPT.getCode().equals(failHandle)){
                        log.error("nodeChainLog {} execute fail interrupt nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                        throw processException;
                    } else if (AbstractNode.FailHandleEnum.ABANDON.getCode().equals(failHandle)){
                        log.error("nodeChainLog {} execute fail abandon nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                    }else {
                        // 默认中断
                        log.error("nodeChainLog {} execute fail default interrupt nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                        throw processException;
                    }
                    // todo 重试
                    /* else if (AbstractNode.FailHandleEnum.RETRY.getCode().equals(failHandle)){
                        log.error("nodeChainLog {} execute fail retry nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                    }*/
                }
            }

            // 是否需要执行下一组节点
            if(Objects.nonNull(nodeChainContext.getExNextNodeGroup()) && !nodeChainContext.getExNextNodeGroup()){
                return;
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

}
