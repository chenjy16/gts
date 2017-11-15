package com.wufumall.example.b.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述: <br>
* @author: xiongkun
* @date: 2017年11月9日 上午10:28:45
 */
@Getter@Setter
public class ExampleBInsertRequest implements Serializable{    

    /**
	*/
	private static final long serialVersionUID = 5597322765221276464L;

	private String name;

    private Long number;
	
	
}