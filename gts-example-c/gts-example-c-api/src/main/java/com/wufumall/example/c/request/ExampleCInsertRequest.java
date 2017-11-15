package com.wufumall.example.c.request;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述: <br>
* @author: xiongkun
* @date: 2017年11月9日 上午10:28:45
 */
@Getter@Setter
public class ExampleCInsertRequest implements Serializable{    

	/**
	*/
	private static final long serialVersionUID = -5347502912336461876L;

	private String number;

    private Integer type;

    private Integer status;
	
	
}