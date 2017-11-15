package com.wufumall.example.b.facade;

import com.wufumall.core.dto.result.BaseCommonResult;
import com.wufumall.example.b.request.ExampleBInsertRequest;

public interface ExampleBFacade {
    
    BaseCommonResult insert(ExampleBInsertRequest request);
    
    BaseCommonResult fail(ExampleBInsertRequest request);
    
    BaseCommonResult timeout(ExampleBInsertRequest request);
}
