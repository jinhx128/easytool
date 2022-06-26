package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.topology.TopologyContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * TestGetC1ByANode
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestGetC2ByBNode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    protected boolean isSkip(TopologyContext<TestContext> topologyContext) {
        return false;
    }

    @Override
    protected void process(TopologyContext<TestContext> topologyContext) {
        TestContext contextInfo = topologyContext.getContextInfo();
        System.out.println(Thread.currentThread().getName() + "start3");
        try {
            Thread.sleep(180L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Thread.currentThread().getName() + "start4");
        if ("B".equals(contextInfo.getB()) && "C1".equals(contextInfo.getC1())){
            contextInfo.setC2(testService.getC() + "2");
        } else {
            int i = 1/0;
//            businessFail(-1, "出错了");
        }
    }

    @Override
    public void onSuccess(TopologyContext<TestContext> testTopologyContext) {
        System.out.println("onSuccess：" + testTopologyContext.toString());
    }

    @Override
    public void onUnknowFail(TopologyContext<TestContext> testTopologyContext, Exception e) {
        System.out.println("onUnknowFail：" + testTopologyContext.toString() + Arrays.toString(e.getStackTrace()));
    }

    @Override
    public void onBusinessFail(TopologyContext<TestContext> testTopologyContext, BusinessException e) {
        System.out.println("onBusinessFail：" + testTopologyContext.toString() + e.getMsg());
    }

    @Override
    public void onTimeoutFail(TopologyContext<TestContext> testTopologyContext) {
        System.out.println("onTimeoutFail：" + testTopologyContext.toString());
    }

    @Override
    public void afterProcess(TopologyContext<TestContext> testTopologyContext) {
        System.out.println("afterProcess：" + testTopologyContext.toString());
    }

}
