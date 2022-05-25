package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.AbstractNode;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.NodeChainContext;
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
    protected boolean isSkip(NodeChainContext<TestContext> nodeChainContext) {
        return false;
    }

    @Override
    protected void process(NodeChainContext<TestContext> testNodeChainContext) {
        System.out.println(Thread.currentThread().getName() + "start1");
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "start2");
        TestContext contextInfo = testNodeChainContext.getContextInfo();
        if ("A".equals(contextInfo.getA())){
            contextInfo.setC1(testService.getC() + "1");
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
