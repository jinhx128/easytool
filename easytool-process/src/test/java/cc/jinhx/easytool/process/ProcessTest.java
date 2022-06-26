package cc.jinhx.easytool.process;

import cc.jinhx.easytool.process.topology.TestTopology;
import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.handler.AbstractHandler;
import cc.jinhx.easytool.process.topology.AbstractTopology;
import cc.jinhx.easytool.process.topology.TopologyContext;
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

            TopologyContext<TestContext> testTopologyContext = buildTopologyContext(TestContext.class);

            @Override
            protected void checkParams() {
                System.out.println("校验参数");
            }

            @Override
            protected ProcessResult<TestContext> process() {
                testTopologyContext.getContextInfo().setReq("re");
                executeTopology(TestTopology.class, testTopologyContext);
                return buildSuccessResult(testTopologyContext.getContextInfo());
            }

        }.execute();
        System.out.println(processResult.getData());
    }

    @Test
    public void test2() {
        ProcessResult<TestContext> processResult = new AbstractHandler<TestContext>() {

            TopologyContext<TestContext> testTopologyContext = buildTopologyContext(TestContext.class);

            @Override
            protected void checkParams() {
                System.out.println("校验参数");
            }

            @Override
            protected ProcessResult<TestContext> process() {
                testTopologyContext.getContextInfo().setReq("req");
                executeTopology(TestTopology.class, AbstractTopology.LogLevelEnum.BASE_AND_TIME, testTopologyContext);
                return buildSuccessResult(testTopologyContext.getContextInfo());
            }

        }.execute();
        System.out.println(processResult.getData());
    }

}

