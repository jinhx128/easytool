package cc.jinhx.easytool.process.demo.chain;

import cc.jinhx.easytool.process.chain.*;
import cc.jinhx.easytool.process.demo.context.DemoContext;
import cc.jinhx.easytool.process.demo.node.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DemoChain
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Slf4j
@Component
public class DemoChain extends AbstractChain<DemoContext> {

    private static final AtomicInteger CHAIN_THREAD_POOL_COUNTER = new AtomicInteger(0);

    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 自定义链路线程池
     */
    public static final ThreadPoolExecutor CHAIN_THREAD_POOL =
            new ThreadPoolExecutor(
                    2, CPU_NUM * 2,
                    10, TimeUnit.MINUTES,
                    new LinkedBlockingQueue<>(1024),
                    (Runnable r) -> new Thread(r, "chain_thread_" + CHAIN_THREAD_POOL_COUNTER.incrementAndGet()),
                    (r, executor) -> log.info("chain has bean rejected" + r));

    private final static long DEFAULT_CHAIN_TIMEOUT = 1000L;

    private final static long DEFAULT_NODE_TIMEOUT = 500L;

    @Override
    protected long getChainTimeout() {
        return DEFAULT_CHAIN_TIMEOUT;
    }

    @Override
    protected ExecutorService getThreadPool() {
        // 执行该链路的线程池
        return CHAIN_THREAD_POOL;
    }

    @Override
    protected Set<AbstractThreadContextConfig> getThreadContextInitConfigs() {
        return new HashSet<>(Collections.singletonList(new KeyThreadContextConfig<>("traceId", MDC::get, MDC::put, MDC::remove)));
    }


    @Override
    protected void checkParams(ChainContext<DemoContext> chainContext) {

    }

    /**
     * 设置节点信息
     */
    @Override
    protected void setNodeInfo() {
        this.addRetryNode(DemoGetDataANode.class, ChainNode.RetryTimesEnum.ONE, DemoChain::getNodeTimeout);
        this.addInterruptNodes(Arrays.asList(DemoGetDataBNode.class, DemoGetDataCNode.class), DemoChain::getNodeTimeout);
        this.addAbandonNode(DemoGetDataDNode.class, DemoChain::getNodeTimeout);
        this.addInterruptNodes(Arrays.asList(DemoGetDataENode.class, DemoGetDataFNode.class, DemoGetDataGNode.class), DemoChain::getNodeTimeout);
    }

    public static long getNodeTimeout() {
        return DEFAULT_NODE_TIMEOUT;
    }

}