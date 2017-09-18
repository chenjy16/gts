package com.wf.gts.manage.controller;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wf.gts.manage.entity.TxManagerInfo;
import com.wf.gts.manage.service.TxManagerInfoService;



@Controller
public class TxManagerIndexController {

    private final TxManagerInfoService txManagerInfoService;

    @Autowired
    public TxManagerIndexController(TxManagerInfoService txManagerInfoService) {
        this.txManagerInfoService = txManagerInfoService;
    }

    @RequestMapping("/index")
    public String index(HttpServletRequest request) {
        final TxManagerInfo txManagerInfo = txManagerInfoService.findTxManagerInfo();
        request.setAttribute("info", txManagerInfo);
        return "index";
    }


}
