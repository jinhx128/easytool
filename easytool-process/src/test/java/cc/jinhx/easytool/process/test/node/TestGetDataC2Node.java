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
 * TestGetDataC2Node
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestGetDataC2Node extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    public Set<Class<? extends AbstractNode>> getDependsOnNodes() {
        return new HashSet<>(Arrays.asList(TestGetDataBNode.class, TestGetDataC1Node.class));
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void execute(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        System.out.println(Thread.currentThread().getName() + "start3");
        try {
            Thread.sleep(180L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        businessFail(-1, "出错了");
//        int i = 1/0;

        System.out.println(Thread.currentThread().getName() + "start4");
        if ("dataB".equals(contextInfo.getDataB()) && "dataC1".equals(contextInfo.getDataC1())){
            contextInfo.setDataC2(testService.getDataC2());
        } else {
//            int i = 1/0;
//            businessFail(-1, "出错了");
        }
    }

    @Override
    public void onSuccess(ChainContext<TestContext> testChainContext) {
        System.out.println("onSuccess：" + testChainContext.toString());
    }

    @Override
    public void onUnknowFail(ChainContext<TestContext> testChainContext, Exception e) {
        System.out.println("onUnknowFail：" + testChainContext.toString() + Arrays.toString(e.getStackTrace()));
    }

    @Override
    public void onBusinessFail(ChainContext<TestContext> testChainContext, BusinessException e) {
        System.out.println("onBusinessFail：" + testChainContext.toString() + e.getMsg());
    }

    @Override
    public void onTimeoutFail(ChainContext<TestContext> testChainContext) {
        System.out.println("onTimeoutFail：" + testChainContext.toString());
    }

    @Override
    public void afterExecute(ChainContext<TestContext> testChainContext) {
        System.out.println("afterExecute：" + testChainContext.toString());
    }

}