package cc.jinhx.demo.node;

import cc.jinhx.demo.context.TestContext;
import cc.jinhx.demo.service.TestService;
import cc.jinhx.process.AbstractNode;
import cc.jinhx.process.BusinessException;
import cc.jinhx.process.NodeChainContext;
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
