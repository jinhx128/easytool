package cc.jinhx.easytool.process.test.chain;

import cc.jinhx.easytool.process.chain.*;
import cc.jinhx.easytool.process.test.context.TestContext;
import cc.jinhx.easytool.process.test.node.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestChain
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Slf4j
@Component
public class TestChain extends AbstractChain<TestContext> {

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
    protected boolean openMonitor() {
        return true;
    }

    @Override
    protected ExecutorService getThreadPool() {
        // 执行该链路的线程池
        return CHAIN_THREAD_POOL;
    }

    @Override
    protected Set<AbstractThreadContextConfig> getThreadContextInitConfigs() {
        Set threadContextConfig = new HashSet();
        threadContextConfig.add(new KeyThreadContextConfig<>("traceId", MDC::get, MDC::put, MDC::remove));
        return threadContextConfig;
    }


    @Override
    protected void checkParams(ChainContext<TestContext> chainContext) {

    }

    /**
     * 设置节点信息
     */
    @Override
    protected void setNodeInfo() {
        this.addInterruptNodes(Collections.singletonList(TestGetDataANode.class), TestChain::getNodeTimeout);
        this.addInterruptNodes(Collections.singletonList(TestGetDataBNode.class), TestChain::getNodeTimeout);
        this.addRetryNode(TestGetDataC2Node.class, ChainNode.RetryTimesEnum.FIVE, TestChain::getNodeTimeout);
        this.addInterruptNode(TestGetDataC1Node.class, TestChain::getNodeTimeout);
        this.addAbandonNode(TestGetDataDNode.class, TestChain::getNodeTimeout);
        this.addInterruptNode(TestGetDataENode.class, TestChain::getNodeTimeout);
    }

    public static long getNodeTimeout() {
        return DEFAULT_NODE_TIMEOUT;
    }

}