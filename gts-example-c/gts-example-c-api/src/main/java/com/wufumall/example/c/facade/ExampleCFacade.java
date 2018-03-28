package com.wufumall.example.c.facade;


import com.wufumall.example.c.request.ExampleCInsertRequest;

public interface ExampleCFacade {
    
    BaseCommonResult insert(ExampleCInsertRequest request);
    
    BaseCommonResult fail(ExampleCInsertRequest request);
    
    BaseCommonResult timeout(ExampleCInsertRequest request);
}
