package com.teamcenter.soa.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringFormatUtil {

	public static final String DATE_FORMAT = "dd-MMM-yyyy HH:mm";
	public static final String DOUBLE_FORMAT = "0.00";
	public static final String INT_FORMAT = "0";
	public static final String TEXT_FORMAT = "@";
	

	public static boolean isDate(String dateString) {
		if(dateString == null)return false;
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		try {
			df.parse(dateString);
		} catch (ParseException e) {
			return false;
		}
		return true;

	}

	public static boolean isDouble(String doubleString) {
		if(doubleString == null)return false;
		try {
			Double.parseDouble(doubleString);
			return true;
		}

		catch (NumberFormatException er) {
			return false;
		}
	}

	public static boolean isInt(String intString) {
		if(intString == null)return false;
		try {
			Integer.parseInt(intString);
			return true;
		}

		catch (NumberFormatException er) {
			return false;
		}
	}

	public static int getInt(String intString) {
		try {
			return Integer.parseInt(intString);
		}

		catch (NumberFormatException er) {
			return -9999999;
		}
	}

	public static double getDouble(String doubleString) {
		try {
			return Double.parseDouble(doubleString);
		}

		catch (NumberFormatException er) {
			return -9999999.99;
		}
	}

	public static Calendar getCalendar(String dateString) {
		DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		Calendar calendar = null;
		try {
			Date date = df.parse(dateString);
			calendar =Calendar.getInstance();
			calendar.setTime(date);
			return calendar;
		} catch (ParseException e) {
			return null;
		}

	}

}
