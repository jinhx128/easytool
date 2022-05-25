package cc.jinhx.easytool.process;

import cc.jinhx.easytool.process.chain.TestNodeChain;
import cc.jinhx.easytool.process.context.TestContext;
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
        ProcessResult<TestContext> processResult = new AbstractLogicHandler<TestContext>() {

            NodeChainContext<TestContext> testNodeChainContext = buildNodeChainContext(TestContext.class);

            @Override
            protected void checkParams() {
                System.out.println("校验参数");
            }

            @Override
            protected ProcessResult<TestContext> process() {
                testNodeChainContext.getContextInfo().setReq("re");
                executeNodeChain(TestNodeChain.class, testNodeChainContext);
                return buildSuccessResult(testNodeChainContext.getContextInfo());
            }

        }.execute();
        System.out.println(processResult.getData());
    }

    @Test
    public void test2() {
        ProcessResult<TestContext> processResult = new AbstractLogicHandler<TestContext>() {

            NodeChainContext<TestContext> testNodeChainContext = buildNodeChainContext(TestContext.class);

            @Override
            protected void checkParams() {
                System.out.println("校验参数");
            }

            @Override
            protected ProcessResult<TestContext> process() {
                testNodeChainContext.getContextInfo().setReq("req");
                executeNodeChain(TestNodeChain.class, AbstractNodeChain.LogLevelEnum.BASE_AND_TIME, testNodeChainContext);
                return buildSuccessResult(testNodeChainContext.getContextInfo());
            }

        }.execute();
        System.out.println(processResult.getData());
    }

}

