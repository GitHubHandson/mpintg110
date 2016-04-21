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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;


/**
 * Commonly used tools to obtain client information
 *
 */
public final class HttpHelper
{
	/**
	 * Get requesting host IP address, if coming through a proxy, you get the real IP address through the firewall;
	 *
	 * @param request
	 * @return String
	 * @throws IOException
	 */
	public final static String getIpAddress(final HttpServletRequest request) throws IOException
	{

		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
			{
				ip = request.getRemoteAddr();
			}
		}
		else if (ip.length() > 15)
		{
			final String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++)
			{
				final String strIp = ips[index];
				if (!("unknown".equalsIgnoreCase(strIp)))
				{
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}
}
