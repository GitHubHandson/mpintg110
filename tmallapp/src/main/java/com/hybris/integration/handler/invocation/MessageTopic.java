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
package com.hybris.integration.handler.invocation;


/**
 * Message Topic
 */
public enum MessageTopic
{

	/**
	 * TAOBAO_TRADE
	 */
	TAOBAO_TRADE("taobao_trade", "Tmall trade messages", true),


	/**
	 * TAOBAO_REFUND
	 */
	TAOBAO_REFUND("taobao_refund", "Tmall taobao refund messages", false),

	/**
	 * TBABAO_ITEM
	 */
	TBABAO_ITEM("taobao_item", "Tmall taobao product item messages", false),

	/**
	 * TAOBAO_FENXIAO
	 */
	TAOBAO_FENXIAO("taobao_fenxiao", "Tmall taobao FENXIAO messages", false)

	;

	private String code;

	private String desc;

	private Boolean isProcess;

	private MessageTopic(final String code, final String desc, final boolean isProcess)
	{
		this.code = code;
		this.desc = desc;
		this.isProcess = isProcess;

	}

	/**
	 * @param code
	 * @return desc
	 */
	public static String getExtNameByCode(final String code)
	{
		for (final MessageTopic e : MessageTopic.values())
		{
			if (e.getCode().equals(code))
			{
				return e.desc;
			}
		}
		return null;
	}

	/**
	 * @param code
	 * @return isProcess
	 */
	public static Boolean isExistProcess(final String code)
	{
		for (final MessageTopic e : MessageTopic.values())
		{
			if (e.getCode().equals(code))
			{
				return e.isProcess;
			}
		}
		return false;
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
	 * @return desc
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * @param desc
	 */
	public void setDesc(final String desc)
	{
		this.desc = desc;
	}

}
