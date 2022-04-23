package cc.jinhx.demo.node;

import cc.jinhx.process.AbstractNode;
import cc.jinhx.process.NodeChainContext;
import cc.jinhx.demo.context.TestContext;
import cc.jinhx.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TestGetDNode
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestGetDNode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    protected void process(NodeChainContext<TestContext> testNodeChainContext) {
        TestContext contextInfo = testNodeChainContext.getContextInfo();
        contextInfo.setD(testService.getD());
    }

}
