package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.topology.TopologyContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TestGetEByAllNode
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestGetEByAllNode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    protected boolean isSkip(TopologyContext<TestContext> topologyContext) {
        return false;
    }

    @Override
    protected void process(TopologyContext<TestContext> topologyContext) {
        TestContext contextInfo = topologyContext.getContextInfo();
        if ("A".equals(contextInfo.getA()) && "B".equals(contextInfo.getB()) && "C1".equals(contextInfo.getC1())
                && "C2".equals(contextInfo.getC2()) && "D".equals(contextInfo.getD())){
            contextInfo.setE(testService.getE());
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
