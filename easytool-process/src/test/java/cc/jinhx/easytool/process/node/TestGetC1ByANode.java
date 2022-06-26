package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.topology.TopologyContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TestGetC1ByANode
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestGetC1ByANode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    protected boolean isSkip(TopologyContext<TestContext> topologyContext) {
        return false;
    }

    @Override
    protected void process(TopologyContext<TestContext> topologyContext) {
        System.out.println(Thread.currentThread().getName() + "start1");
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "start2");
        TestContext contextInfo = topologyContext.getContextInfo();
        if ("A".equals(contextInfo.getA())){
            contextInfo.setC1(testService.getC() + "1");
        }
    }

    @Override
    public void onUnknowFail(TopologyContext<TestContext> topologyContext, Exception e) {

    }

    @Override
    public void onBusinessFail(TopologyContext<TestContext> topologyContext, BusinessException e) {

    }

    @Override
    public void onTimeoutFail(TopologyContext<TestContext> topologyContext) {

    }

}
