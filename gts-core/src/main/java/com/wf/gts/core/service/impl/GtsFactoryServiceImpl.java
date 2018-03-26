package com.wf.gts.core.service.impl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.wf.gts.core.bean.GtsTransInfo;
import com.wf.gts.core.handler.JoinGtsTransHandler;
import com.wf.gts.core.handler.StartGtsTransHandler;
import com.wf.gts.core.service.GtsFactoryService;


@Service
public class GtsFactoryServiceImpl implements GtsFactoryService<GtsTransInfo> {
    
    @Override
    public Class factoryOf(GtsTransInfo info) throws Throwable {
      
        if (StringUtils.isBlank(info.getTxGroupId())) {
            return StartGtsTransHandler.class;
        } else {
            return JoinGtsTransHandler.class;
        }

    }
}
