package cc.jinhx.demo.node;

import cc.jinhx.demo.context.TestContext;
import cc.jinhx.demo.service.TestService;
import cc.jinhx.process.AbstractNode;
import cc.jinhx.process.BusinessException;
import cc.jinhx.process.NodeChainContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TestGetBByReqNode
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestGetBByReqNode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    protected void process(NodeChainContext<TestContext> testNodeChainContext) {
        TestContext contextInfo = testNodeChainContext.getContextInfo();
        if ("req".equals(contextInfo.getReq())){
            contextInfo.setB(testService.getB());
        }
    }

    @Override
    protected void onUnknowFail(NodeChainContext<TestContext> nodeChainContext, Exception e) {

    }

    @Override
    protected void onBusinessFail(NodeChainContext<TestContext> nodeChainContext, BusinessException e) {

    }

    @Override
    protected void onTimeoutFail(NodeChainContext<TestContext> nodeChainContext) {

    }

}
