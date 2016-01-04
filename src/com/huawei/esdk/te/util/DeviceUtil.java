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

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipboardManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import com.huawei.esdk.te.TESDK;
import com.huawei.utils.StringUtil;

/**
 * 类名称：DeviceUtil.java 类描述：设备工具类
 * 
 */
public final class DeviceUtil
{

	private static final String TAG = DeviceUtil.class.getSimpleName();

	/**
	 * bitmap参数对象
	 */

	/**
	 * 电源管理 屏幕锁
	 */
	private static PowerManager.WakeLock wakeLock = null;

	/**
	 * 点亮的时候使用
	 */
	private static PowerManager.WakeLock wakeLockLight = null;

	private static SensorManager sm = null;

	private static SensorEventListener sensorListener = null;

	private static Sensor proximitySensor = null;

	/**
	 * 点亮长时间 25s
	 */
	public static final int LIGHT_TIME_LONG = 25000;

	/**
	 * 点亮中时间10s
	 */
	public static final int LIGHT_TIME_MIDDLE = 10000;

	/**
	 * 点亮短时间5s
	 */
	public static final int LIGHT_TIME_SHORT = 5000;

	/**
	 * 点亮最短时间1s
	 */
	public static final int LIGHT_TIME_MIN = 1000;

	private DeviceUtil()
	{
		// 私有构造
	}

	/**
	 * 方法名称：getIMEI 作者：modify by yzf 方法描述：得到设备ID号(IMEI) 输入参数：@param context 界面
	 * 输入参数：@return 设备ID号 返回类型： 备注：
	 */
	public static String getIMEI(Context context)
	{
		String strIMEI = null;
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		strIMEI = telephonyManager.getDeviceId();
		if (strIMEI == null)
		{
			strIMEI = "";
		}
		return strIMEI;
	}

