package cc.jinhx.easytool.process.test.node;

import cc.jinhx.easytool.process.test.context.TestContext;
import cc.jinhx.easytool.process.node.AbstractNode;
import cc.jinhx.easytool.process.test.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * TestGetDataBNode
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestGetDataBNode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return null;
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void execute(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if ("req".equals(contextInfo.getReq())){
            contextInfo.setDataB(testService.getDataB());
        }
    }

    @Override
    public void onUnknowFail(ChainContext<TestContext> chainContext, Exception e) {

    }

    @Override
    public void onBusinessFail(ChainContext<TestContext> chainContext, BusinessException e) {

    }

    @Override
    public void onTimeoutFail(ChainContext<TestContext> chainContext) {

    }

}