package com.wf.gts.manage.controller;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wf.gts.common.beans.TransItem;
import com.wf.gts.common.enums.TransRoleEnum;
import com.wf.gts.common.enums.TransStatusEnum;
import com.wf.gts.manage.service.GtsManagerService;


@Controller
@RequestMapping("/gtsManage")
public class ManagerIndexController {
  
    @Autowired
    private final GtsManagerService gtsManagerService; 
    
    @Autowired
    public ManagerIndexController(GtsManagerService gtsManagerService) {
        this.gtsManagerService=gtsManagerService;
    }

   
    
    @RequestMapping("/tx/list")
    @ResponseBody
    public Object list(HttpServletRequest request) { 
    	List<List<TransItem>> list = gtsManagerService.listTxTransactionItem(); 
    	list.stream().forEach(group->{
    		group.stream().forEach(item ->{
    			if(TransStatusEnum.PRE_COMMIT.getCode() == item.getStatus()){
    				item.setStatusValue(TransStatusEnum.PRE_COMMIT.getDesc());
    			}else if(TransStatusEnum.BEGIN.getCode() == item.getStatus()){
    				item.setStatusValue(TransStatusEnum.BEGIN.getDesc());
    			}else if(TransStatusEnum.COMMIT.getCode() == item.getStatus()){
    				item.setStatusValue(TransStatusEnum.COMMIT.getDesc());
    			}else if(TransStatusEnum.ROLLBACK.getCode() == item.getStatus()){
    				item.setStatusValue(TransStatusEnum.ROLLBACK.getDesc());
    			}
    			if(TransRoleEnum.START.getCode() == item.getRole()){
    				item.setRoleValue(TransRoleEnum.START.getDesc());
    			}else if(TransRoleEnum.ACTOR.getCode() == item.getRole()){
    				item.setRoleValue(TransRoleEnum.ACTOR.getDesc());
    			}
    		});
    		
    	});
        return list;
    }

}
