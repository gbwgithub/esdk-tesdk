/*
 *    Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.
 *    eSDK is licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.esdk.te.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.huawei.esdk.log4Android.Log4Android;
import com.huawei.esdk.te.TESDK;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 带日志文件输入的，又可控开关的日志调试
 */
@SuppressLint("NewApi")
public class LogUtil
{
	private static final String TAG = LogUtil.class.getSimpleName();

	private static String inInterface = ""; // 用于记录in()函数进入的接口名
	private static String inTime = ""; // 用于记录in()函数进入的时间

	public final static String product = "TE-API-Android";
	private final static String format = "yyyy-MM-dd HH:mm:ss SSS";

	// 日志文件总开关
	private static Boolean MYLOG_SWITCH = false;
	// 日志写入文件开关
	private static Boolean MYLOG_WRITE_TO_FILE = true;
	// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	private static char MYLOG_TYPE = 'v';
	// 日志文件在的路径,默认在sdcard中
	private static String MYLOG_PATH_DIR = "/sdcard/TEMobile/log";
	// private static String MYLOG_PATH_DIR = getFilesDir().getPath() + "/log/";
	private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数
	private static String MYLOGFILEName = "eSDKLog.txt";// 本类输出的日志文件名称
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式

	public static void setLogSwitch(boolean logSwitch)
	{
		Log.d("LogUtil", "setLogSwitch() -> " + logSwitch);
		MYLOG_SWITCH = logSwitch;
		if (null != TESDK.getInstance() && null != TESDK.getInstance().getLogPath() && 0 != TESDK.getInstance().getLogPath().length())
		{
			MYLOG_PATH_DIR = TESDK.getInstance().getLogPath();
			if (MYLOG_PATH_DIR.charAt((MYLOG_PATH_DIR.length() - 1)) == '/')
			{
				MYLOG_PATH_DIR = MYLOG_PATH_DIR.substring(0, MYLOG_PATH_DIR.length() - 1);
			}
		}
	}

	public static boolean getLogSwitch()
	{
		Log.d("LogUtil", "getLogSwitch() -> " + MYLOG_SWITCH);
		return MYLOG_SWITCH;
	}

	public static void in()
	{
		inInterface = new Throwable().getStackTrace()[1].getMethodName();
		inTime = String.format("[%s]", new SimpleDateFormat(format).format(new Date()));
		// Log.d(TAG, "in function -> " + new
		// Throwable().getStackTrace()[1].getMethodName());
		log(TAG, "in function -> " + new Throwable().getStackTrace()[1].getMethodName(), 'v');
	}

	public static void out(String resultCode, String params)
	{
		String outInterface = new Throwable().getStackTrace()[1].getMethodName();
		if (inInterface.equals(outInterface) && !outInterface.equals(TAG))
		{
			String respTime = String.format("[%s]", new SimpleDateFormat(format).format(new Date()));
			log4Android("", outInterface, "", "", "", inTime, respTime, resultCode, params);
			return;
		}
		log(TAG, "out function -> " + new Throwable().getStackTrace()[1].getMethodName(), 'v');
		LogUtil.e(TAG, "outInterface dosn't mathc inInterface");
	}

	public static void log4Android(String protocolType, String interfaceName, String sourceAddr, String targetAddr, String transactionID, String reqTime,
			String respTime, String resultCode, String params)
	{
		if (null != protocolType && "".equals(protocolType))
		{
			protocolType = "Native";
		}

		if (null != reqTime && "".equals(reqTime))
		{
			reqTime = String.format("[%s]", new SimpleDateFormat(format).format(new Date()));
		}

		Log4Android.getInstance().logInterfaceInfo(product, "1", protocolType, interfaceName, sourceAddr, targetAddr, transactionID, reqTime, respTime,
				resultCode, params);
	}

	public static void w(String tag, Object msg)
	{ // 警告信息
		log(tag, msg.toString(), 'w');
	}

	public static void e(String tag, Object msg)
	{ // 错误信息
		log(tag, msg.toString(), 'e');
	}

	public static void d(String tag, Object msg)
	{// 调试信息
		log(tag, msg.toString(), 'd');
	}

	public static void i(String tag, Object msg)
	{
		log(tag, msg.toString(), 'i');
	}

	public static void v(String tag, Object msg)
	{
		log(tag, msg.toString(), 'v');
	}

	public static void w(String tag, String text)
	{
		log(tag, text, 'w');
	}

	public static void e(String tag, String text)
	{
		log(tag, text, 'e');
	}

	public static void d(String tag, String text)
	{
		log(tag, text, 'd');
	}

	public static void i(String tag, String text)
	{
		log(tag, text, 'i');
	}

	public static void v(String tag, String text)
	{
		log(tag, text, 'v');
	}

	/**
	 * 根据tag, msg和等级，输出日志
	 */
	private static void log(String tag, String msg, char level)
	{
		if (MYLOG_SWITCH)
		{
			if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE))
			{ // 输出错误信息
				Log.e(tag, msg);
			} else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE))
			{
				Log.w(tag, msg);
			} else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE))
			{
				Log.d(tag, msg);
			} else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE))
			{
				Log.i(tag, msg);
			} else
			{
				Log.v(tag, msg);
			}
			if (MYLOG_WRITE_TO_FILE)
			{
				// 写入文件目前有BUG，待修复
				writeLogtoFile(String.valueOf(level), tag, msg);
			}
		}
	}

	/**
	 * 打开日志文件并写入日志
	 **/
	private static void writeLogtoFile(String mylogtype, String tag, String text)
	{
		// 新建或打开日志文件
		Date nowtime = new Date();
		String needWriteFiel = logfile.format(nowtime);
		String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype + "    " + tag + "    " + text;
		// Log.d("LogUtil - for test", "writeLogtoFile path -> " +
		// MYLOG_PATH_DIR + "/" + needWriteFiel + MYLOGFILEName);
		File file = new File(MYLOG_PATH_DIR, needWriteFiel + MYLOGFILEName);
		try
		{
			file.setWritable(Boolean.TRUE);
			FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 删除制定的日志文件
	 */
	public static void delFile()
	{// 删除日志文件
		String needDelFiel = logfile.format(getDateBefore());
		File file = new File(MYLOG_PATH_DIR, needDelFiel + MYLOGFILEName);
		if (file.exists())
		{
			file.delete();
		}
	}

	/**
	 * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
	 */
	private static Date getDateBefore()
	{
		Date nowtime = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowtime);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
		return now.getTime();
	}

}
