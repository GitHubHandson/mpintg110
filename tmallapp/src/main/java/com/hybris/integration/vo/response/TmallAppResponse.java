/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.integration.vo.response;

import java.io.Serializable;


public class TmallAppResponse implements Serializable
{

	private static final long serialVersionUID = 5229548628900825985L;

	private String code = "0";

	private String msg = "success";

	private Object body;

	public TmallAppResponse()
	{
	}

	public TmallAppResponse(String code, String msg, Object body)
	{
		this.code = code;
		this.msg = msg;
		this.body = body;
	}

	public TmallAppResponse(String code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}

	public TmallAppResponse(Object body)
	{
		this.body = body;
	}

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 *           the code to set
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg()
	{
		return msg;
	}

	/**
	 * @param msg
	 *           the msg to set
	 */
	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	/**
	 * @return the body
	 */
	public Object getBody()
	{
		return body;
	}

	/**
	 * @param body
	 *           the body to set
	 */
	public void setBody(Object body)
	{
		this.body = body;
	}


}
