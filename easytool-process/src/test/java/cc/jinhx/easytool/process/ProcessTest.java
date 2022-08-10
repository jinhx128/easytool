package cc.jinhx.easytool.process;

import cc.jinhx.easytool.process.chain.AbstractChain;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.chain.TestChain;
import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.handler.AbstractHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * json工具，依赖jackson
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class ProcessTest {

    @Test
    public void test1() {
        ProcessResult<TestContext> processResult = new AbstractHandler<TestContext>() {

            ChainContext<TestContext> testChainContext = buildChainContext(TestContext.class);

            @Override
            protected void checkParams() {
                System.out.println("校验参数");
            }

            @Override
            protected ProcessResult<TestContext> process() {
                testChainContext.getContextInfo().setReq("re");
                executeChain(TestChain.class, testChainContext);
                return buildSuccessResult(testChainContext.getContextInfo());
            }

        }.execute();
        System.out.println(processResult.getData());
    }

    @Test
    public void test2() {
        ProcessResult<TestContext> processResult = new AbstractHandler<TestContext>() {

            ChainContext<TestContext> testChainContext = buildChainContext(TestContext.class);

            @Override
            protected void checkParams() {
                System.out.println("校验参数");
            }

            @Override
            protected ProcessResult<TestContext> process() {
                testChainContext.getContextInfo().setReq("req");
                executeChain(TestChain.class, AbstractChain.LogLevelEnum.BASE_AND_TIME, testChainContext);
                return buildSuccessResult(testChainContext.getContextInfo());
            }

        }.execute();
        System.out.println(processResult.getData());
    }

}

