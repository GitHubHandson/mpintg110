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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * ChineseUtill
 */
public class ChineseUtil
{

	private static boolean isChinese(final char c)
	{
		final Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
		{
			return true;
		}
		return false;
	}

	/**
	 * Determining whether the string is garbled
	 *
	 * @param strName
	 * @return false / ture
	 */
	public static boolean isMessyCode(final String strName)
	{
		final Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		final Matcher m = p.matcher(strName);
		final String after = m.replaceAll("");
		final String temp = after.replaceAll("\\p{P}", "");
		final char[] ch = temp.trim().toCharArray();
		float chLength = 0;
		float count = 0;
		for (int i = 0; i < ch.length; i++)
		{
			final char c = ch[i];
			if (!Character.isLetterOrDigit(c))
			{
				if (!isChinese(c))
				{
					count = count + 1;
				}
				chLength++;
			}
		}
		final float result = count / chLength;
		if (result > 0.4)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
