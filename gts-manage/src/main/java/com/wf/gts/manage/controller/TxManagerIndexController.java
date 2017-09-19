package com.wf.gts.manage.controller;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.Lists;
import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.manage.entity.TxManagerInfo;
import com.wf.gts.manage.service.TxManagerInfoService;
import com.wf.gts.manage.service.TxManagerService;



@Controller
public class TxManagerIndexController {

    private final TxManagerInfoService txManagerInfoService;
    private final TxManagerService txManagerService;

    @Autowired
    public TxManagerIndexController(TxManagerInfoService txManagerInfoService,TxManagerService txManagerService) {
        this.txManagerInfoService = txManagerInfoService;
        this.txManagerService=txManagerService;
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        final TxManagerInfo txManagerInfo = txManagerInfoService.findTxManagerInfo();
        request.setAttribute("info", txManagerInfo);
        return "index";
    }
    
    
    @RequestMapping("tx/list")
    @ResponseBody
    public Object list(HttpServletRequest request) {
        Collection<String> keys=txManagerService.listTxGroupId();
        List<TxTransactionItem> res=Lists.newArrayList();
        keys.stream().forEach(key->{
          List<TxTransactionItem> list=txManagerService.listByTxGroupId(key);
          res.addAll(list);
        });
        return res;
    }
    


}
