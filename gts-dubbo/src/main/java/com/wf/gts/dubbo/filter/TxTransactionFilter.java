package com.wf.gts.dubbo.filter;
import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.wf.gts.core.util.TxTransactionLocal;

@Activate(group = {Constants.SERVER_KEY, Constants.CONSUMER})
public class TxTransactionFilter implements Filter {

  
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (RpcContext.getContext().isConsumerSide()) {
            RpcContext.getContext().setAttachment("tx-group",TxTransactionLocal.getInstance().getTxGroupId());
        }
        return invoker.invoke(invocation);
    }
}
