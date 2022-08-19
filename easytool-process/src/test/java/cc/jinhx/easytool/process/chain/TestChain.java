package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.node.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

/**
 * TestChain
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestChain extends AbstractChain<TestContext> {

    @Override
    protected void checkParams(ChainContext<TestContext> chainContext) {

    }

    /**
     * 设置节点信息
     */
    @Override
    protected void setNodeInfo() {
        this.addInterruptNodes(Arrays.asList(TestGetAByReqNode.class));
        this.addInterruptNodes(Arrays.asList(TestGetBByReqNode.class), 7000);
        this.addRetryNode(TestGetC2ByBNode.class, ChainNode.RetryTimesEnum.FIVE);
        this.addInterruptNode(TestGetC1ByANode.class, 1000L);
        this.addAbandonNode(TestGetDNode.class);
        this.addInterruptNode(TestGetEByAllNode.class);
    }

}
