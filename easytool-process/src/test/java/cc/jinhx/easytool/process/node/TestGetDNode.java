package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public Set<Class<? extends AbstractNode>> getChildNodes() {
        return null;
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void process(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        contextInfo.setD(testService.getD());
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
