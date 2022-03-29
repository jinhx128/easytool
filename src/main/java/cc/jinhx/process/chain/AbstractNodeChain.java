package cc.jinhx.process.chain;

import cc.jinhx.process.annotation.NodeChain;
import cc.jinhx.process.enums.ExceptionEnums;
import cc.jinhx.process.enums.NodeChainLogLevelEnums;
import cc.jinhx.process.enums.NodeFailHandleEnums;
import cc.jinhx.process.enums.NodeLogLevelEnums;
import cc.jinhx.process.exception.BusinessException;
import cc.jinhx.process.exception.ProcessException;
import cc.jinhx.process.manager.NodeManager;
import cc.jinhx.process.node.AbstractNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * 抽象节点链
 *
 * @author jinhx
 * @since 2022-03-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@NodeChain
public abstract class AbstractNodeChain extends LinkedHashMap<String, List<AbstractNode>> {

    private static final long serialVersionUID = 4780080785208529405L;

    private Integer logLevel = NodeChainLogLevelEnums.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode();

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

    public void addSynNodeGroup(List<Class<? extends AbstractNode>> nodes) {
        addSynNodeGroup(nodes, null, null);
    }

    public void addSynNodeGroup(List<Class<? extends AbstractNode>> nodes, Integer failHandle) {
        addSynNodeGroup(nodes, failHandle, null);
    }

    public void addSynNodeGroup(List<Class<? extends AbstractNode>> nodes, Long timeout) {
        addSynNodeGroup(nodes, null, timeout);
    }

    public void addSynNodeGroup(List<Class<? extends AbstractNode>> nodes, Integer failHandle, Long timeout) {
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
    public void add(String groupName, Class<? extends AbstractNode> node, Integer failHandle, Long timeout) {
        AbstractNode abstractNode = NodeManager.getNode(node, failHandle, timeout);
        if (Objects.isNull(abstractNode)){
            throw new ProcessException(ExceptionEnums.NODE_UNREGISTERED.getMsg() + "=" + node.getName());
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
        if (NodeChainLogLevelEnums.NO.getCode().equals(logLevel)){
            logLevel = NodeLogLevelEnums.NO.getCode();
        } else if (NodeChainLogLevelEnums.BASE.getCode().equals(logLevel)) {
            logLevel = NodeLogLevelEnums.BASE.getCode();
        } else if (NodeChainLogLevelEnums.BASE_AND_TIME.getCode().equals(logLevel)) {
            logLevel = NodeLogLevelEnums.BASE_AND_TIME.getCode();
        } else if (NodeChainLogLevelEnums.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode().equals(logLevel)) {
            baseAndTimeAndFirstAndLastNodesParamsLogLevel = true;
        } else if (NodeChainLogLevelEnums.BASE_AND_TIME_AND_ALL_NODES_PARAMS.getCode().equals(logLevel)) {
            logLevel = NodeLogLevelEnums.BASE_AND_TIME_AND_PARAMS.getCode();
        } else {
            logLevel = NodeLogLevelEnums.BASE_AND_TIME.getCode();
        }

        int count = 1;
        for (Map.Entry<String, List<AbstractNode>> nodesEntry : this.entrySet()) {
            // 通过节点链日志设置节点日志级别
            if (baseAndTimeAndFirstAndLastNodesParamsLogLevel){
                if (count == 1 || count == this.entrySet().size()){
                    logLevel = NodeLogLevelEnums.BASE_AND_TIME.getCode();
                }else {
                    logLevel = NodeLogLevelEnums.BASE_AND_TIME_AND_PARAMS.getCode();
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
                    throw new ProcessException(ExceptionEnums.NODE_CHAIN_THREAD_POOL_EXECUTOR_NOT_NULL.getMsg() + "=" + nodeChainName);
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
                    // 中断超时线程，不一定成功
                    boolean cancel = future.cancel(true);
                    log.error("nodeChainLog {} execute timeout nodeName={} timeout={} cancel={}", nodeChainContext.getLogStr(), nodeName, timeout, cancel);
                    processException = new ProcessException(ExceptionEnums.NODE_TIMEOUT.getMsg() + "=" + nodeName);
                } catch (ProcessException e) {
                    log.error("nodeChainLog {} execute fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, ExceptionUtils.getStackTrace(e));
                    processException = e;
                } catch (BusinessException e) {
                    log.error("nodeChainLog {} execute business fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, ExceptionUtils.getStackTrace(e));
                    throw e;
                } catch (Exception e) {
                    log.error("nodeChainLog {} execute fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, ExceptionUtils.getStackTrace(e));
                    processException = new ProcessException(ExceptionEnums.NODE_UNKNOWN.getMsg() + "=" + nodeName);
                }

                // 降级处理
                if (Objects.nonNull(processException)) {
                    if (NodeFailHandleEnums.INTERRUPT.getCode().equals(failHandle)){
                        log.error("nodeChainLog {} execute fail interrupt nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                        throw processException;
                    } else if (NodeFailHandleEnums.ABANDON.getCode().equals(failHandle)){
                        log.error("nodeChainLog {} execute fail abandon nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                    }else {
                        // 默认中断
                        log.error("nodeChainLog {} execute fail default interrupt nodeName={} timeout={}", nodeChainContext.getLogStr(), nodeName, timeout);
                        throw processException;
                    }
                    // todo 重试
                    /* else if (NodeFailHandleEnums.RETRY.getCode().equals(failHandle)){
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

}
