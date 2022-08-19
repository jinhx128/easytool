package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.ThreadUtil;
import cc.jinhx.easytool.process.node.AbstractNode;
import lombok.Data;
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
public class ChainMonitor {

    /**
     * 链路次数map
     */
    private static final Map<Class<? extends AbstractChain>, Long> chainClassCountMap = new ConcurrentHashMap<>();

    /**
     * 链路节点耗时map
     */
    private static final Map<Class<? extends AbstractChain>, Map<Class<? extends AbstractNode>, Long>> nodeClassTimeMap = new ConcurrentHashMap<>();


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
        ThreadUtil.COMMON_CHAIN_THREAD_POOL.execute(() -> {
            Map<Class<? extends AbstractNode>, Long> nodeClassLongMap = nodeClassTimeMap.computeIfAbsent(chainClass, v -> new ConcurrentHashMap<>());
            Long nodeClassTime = nodeClassLongMap.get(nodeClass);
            if (Objects.isNull(nodeClassTime)) {
                nodeClassTime = time;
            } else {
                nodeClassTime += time;
            }
            nodeClassLongMap.put(nodeClass, nodeClassTime);

            Long chainClassCount = chainClassCountMap.get(chainClass);
            if (Objects.isNull(chainClassCount)) {
                chainClassCount = 1L;
            } else {
                chainClassCount += 1;
            }
            chainClassCountMap.put(chainClass, chainClassCount);
        });
    }

    /**
     * 开启监控
     */
    private static void openMonitor() {
        ThreadUtil.CHAIN_MONITOR_SCHEDULER.scheduleAtFixedRate(() -> nodeClassTimeMap.forEach((chainClass, nodeClassTimeMap) -> {
            Long chainClassCount = chainClassCountMap.get(chainClass) / nodeClassTimeMap.size();
            StringBuffer logStr = new StringBuffer("process chainMonitorLog chainClass [" + chainClass + "] allCount=" + chainClassCount);
            nodeClassTimeMap.forEach((nodeClass, time) -> logStr.append(" nodeClass [").append(nodeClass).append(" allTime=").append(time).append(" time=").append(time / chainClassCount));
            log.info(logStr.toString());
            logStr.setLength(0);
        }), 0, 600 * 1000, TimeUnit.MILLISECONDS);
    }

}
