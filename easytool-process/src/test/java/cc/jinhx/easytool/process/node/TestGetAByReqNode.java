package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * TestGetAByReqNode
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestGetAByReqNode extends AbstractNode<TestContext> {

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
    protected void process(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        if ("req".equals(contextInfo.getReq())){
            contextInfo.setA(testService.getA());
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