	/**
	 * Function: 获取MacAddress
	 * 
	 * @param context
	 * @return String
	 */
	public static String getMacAddress(Context context)
	{
		String mac = null;
		WifiManager wManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wManager != null && wManager.getConnectionInfo() != null)
		{
			mac = wManager.getConnectionInfo().getMacAddress();
		}
		return mac;
	}

	/**
	 * 方法名称：isTonpApp 作者：wangjian 方法描述：判断应用程序是否是最上层的应用 输入参数：@param context 上下文环境
	 * 输入参数：@return boolean 返回类型：boolean： 备注：
	 */
	public static boolean isTopApp(Context context)
	{
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskList = am.getRunningTasks(2);
		if (taskList == null || taskList.isEmpty())
		{
			return false;
		}
		RunningTaskInfo rti = taskList.get(0);
		String tmp = rti.topActivity.getPackageName();
		return tmp.equals(context.getPackageName());
	}

	/**
	 * 方法名称：copy 作者：liming 方法描述： 输入参数:@param context 输入参数:@param selected
	 * 返回类型：void 备注：
	 */
	public static void copy(Context context, String selected)
	{
		ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText(selected);
	}

	/**
	 * 方法名称：paste 作者：liming 方法描述： 输入参数:@param context 输入参数:@return 返回类型：String
	 * 备注：
	 */
	public static String paste(Context context)
	{
		ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		if (null != cm)
		{
			if (cm.hasText() && null != cm.getText())
			{
				return cm.getText().toString();
			}
		}
		return "";
	}

	/**
	 * 方法名称：setKeepScreenOn 作者：Liu Weihuai 方法描述：设置屏幕常亮 输入参数：@param context 上下文环境
	 * 返回类型：void： 备注：
	 */

	public static synchronized void setKeepScreenOn(Context context)
	{
		releaseWakeLock();

		if (wakeLock == null)
		{
			LogUtil.i(TAG, "setKeepScreenOn() context = " + context);
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
			wakeLock.acquire();
		}
	}

	/**
	 * 点亮屏幕，不常亮 screenOnTime释放资源
	 * 
	 * @param context
	 * @param screenOnTime
	 *            点亮屏幕的时间
	 */
	public static synchronized void setLightScreenOn(Context context, int screenOnTime)
	{
		if (0 >= screenOnTime)
		{
			return;
		}
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

		// begin modify by l00211010 reason:判断对象错误 2014.1.23 DTS2014012208457
		if (null != wakeLockLight)
		{
			releaseLightWakeLock();
		}
		// begin modify by l00211010 reason:判断对象错误 2014.1.23 DTS2014012208457
		wakeLockLight = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "WindowLock");
		wakeLockLight.acquire(screenOnTime);
	}

	/**
	 * 释放点亮屏幕的资源锁
	 */
	public static synchronized void releaseLightWakeLock()
	{
		if (null != wakeLockLight)
		{
			// 设置是需要计算锁的数量，设置为false时，在release（）的时候，不管你acquire()了多少回，可以releaseWakeLock掉
			wakeLockLight.setReferenceCounted(false);
			if (wakeLockLight.isHeld())
			{
				wakeLockLight.release();
			}
			wakeLockLight = null;
		}
	}

	/**
	 * 方法名称：releaseWakeLock 作者：Liu Weihuai 方法描述：恢复屏幕锁 输入参数： 返回类型：void： 备注：
	 */

	public static synchronized void releaseWakeLock()
	{
		LogUtil.i(TAG, "releaseWakeLock " + wakeLock);
		if (null != wakeLock)
		{
			// 设置是需要计算锁的数量，设置为false时，在release（）的时候，不管你acquire()了多少回，可以releaseWakeLock掉
			wakeLock.setReferenceCounted(false);
			if (wakeLock.isHeld())
			{
				wakeLock.release();
			}
			wakeLock = null;
		}
	}

	/**
	 * 
	 * 方法名称：releaseWakeLock 作者：liming 方法描述：释放屏幕感应锁 输入参数：@param context
	 * 返回类型：void： 备注：
	 */
	public static synchronized void releaseWakeLock(Activity context)
	{
		if (null != sm && null != sensorListener)
		{
			sm.unregisterListener(sensorListener);
			sm = null;
			sensorListener = null;
		}
		proximitySensor = null;
		setScreenBacklight(context); // 设置背光/*, 1.0f*/
		releaseWakeLock();
	}

	/**
	 * 
	 * 方法名称：setScreenStateSensor 作者：liming 方法描述：当屏幕支持感应器的时候，对光线感应进行处理
	 * 输入参数：@param context 返回类型：void： 备注：
	 */
	public static synchronized void setScreenStateSensor(final Activity context)
	{
		if (null == sm)
		{
			sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		}

		if (null == sensorListener)
		{
			sensorListener = new SensorEventListener()
			{

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy)
				{

				}

				@Override
				public void onSensorChanged(SensorEvent event)
				{
					/*
					 * float distance = event.values[0]; //获取距离 boolean active =
					 * (distance >= 0.0 && distance < 5.0f && distance <
					 * event.sensor .getMaximumRange());
					 */
					setScreenBacklight(context/* , (active ? 0.0f : 1.0f) */); // 设置背光
				}
			};
		}
		if (null == proximitySensor)
		{
			proximitySensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		}
		sm.registerListener(sensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	private static synchronized void setScreenBacklight(Activity activity/*
																		 * ,
																		 * float
																		 * a
																		 */)
	{
		if (!isTopApp(activity))
		{
			return;
		}
	}

	/**
	 * 判断是否是WIFI登录
	 * 
	 * @l00186254
	 * @return true是WIFI登陆，false不是WIFI登陆
	 */
	public static boolean isWifiConnect()
	{
		if (TESDK.getInstance().getApplication() == null)
		{
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) (TESDK.getInstance().getApplication().getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (null != wifi && wifi.isConnected())
		{
			return true;
		}
		return false;
	}

	/**
	 * 判断Service是否处于运行状态
	 * 
	 * @l00186254
	 * @param mContext
	 *            上下文环境
	 * @param className
	 *            类名
	 * @return boolean
	 */
	public static boolean isServiceRunning(Context mContext, String className)
	{
		boolean isRunning = false;
		if (mContext == null || StringUtil.isStringEmpty(className))
		{
			return isRunning;
		}
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(50);
		if (null == serviceList || serviceList.isEmpty())
		{
			return false;
		}
		int size = serviceList.size();
		for (int i = 0; i < size; i++)
		{
			if (serviceList.get(i) != null && serviceList.get(i).service.getClassName().equals(className))
			{
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 判断设备是否插入SIM卡
	 * 
	 * @return boolean
	 */
	public static boolean isSimCardOK()
	{
		// 获取SIM卡状态
		TelephonyManager tm = (TelephonyManager) TESDK.getInstance().getApplication().getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT || tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN)
		{
			// 无SIM卡或位置状态
			return false;
		}

		return true;
	}

	/**
	 * 获取sim卡是否在通话中
	 * 
	 * @return true:通话中或者振铃中， false：空闲中
	 */
	// public static boolean isInSIMCall() {
	// Context context = ActivityStackManager.INSTANCE.getCurrentActivity();
	// TelephonyManager telephonyManager = (TelephonyManager)
	// context.getSystemService(Context.TELEPHONY_SERVICE);
	// int callStatus = telephonyManager.getCallState();
	//
	// return callStatus == TelephonyManager.CALL_STATE_IDLE ? false : true;
	// }

	/**
	 * 获取sd卡路径
	 * 
	 * @return sd卡路径，无则返回null
	 */
	public static String getSdcardPath()
	{
		File sDFile = android.os.Environment.getExternalStorageDirectory();

		if (sDFile == null)
		{
			return null;
		}

		return sDFile.getPath();
	}

}
