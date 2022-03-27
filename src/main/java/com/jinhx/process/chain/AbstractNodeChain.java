package com.jinhx.process.chain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jinhx.process.annotation.NodeChain;
import com.jinhx.process.enums.ExceptionEnums;
import com.jinhx.process.enums.NodeChainLogLevelEnums;
import com.jinhx.process.enums.NodeFailHandleEnums;
import com.jinhx.process.enums.NodeLogLevelEnums;
import com.jinhx.process.exception.ProcessException;
import com.jinhx.process.manager.NodeManager;
import com.jinhx.process.node.AbstractNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 抽象节点链
 *
 * @author jinhx
 * @since 2021-08-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@NodeChain
public abstract class AbstractNodeChain extends LinkedHashMap<String, List<AbstractNode>> {

    private static final long serialVersionUID = 4780080785208529405L;

    private Integer logLevel = NodeChainLogLevelEnums.BASE_AND_TIME_AND_FIRST_AND_LAST_NODES_PARAMS.getCode();

    public void add(Class<? extends AbstractNode> node) {
        add(node.getSimpleName(), node, null, null);
    }

    public void add(Class<? extends AbstractNode> node, Integer failHandle) {
        add(node.getSimpleName(), node, failHandle, null);
    }

    public void add(Class<? extends AbstractNode> node, Long timeout) {
        add(node.getSimpleName(), node, null, timeout);
    }

    public void add(Class<? extends AbstractNode> node, Integer failHandle, Long timeout) {
        add(node.getSimpleName(), node, failHandle, timeout);
    }

    public void add(String groupName, Class<? extends AbstractNode> node) {
        add(groupName, node, null, null);
    }

    public void add(String groupName, Class<? extends AbstractNode> node, Integer failHandle) {
        add(groupName, node, failHandle, null);
    }

    public void add(String groupName, Class<? extends AbstractNode> node, Long timeout) {
        add(groupName, node, null, timeout);
    }

    public void addGroup(List<Class<? extends AbstractNode>> nodes) {
        int i = nodes.hashCode();
        for (Class<? extends AbstractNode> node : nodes) {
            add(String.valueOf(i), node, null, null);
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
        if (this.containsKey(groupName)) {
            this.get(groupName).add(NodeManager.getNode(node, failHandle, timeout));
        } else {
            this.put(groupName, Lists.newArrayList(NodeManager.getNode(node, failHandle, timeout)));
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
            Map<Future<Void>, AbstractNode> futureMap = Maps.newHashMap();
            // 多个node节点的组合节点，并行执行
            for (AbstractNode abstractNode : abstractNodeList) {
//                AbstractNode<T> abstractNode = SpringUtils.getBean(nodeClass);
//                futureList.add(ThreadPoolEnum.ARTICLE.getThreadPoolExecutor().submit(() -> {
//                    abstractNode.execute(context, finalLogLevel);
//                    return null;
//                }));
                Integer finalLogLevel = logLevel;

                if (Objects.nonNull(threadPoolExecutor)){
                    futureMap.put(CompletableFuture.supplyAsync(() -> {
                        abstractNode.execute(nodeChainContext, finalLogLevel, this.getClass().getSimpleName());
                        return null;
                    }, threadPoolExecutor), abstractNode);
                } else if (Objects.nonNull(getThreadPoolExecutor())) {
                    futureMap.put(CompletableFuture.supplyAsync(() -> {
                        abstractNode.execute(nodeChainContext, finalLogLevel, this.getClass().getSimpleName());
                        return null;
                    }, getThreadPoolExecutor()), abstractNode);
                } else {
                    throw new ProcessException(ExceptionEnums.NODE_CHAIN_THREAD_POOL_EXECUTOR_NOT_NULL);
                }
            }

            ProcessException processException = null;
            for (Map.Entry<Future<Void>, AbstractNode> futureEntry : futureMap.entrySet()) {
                Future<Void> future = futureEntry.getKey();
                AbstractNode abstractNode = futureEntry.getValue();
                Long timeout = abstractNode.getTimeout();
                Integer failHandle = abstractNode.getFailHandle();
                String nodeName = abstractNode.getClass().getSimpleName();
                try {
                    future.get(timeout, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    // 中断超时线程，不一定成功
                    boolean cancel = future.cancel(true);
                    log.error("nodeChainLog {} execute timeout nodeName={} timeout={} cancel={}", nodeChainContext.getLogStr(), nodeName, timeout, cancel);
                    processException = new ProcessException(ExceptionEnums.NODE_TIMEOUT);
                } catch (ProcessException e) {
                    log.error("nodeChainLog {} execute fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, ExceptionUtils.getStackTrace(e));
                    processException = e;
                } catch (Exception e) {
                    log.error("nodeChainLog {} execute fail nodeName={} msg={}", nodeChainContext.getLogStr(), nodeName, ExceptionUtils.getStackTrace(e));
                    processException = new ProcessException(ExceptionEnums.NODE_UNKNOWN);
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
