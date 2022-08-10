package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public Set<Class<? extends AbstractNode>> getChildNodes() {
        return new HashSet<>(Arrays.asList(TestGetAByReqNode.class, TestGetBByReqNode.class, TestGetC1ByANode.class, TestGetC2ByBNode.class, TestGetDNode.class));
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void process(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        if ("A".equals(contextInfo.getA()) && "B".equals(contextInfo.getB()) && "C1".equals(contextInfo.getC1())
                && "C2".equals(contextInfo.getC2()) && "D".equals(contextInfo.getD())){
            contextInfo.setE(testService.getE());
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
