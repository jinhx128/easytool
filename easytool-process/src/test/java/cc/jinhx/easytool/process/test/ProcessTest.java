package cc.jinhx.easytool.process.test;

import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.SpringContextConfig;
import cc.jinhx.easytool.process.ThreadUtil;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.chain.ChainHandler;
import cc.jinhx.easytool.process.test.chain.TestChain;
import cc.jinhx.easytool.process.test.context.TestContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * ProcessTest
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContextConfig.class)
public class ProcessTest {

    @Autowired
    private TestChain testChain;

    @Test
    public void test1() {
//        ProcessResult<DemoContext> processResult = new AbstractHandler<DemoContext>() {
//
//            ChainContext<DemoContext> testChainContext = buildChainContext(DemoContext.class);
//
//            @Override
//            protected void checkParams() {
//                System.out.println("校验参数");
//            }
//
//            @Override
//            protected ProcessResult<DemoContext> process() {
//                testChainContext.getContextInfo().setReq("re");
//                return executeChain(TestChain.class, testChainContext);
//            }
//
//        }.execute();
//        System.out.println(processResult.getData());
    }

    @Test
    public void test2() {
//        ProcessResult<DemoContext> processResult = new AbstractHandler<DemoContext>() {
//
//            ChainContext<DemoContext> testChainContext = buildChainContext(DemoContext.class);
//
//            @Override
//            protected void checkParams() {
//                System.out.println("校验参数");
//            }
//
//            @Override
//            protected ProcessResult<DemoContext> process() {
//                testChainContext.getContextInfo().setReq("req");
//                return executeChain(TestChain.class, testChainContext);
//            }
//
//        }.execute();
//        System.out.println(processResult.getData());
    }

    @Test
    public void test3() {
        ChainContext<TestContext> chainContext = ChainContext.create(TestContext.class);
        chainContext.getContextInfo().setReq("req");
        ProcessResult<TestContext> processResult = testChain.execute(chainContext);
        System.out.println(processResult);
    }

    @Test
    public void test4() {
        ChainContext<TestContext> chainContext = ChainContext.create(TestContext.class);
        chainContext.getContextInfo().setReq("req");
        ProcessResult<String> processResult = testChain.execute(chainContext, TestContext::getDataE);
        System.out.println(processResult);
    }

    @Test
    public void test5() {
        ChainContext<TestContext> chainContext = ChainContext.create(TestContext.class);
        chainContext.getContextInfo().setReq("req");
        ProcessResult<TestContext> processResult = ChainHandler.execute(TestChain.class, chainContext);
        System.out.println(processResult);
    }

    @Test
    public void test6() {
        ChainContext<TestContext> chainContext = ChainContext.create(TestContext.class);
        chainContext.getContextInfo().setReq("req");
        ProcessResult<String> processResult = ChainHandler.execute(TestChain.class, chainContext, TestContext::getDataE);
        System.out.println(processResult);
    }

    @Test
    public void test7() throws InterruptedException {
        int count = 1;
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 10; i > count; i--) {
            int finalI = i;
            ThreadUtil.withinTime(CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(finalI * 100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).thenRun(countDownLatch::countDown), Duration.ofMillis(200)).exceptionally(throwable -> {
                countDownLatch.countDown();
                return null;
            });
        }

        countDownLatch.await();
        long time = System.currentTimeMillis() - start;
        System.out.println(time);
    }

}