package cc.jinhx.easytool.process.test.node;

import cc.jinhx.easytool.process.BusinessException;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.node.AbstractNode;
import cc.jinhx.easytool.process.test.context.TestContext;
import cc.jinhx.easytool.process.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * DemoGetDataENode
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestGetDataENode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return new HashSet<>(Arrays.asList(TestGetDataANode.class, TestGetDataBNode.class, TestGetDataC1Node.class, TestGetDataC2Node.class, TestGetDataDNode.class));
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void execute(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        if ("dataA".equals(contextInfo.getDataA()) && "dataB".equals(contextInfo.getDataB()) && "dataC1".equals(contextInfo.getDataC1())
                && "dataC2".equals(contextInfo.getDataC2()) && "dataD".equals(contextInfo.getDataD())){
            contextInfo.setDataE(testService.getDataE());
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