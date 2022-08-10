package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * TestGetC1ByANode
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestGetC1ByANode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return new HashSet<>(Arrays.asList(TestGetAByReqNode.class));
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void process(ChainContext<TestContext> chainContext) {
        System.out.println(Thread.currentThread().getName() + "start1");
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "start2");
        TestContext contextInfo = chainContext.getContextInfo();
        if ("A".equals(contextInfo.getA())){
            contextInfo.setC1(testService.getC() + "1");
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
