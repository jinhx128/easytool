package cc.jinhx.easytool.process.node;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.service.TestService;
import cc.jinhx.easytool.process.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * TestGetC1ByANode
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestGetC2ByBNode extends AbstractNode<TestContext> {

    @Autowired
    private TestService testService;

    @Override
    public Set<Class<? extends AbstractNode>> getChildNodes() {
        return new HashSet<>(Arrays.asList(TestGetBByReqNode.class));
    }

    @Override
    protected boolean isSkip(ChainContext<TestContext> chainContext) {
        return false;
    }

    @Override
    protected void process(ChainContext<TestContext> chainContext) {
        TestContext contextInfo = chainContext.getContextInfo();
        System.out.println(Thread.currentThread().getName() + "start3");
        try {
            Thread.sleep(180L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Thread.currentThread().getName() + "start4");
        if ("B".equals(contextInfo.getB()) && "C1".equals(contextInfo.getC1())){
            contextInfo.setC2(testService.getC() + "2");
        } else {
            int i = 1/0;
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
    public void afterProcess(ChainContext<TestContext> testChainContext) {
        System.out.println("afterProcess：" + testChainContext.toString());
    }

}
