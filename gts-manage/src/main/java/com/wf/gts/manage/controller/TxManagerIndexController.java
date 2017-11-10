package com.wf.gts.manage.controller;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wf.gts.common.beans.TxTransactionItem;
import com.wf.gts.common.enums.TransactionRoleEnum;
import com.wf.gts.common.enums.TransactionStatusEnum;
import com.wf.gts.manage.entity.TxManagerInfo;
import com.wf.gts.manage.service.TxManagerInfoService;
import com.wf.gts.manage.service.TxManagerService;


@Controller
@RequestMapping("/gtsManage")
public class TxManagerIndexController {
  
  
    private final TxManagerInfoService txManagerInfoService;
    
    @Autowired
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
    
    
    @RequestMapping("/tx/list")
    @ResponseBody
    public Object list(HttpServletRequest request) { 
    	List<List<TxTransactionItem>> list = txManagerService.listTxTransactionItem(); 
    	if(list == null || list.size() == 0){
    		List<TxTransactionItem> tiList = new ArrayList<>();
    		TxTransactionItem ti = new TxTransactionItem();
    		ti.setTxGroupId("group1");
    		ti.setTransId("12");
    		ti.setTmDomain("tmDomain");
    		ti.setModelName("modelName");
    		ti.setRole(TransactionRoleEnum.START.getCode());
    		ti.setStatus(TransactionStatusEnum.BEGIN.getCode());
    		tiList.add(ti);
    		
    		TxTransactionItem tii = new TxTransactionItem();
    		tii.setTxGroupId("group1");
    		tii.setTransId("12dsf");
    		tii.setTmDomain("tmDomain");
    		tii.setModelName("modelName");
    		tii.setRole(TransactionRoleEnum.START.getCode());
    		tii.setStatus(TransactionStatusEnum.BEGIN.getCode());
    		tiList.add(tii);
    		list.add(tiList);
    		
    		List<TxTransactionItem> tiList1 = new ArrayList<>();
    		TxTransactionItem ti1 = new TxTransactionItem();
    		ti1.setTxGroupId("group2");
    		ti1.setTransId("123hgfjty");
    		ti1.setTmDomain("tmDomain");
    		ti1.setModelName("modelName");
    		ti1.setRole(TransactionRoleEnum.START.getCode());
    		ti1.setStatus(TransactionStatusEnum.BEGIN.getCode());
    		tiList1.add(ti1);
    		
    		TxTransactionItem ti11 = new TxTransactionItem();
    		ti11.setTxGroupId("group2");
    		ti11.setTransId("123cxgfr");
    		ti11.setTmDomain("tmDomain");
    		ti11.setModelName("modelName");
    		ti11.setRole(TransactionRoleEnum.START.getCode());
    		ti11.setStatus(TransactionStatusEnum.BEGIN.getCode());
    		tiList1.add(ti11);
    		
    		list.add(tiList);
    		
    		List<TxTransactionItem> tiList2 = new ArrayList<>();
    		TxTransactionItem ti2 = new TxTransactionItem();
    		ti2.setTxGroupId("group3");
    		ti2.setTransId("123dsfcv");
    		ti2.setTmDomain("tmDomain");
    		ti2.setModelName("modelName");
    		ti2.setRole(TransactionRoleEnum.START.getCode());
    		ti2.setStatus(TransactionStatusEnum.BEGIN.getCode());
    		tiList2.add(ti2);
    		
    		TxTransactionItem ti22 = new TxTransactionItem();
    		ti22.setTxGroupId("group3");
    		ti22.setTransId("123erws");
    		ti22.setTmDomain("tmDomain");
    		ti22.setModelName("modelName");
    		ti22.setRole(TransactionRoleEnum.START.getCode());
    		ti22.setStatus(TransactionStatusEnum.BEGIN.getCode());
    		tiList2.add(ti2);
    		
    		list.add(tiList);
    	} 
        return list;
    }

}
