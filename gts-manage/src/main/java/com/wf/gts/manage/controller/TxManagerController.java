package com.wf.gts.manage.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.entity.TxManagerServer;
import com.wf.gts.common.entity.TxManagerServiceDTO;
import com.wf.gts.manage.entity.TxManagerInfo;
import com.wf.gts.manage.executer.impl.HttpTransactionExecutor;
import com.wf.gts.manage.service.TxManagerInfoService;


@RestController
@RequestMapping("/tx/manager")
public class TxManagerController {

    private final TxManagerInfoService txManagerInfoService;

    private final HttpTransactionExecutor httpTransactionExecutor;

    @Autowired
    public TxManagerController(TxManagerInfoService txManagerInfoService, HttpTransactionExecutor transactionExecutor) {
        this.txManagerInfoService = txManagerInfoService;
        this.httpTransactionExecutor = transactionExecutor;
    }

    @ResponseBody
    @PostMapping("/findTxManagerServer")
    public TxManagerServer findTxManagerServer() {
        return txManagerInfoService.findTxManagerServer();
    }

    @ResponseBody
    @PostMapping("/loadTxManagerService")
    public List<TxManagerServiceDTO> loadTxManagerService() {
        return txManagerInfoService.loadTxManagerService();
    }

    @RequestMapping("/findTxManagerInfo")
    public TxManagerInfo findTxManagerInfo() {
        return txManagerInfoService.findTxManagerInfo();
    }

    @PostMapping("/httpCommit")
    public void httpCommit(@RequestBody List<TxTransactionItem> items) {
        httpTransactionExecutor.commit(items);
    }


    @PostMapping("/httpRollBack")
    public void httpRollBack(@RequestBody List<TxTransactionItem> items) {
        httpTransactionExecutor.rollBack(items);
    }


}
