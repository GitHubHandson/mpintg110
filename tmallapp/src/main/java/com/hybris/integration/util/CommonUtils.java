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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hybris.integration.exception.TmallAppException;


/**
 * CommonUtils
 */
public class CommonUtils
{

	private static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

	/**
	 * Get all dates within three months
	 *
	 * @param startDate
	 * @param nowDate
	 *
	 * @return Sample Data:
	 *         <p>
	 *         key:value-->start:2001-01-01 00:00:00 <br>
	 *         or <br>
	 *         end:2001-01-01 23:59:59 <br>
	 */
	public static List<Map<String, Date>> findDates(Date startDate, Date nowDate)
	{
		List<Map<String, Date>> lDate = new ArrayList<Map<String, Date>>();
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		SimpleDateFormat ymdmhs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

		Calendar calendar = Calendar.getInstance();
		if (nowDate == null && startDate == null)
		{

			nowDate = calendar.getTime();
			calendar.add(Calendar.MONTH, -3);
			startDate = calendar.getTime();
		}
		else if (nowDate != null && startDate == null)
		{

			calendar.add(Calendar.MONTH, -3);
			startDate = calendar.getTime();

		}
		else if (nowDate == null && startDate != null)
		{
			nowDate = calendar.getTime();
		}

		try
		{
			Map<String, Date> beginItem = new HashMap<String, Date>();
			beginItem.put("start", ymdmhs.parse(ymd.format(startDate) + " 00:00:00"));
			beginItem.put("end", ymdmhs.parse(ymd.format(startDate) + " 23:59:59"));

			lDate.add(beginItem);

			Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(startDate);

			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(startDate);

			while (ymd.parse(ymd.format(nowDate)).after(calBegin.getTime()))
			{
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
				Map<String, Date> dateItem = new HashMap<String, Date>();

				dateItem.put("start", ymdmhs.parse(ymd.format(calBegin.getTime()) + " 00:00:00"));
				dateItem.put("end", ymdmhs.parse(ymd.format(calBegin.getTime()) + " 23:59:59"));

				lDate.add(dateItem);
			}
		}
		catch (ParseException e)
		{
			LOGGER.error(e.getMessage());
		}
		return lDate;
	}

	/**
	 *
	 * @param source
	 *           </br> Format:yyyy-MM-dd HH:mm:ss
	 * @return Date
	 * @throws ParseException
	 */
	public static Date stringToDate(String source) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		if (StringUtils.isNotEmpty(source))
		{
			return sdf.parse(source);
		}
		return null;
	}

	public static Date lastSecondOfTheDayTime(String source) throws TmallAppException
	{
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date newDate = null;
		try
		{
			Date oldDate = ymd.parse(source);
			newDate = ymdhms.parse(ymd.format(oldDate) + " 23:59:59");
		}
		catch (ParseException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new TmallAppException(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
		}
		return newDate;
	}

	/**
	 * @return Gson instance
	 */
	public static Gson getGsonByBuilder()
	{
		return getGsonByBuilder(true, null);
	}

	/**
	 * @param whetherExposeAnnotation
	 * @return Gson instance
	 */
	public static Gson getGsonByBuilder(boolean whetherExposeAnnotation)
	{
		return getGsonByBuilder(whetherExposeAnnotation, null);
	}

	/**
	 * @param whetherExposeAnnotation
	 * @param dateFormat
	 * @return Gson instance
	 */
	public static Gson getGsonByBuilder(boolean whetherExposeAnnotation, String dateFormat)
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		if (whetherExposeAnnotation)
		{
			//Export only entity with @Expose annotation properties
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		}

		gsonBuilder.enableComplexMapKeySerialization();

		if (StringUtils.isEmpty(dateFormat))
		{
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		}
		gsonBuilder.setDateFormat(dateFormat);//.serializeNulls()
		gsonBuilder.setVersion(1.0);

		return gsonBuilder.create();
	}

	/**
	 * @param path
	 * @return String
	 */
	public static String readFile(String path)
	{
		String content = null;
		try
		{
			File file = new File(path);
			content = FileUtils.readFileToString(file, "UTF-8");
		}
		catch (NoSuchFileException e)
		{
			LOGGER.error("File not found:" + e.getMessage(), e);
		}
		catch (IOException e)
		{
			LOGGER.error("File read failed:" + e.getMessage(), e);
		}
		return content;
	}

	/**
	 * @param ins
	 * @return String
	 */
	public static String readFile(InputStream ins)
	{
		List<String> list;
		StringBuffer sb = new StringBuffer();
		try
		{
			list = IOUtils.readLines(ins, "UTF-8");
			Iterator<String> iter = list.iterator();
			while (iter.hasNext())
			{
				sb.append(iter.next());
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Exception while reading file " + e.getMessage(), e);
		}
		return sb.toString();
	}

	/**
	 * Read all the files in a folder
	 *
	 * @param filepath
	 * @param character
	 *           Fuzzy query based on the file name
	 * @return key=filename<br/>
	 *         value=filecontent
	 */
	public static Map<String, String> readFileContentOfTheFloder(String filepath, String character)
	{

		File file = new File(filepath);
		Map<String, String> result = new HashMap<String, String>();
		if (file.isDirectory())
		{
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++)
			{
				File readfile = new File(filepath + File.separator + filelist[i]);
				if (!readfile.isDirectory())
				{

					String tempFilePath = readfile.getAbsolutePath();
					String tempfileName = readfile.getName();

					if (StringUtils.isNotEmpty(character))
					{
						if (tempfileName.contains(character))
						{
							result.put(tempfileName, readFile(tempFilePath));
						}
					}
					else
					{
						result.put(tempfileName, readFile(tempFilePath));
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param filePath
	 * @param content
	 */
	public static void writeFile(String filePath, String content)
	{
		writeFile(filePath, null, content);
	}

	/**
	 * @param filePath
	 * @param suffix
	 * @param content
	 */
	public static void writeFile(String filePath, String suffix, String content)
	{
		suffix = StringUtils.isEmpty(suffix) ? "json" : suffix;
		File file = new File(filePath + "." + suffix);
		try
		{
			FileUtils.writeStringToFile(file, content, "UTF-8");
		}
		catch (IOException e)
		{
			LOGGER.error("File write error:" + e.getMessage(), e);
		}

	}
}
