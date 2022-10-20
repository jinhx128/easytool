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
     * 设置节点信息，链路的执行顺序跟此处的添加顺序无关
     */
    @Override
    protected void setNodeInfo() {
        // 添加重试节点DemoGetDataANode，重试次数为1，并配置节点超时时间。执行遇到异常时重试执行该节点，达到最大重试次数后还未执行成功，则中断链路，并返回异常信息
        this.addRetryNode(DemoGetDataANode.class, ChainNode.RetryTimesEnum.ONE, DemoChain::getNodeTimeout);
        // 添加中断节点DemoGetDataBNode，DemoGetDataCNode，并配置节点超时时间。执行遇到异常时中断链路，并返回异常信息
        this.addInterruptNodes(Arrays.asList(DemoGetDataBNode.class, DemoGetDataCNode.class), DemoChain::getNodeTimeout);
        // 添加抛弃节点DemoGetDataDNode，并配置节点超时时间。执行遇到异常时抛弃该节点，继续执行后续节点
        this.addAbandonNode(DemoGetDataDNode.class, DemoChain::getNodeTimeout);
        // 添加中断节点DemoGetDataENode，DemoGetDataFNode，DemoGetDataGNode，并配置节点超时时间。执行遇到异常时中断链路，并返回异常信息
        this.addInterruptNodes(Arrays.asList(DemoGetDataENode.class, DemoGetDataFNode.class, DemoGetDataGNode.class), DemoChain::getNodeTimeout);
    }

    public static long getNodeTimeout() {
        return DEFAULT_NODE_TIMEOUT;
    }

}