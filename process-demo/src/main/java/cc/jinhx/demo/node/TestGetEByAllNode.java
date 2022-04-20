package cc.jinhx.demo.node;

import cc.jinhx.demo.context.TestContext;
import cc.jinhx.demo.service.TestService;
import cc.jinhx.process.AbstractNode;
import cc.jinhx.process.NodeChainContext;
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
    public void process(NodeChainContext<TestContext> testNodeChainContext) {
        TestContext contextInfo = testNodeChainContext.getContextInfo();
        if ("A".equals(contextInfo.getA()) && "B".equals(contextInfo.getB()) && "C1".equals(contextInfo.getC1())
                && "C2".equals(contextInfo.getC2()) && "D".equals(contextInfo.getD())){
            contextInfo.setE(testService.getE());
        }
    }

}
