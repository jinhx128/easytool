package cc.jinhx.easytool.process.monitor;

import cc.jinhx.easytool.process.ThreadUtil;
import cc.jinhx.easytool.process.chain.AbstractChain;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 链路监控
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@Data
public class Monitor {

    /**
     * 链路监控参数map
     */
    private static final Map<Class<? extends AbstractChain>, Map<Class<? extends AbstractNode>, NodeExecuteInfo>> chainNodeExecuteInfoMap = new ConcurrentHashMap<>();


    static {
        openMonitor();
    }


    /**
     * 添加执行次数
     *
     * @param chainClass chainClass
     * @param nodeClass  nodeClass
     * @param time       time
     */
    public static void addCount(Class<? extends AbstractChain> chainClass, Class<? extends AbstractNode> nodeClass, long time) {
        if (Objects.nonNull(chainClass) && Objects.nonNull(nodeClass) && time > 0) {
            ThreadUtil.COMMON_CHAIN_THREAD_POOL.execute(() -> {
                Map<Class<? extends AbstractNode>, NodeExecuteInfo> nodeExecuteInfoMap = chainNodeExecuteInfoMap.computeIfAbsent(chainClass, v -> new ConcurrentHashMap<>());
                NodeExecuteInfo nodeExecuteInfo = nodeExecuteInfoMap.get(nodeClass);
                if (Objects.isNull(nodeExecuteInfo)) {
                    nodeExecuteInfo = new NodeExecuteInfo(time, time, time, 1);
                } else {
                    nodeExecuteInfo.setTotalTime(nodeExecuteInfo.getTotalTime() + time);
                    nodeExecuteInfo.setTotalCount(nodeExecuteInfo.getTotalCount() + 1);
                    if (time < nodeExecuteInfo.getMinTime()) {
                        nodeExecuteInfo.setMinTime(time);
                    }
                    if (time > nodeExecuteInfo.getMaxTime()) {
                        nodeExecuteInfo.setMaxTime(time);
                    }
                }
                nodeExecuteInfoMap.put(nodeClass, nodeExecuteInfo);
            });
        }
    }

    /**
     * 开启监控
     */
    private static void openMonitor() {
        ThreadUtil.CHAIN_MONITOR_SCHEDULER.scheduleAtFixedRate(() -> chainNodeExecuteInfoMap.forEach((chainClass, nodeExecuteInfoMap) -> {
            StringBuffer logStr = new StringBuffer("process monitorLog chain [" + chainClass.getSimpleName() + "]");
            nodeExecuteInfoMap.forEach((nodeClass, nodeExecuteInfo) -> {
                if (Objects.nonNull(nodeExecuteInfo)) {
                    logStr.append(" node [").append(nodeClass.getSimpleName()).append("] averageTime=").append(nodeExecuteInfo.getTotalTime() / nodeExecuteInfo.getTotalCount())
                            .append(" minTime=").append(nodeExecuteInfo.getMinTime()).append(" maxTime=").append(nodeExecuteInfo.getMaxTime())
                            .append(" totalTime=").append(nodeExecuteInfo.getTotalTime()).append(" totalCount=").append(nodeExecuteInfo.getTotalCount());
                }
            });
            log.info(logStr.toString());
            logStr.setLength(0);
        }), 1200 * 1000, 1200 * 1000, TimeUnit.MILLISECONDS);
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class NodeExecuteInfo {

        /**
         * 最大耗时
         */
        private long maxTime;

        /**
         * 最小耗时
         */
        private long minTime;

        /**
         * 总耗时
         */
        private long totalTime;

        /**
         * 总次数
         */
        private long totalCount;

    }

}