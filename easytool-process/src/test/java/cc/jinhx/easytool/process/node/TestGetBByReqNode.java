package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.AbstractNode;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.NodeChainContext;
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
    protected boolean isSkip(NodeChainContext<TestContext> nodeChainContext) {
        return false;
    }

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
