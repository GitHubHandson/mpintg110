package com.hybris.datahub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

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
	public static List<Map<String, Date>> findDates(Date startDate, Date nowDate) {
		final List<Map<String, Date>> lDate = new ArrayList<Map<String, Date>>();
		final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		final SimpleDateFormat ymdmhs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		final Calendar calendar = Calendar.getInstance();
		if (nowDate == null && startDate == null) {

			nowDate = calendar.getTime();
			calendar.add(Calendar.MONTH, -3);
			startDate = calendar.getTime();
		} else if (nowDate != null && startDate == null) {

			calendar.add(Calendar.MONTH, -3);
			startDate = calendar.getTime();

		} else if (nowDate == null && startDate != null) {
			nowDate = calendar.getTime();
		}

		try {
			final Map<String, Date> beginItem = new HashMap<String, Date>();
			beginItem.put("start", ymdmhs.parse(ymd.format(startDate) + " 00:00:00"));
			beginItem.put("end", ymdmhs.parse(ymd.format(startDate) + " 23:59:59"));

			lDate.add(beginItem);

			final Calendar calBegin = Calendar.getInstance();
			calBegin.setTime(startDate);

			final Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(startDate);

			while (ymd.parse(ymd.format(nowDate)).after(calBegin.getTime())) {
				calBegin.add(Calendar.DAY_OF_MONTH, 1);
				final Map<String, Date> dateItem = new HashMap<String, Date>();

				dateItem.put("start", ymdmhs.parse(ymd.format(calBegin.getTime()) + " 00:00:00"));
				dateItem.put("end", ymdmhs.parse(ymd.format(calBegin.getTime()) + " 23:59:59"));

				lDate.add(dateItem);
			}
		} catch (final ParseException e) {
			LOGGER.error(e.getMessage());
		}
		return lDate;
	}

	/**
	 *
	 * @param source
	 *            </br>
	 *            Format:yyyy-MM-dd HH:mm:ss
	 * @return Date
	 * @throws ParseException
	 */
	public static Date stringToDate(final String source) throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		if (StringUtils.isNotEmpty(source)) {
			return sdf.parse(source);
		}
		return null;
	}

	/**
	 * @return Gson instance
	 */
	public static Gson getGsonByBuilder() {
		return getGsonByBuilder(true, null);
	}

	/**
	 * @param whetherExposeAnnotation
	 * @return Gson instance
	 */
	public static Gson getGsonByBuilder(final boolean whetherExposeAnnotation) {
		return getGsonByBuilder(whetherExposeAnnotation, null);
	}

	/**
	 * @param whetherExposeAnnotation
	 * @param dateFormat
	 * @return Gson instance
	 */
	public static Gson getGsonByBuilder(final boolean whetherExposeAnnotation, String dateFormat) {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		if (whetherExposeAnnotation) {
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		}

		gsonBuilder.enableComplexMapKeySerialization();

		if (StringUtils.isEmpty(dateFormat)) {
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		}
		gsonBuilder.setDateFormat(dateFormat);// .serializeNulls()
		gsonBuilder.setVersion(1.0);

		return gsonBuilder.create();
	}

	/**
	 * @param path
	 * @return String
	 */
	public static String readFile(final String path) {
		final File file = new File(path);

		FileInputStream fin = null;

		String content = "";

		try {

			fin = new FileInputStream(file);

			content = readFile(fin);

		} catch (final FileNotFoundException e) {
			LOGGER.info("File not found" + e);
		}
		return content;
	}

	/**
	 * @param ins
	 * @return String
	 */
	public static String readFile(final InputStream ins) {
		int ch;
		final StringBuffer sb = new StringBuffer();
		String results = null;
		try {
			while ((ch = ins.read()) != -1) {
				sb.append((char) ch);
			}
			results = new String(sb.toString().getBytes("ISO-8859-1"), "utf-8");
		} catch (final IOException e) {
			LOGGER.info("Exception while reading file " + e);
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
			} catch (final IOException ioe) {
				LOGGER.info("Error while closing stream: " + ioe);
			}
		}
		return results;
	}

	/**
	 * Read all the files in a folder
	 *
	 * @param filepath
	 * @param character
	 *            Fuzzy query based on the file name
	 * @return key=filename<br/>
	 *         value=filecontent
	 */
	public static Map<String, String> readFileContentOfTheFloder(final String filepath, final String character) {

		final File file = new File(filepath);
		final Map<String, String> result = new HashMap<String, String>();
		if (file.isDirectory()) {

			final String[] filelist = file.list();

			for (int i = 0; i < filelist.length; i++) {

				final File readfile = new File(filepath + File.separator + filelist[i]);
				if (!readfile.isDirectory()) {

					final String tempFilePath = readfile.getAbsolutePath();
					final String tempfileName = readfile.getName();

					if (StringUtils.isNotEmpty(character)) {
						if (tempfileName.contains(character)) {
							result.put(tempfileName, readFile(tempFilePath));
						}
					} else {
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
	public static void writeFile(final String filePath, final String content) {
		writeFile(filePath, null, content);
	}

	/**
	 * @param filePath
	 * @param suffix
	 * @param content
	 */
	public static void writeFile(final String filePath, String suffix, final String content) {

		suffix = StringUtils.isEmpty(suffix) ? "json" : suffix;

		final File file = new File(filePath + "." + suffix);
		FileOutputStream out = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			out = new FileOutputStream(file, true);
			out.write(content.getBytes("utf-8"));
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		} catch (final FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (final IOException e) {
			LOGGER.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (final IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
	}
}
