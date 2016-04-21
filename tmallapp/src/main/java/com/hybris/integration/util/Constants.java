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




public enum Constants
{
	MESSAGE_FILE_PREFIX("TMCMESSAGESINFORMATION", "Listener persistence prefix name."), ;


	private String value;

	private String desc;


	private Constants(String value, String desc)
	{
		this.value = value;
		this.desc = desc;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @return the desc
	 */
	public String getDesc()
	{
		return desc;
	}
}
