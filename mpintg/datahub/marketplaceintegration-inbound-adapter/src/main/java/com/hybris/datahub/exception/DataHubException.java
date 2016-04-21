package com.hybris.datahub.exception;

import com.hybris.datahub.util.CommonUtils;
import com.hybris.datahub.vo.response.DataHubResponse;


public class DataHubException extends RuntimeException
{

	private static final long serialVersionUID = -5325392504946334907L;

	protected static DataHubResponse dataHubResponse;

	/**
	 * AppException
	 */
	public DataHubException()
	{
		super();
	}

	/**
	 * @param code
	 * @param msg
	 */
	public DataHubException(String code, String msg)
	{
		super(initErrorMessage(code, msg, null));

	}

	/**
	 * @param code
	 * @param msg
	 * @param body
	 */
	public DataHubException(String code, String msg, Object body)
	{
		super(initErrorMessage(code, msg, body));
	}

	private static String initErrorMessage(String code, String msg, Object body)
	{
		dataHubResponse = new DataHubResponse(code, msg, body);
		String responseJson = CommonUtils.getGsonByBuilder(false).toJson(dataHubResponse);
		return responseJson;
	}


}
