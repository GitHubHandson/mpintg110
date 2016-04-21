package com.hybris.datahub.vo.response;

import java.io.Serializable;

public class DataHubResponse implements Serializable
{
	private static final long serialVersionUID = 5229548628900825985L;

	private String code = "0";

	private String msg = "success";

	private Object body;

	public DataHubResponse()
	{
	}

	public DataHubResponse(String code, String msg, Object body)
	{
		this.code = code;
		this.msg = msg;
		this.body = body;
	}

	public DataHubResponse(String code, String msg)
	{
		this.code = code;
		this.msg = msg;
	}

	public DataHubResponse(Object body)
	{
		this.body = body;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(Object body) {
		this.body = body;
	}
}
