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
package com.hybris.integration.util;




public enum ResponseCode
{
	REQUEST_DATAHUB_ERROR("10001", "Request data hub failure."),

	REQUEST_TMALL_ERROR("10002", "Request tmall failure."),

	REQUEST_BODY_IS_EMPTY("10003", "Request body can not be empty."),

	MISSING_REQUIRED_PARAMS("10004", "Missing required parameters."),

	OBJECT_NOT_FOUND("10005", "Object not found."),

	OBJECT_EXPIRED("10006", "Object expired."),

	CONNECTION_REFUSED("10007", "Connection refused:connect."),

	AES_ENCRYPTION_ERROR("sys_001", "AES encryption failure."),

	AES_DECRYPTION_ERROR("sys_002", "AES decryption failure."),

	INTERNAL_SERVER_ERROR("sys_0023", "Internal server error.")

	;

	private String code;

	private String value;


	private ResponseCode(final String code, final String value)
	{
		this.code = code;
		this.value = value;

	}

	/**
	 * @param code
	 * @return value
	 */
	public static String getExtValueByCode(final String code)
	{
		for (final ResponseCode e : ResponseCode.values())
		{
			if (e.getCode().equals(code))
			{
				return e.value;
			}
		}
		return null;
	}


	/**
	 * @return code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @param code
	 */
	public void setCode(final String code)
	{
		this.code = code;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *           the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

}
