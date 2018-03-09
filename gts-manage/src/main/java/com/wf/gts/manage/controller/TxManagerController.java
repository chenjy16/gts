package com.wf.gts.manage.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.manage.executer.impl.HttpTransactionExecutor;


@RestController
@RequestMapping("/gtsManage/tx/manager")
public class TxManagerController {

    private final HttpTransactionExecutor httpTransactionExecutor;
    
    @Autowired
    public TxManagerController( HttpTransactionExecutor transactionExecutor) {
        this.httpTransactionExecutor = transactionExecutor;
    }
    
    /**
     * 功能描述: 提交事务
     * @author: chenjy
     * @date: 2017年9月18日 下午5:50:47 
     * @param items
     */
    @PostMapping("/httpCommit")
    public void httpCommit(@RequestBody List<TxTransactionItem> items) {
        httpTransactionExecutor.commit(items);
    }

    /**
     * 功能描述: 回滚事务
     * @author: chenjy
     * @date: 2017年9月18日 下午5:50:52 
     * @param items
     */
    @PostMapping("/httpRollBack")
    public void httpRollBack(@RequestBody List<TxTransactionItem> items) {
        httpTransactionExecutor.rollBack(items);
    }


}
