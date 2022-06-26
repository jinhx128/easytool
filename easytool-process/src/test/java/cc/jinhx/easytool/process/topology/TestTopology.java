package cc.jinhx.easytool.process.topology;

import cc.jinhx.easytool.process.node.*;

import java.util.Arrays;

/**
 * TestTopology
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestTopology extends AbstractTopology {

    /**
     * 配置节点信息
     * 1. 通过内部addxxx方法，添加节点到拓扑图，执行顺序按照添加顺序
     * 2. 组内异步，与组外同步
     * 3. 添加一个同步节点，自己属于一个组，且组内只能有自己
     * 4. 添加一个异步节点/节点组
     *   4.1 可通过参数restartAsyncGroup控制是否要加入上一个添加的异步节点/节点组属于同组，默认是
     *   4.2 如果上一个是同步节点，则无法加入，自己只能属于一个新的组，后面添加的异步节点/节点组依然可以通过参数restartAsyncGroup控制
     */
    @Override
    protected void setNodeInfo() {
        // 添加一组异步执行的节点
        this.addAsyncNodeList(Arrays.asList(TestGetAByReqNode.class, TestGetBByReqNode.class));
        // 添加一个异步执行的节点，与下面添加的异步节点属于同组
        this.addAsyncNode(TestGetC2ByBNode.class, AbstractNode.FailHandleEnum.RETRY, AbstractNode.RetryTimesEnum.FIVE, true);
        this.addAsyncNode(TestGetC1ByANode.class, 1000L);
        // 添加一个异步执行的节点，与上面添加的异步节点属于同组
        this.addAsyncNode(TestGetDNode.class);
        // 添加一个同步执行的节点，自己属于一个组，且组内只能有自己
        this.addSyncNode(TestGetEByAllNode.class);
    }

}
