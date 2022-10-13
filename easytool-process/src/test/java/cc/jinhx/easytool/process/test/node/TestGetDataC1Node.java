package cc.jinhx.easytool.process.test.node;

import cc.jinhx.easytool.process.test.context.TestContext;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.node.AbstractNode;
import cc.jinhx.easytool.process.test.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * TestGetDataC1Node
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestGetDataC1Node extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return new HashSet<>(Arrays.asList(TestGetDataANode.class));
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void execute(ChainContext<TestContext> chainContext) {
        System.out.println(Thread.currentThread().getName() + "start1");
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "start2");
        TestContext contextInfo = chainContext.getContextInfo();
        if ("dataA".equals(contextInfo.getDataA())){
            contextInfo.setDataC1(testService.getDataC1());
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