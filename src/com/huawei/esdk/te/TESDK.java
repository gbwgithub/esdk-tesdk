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

package com.huawei.esdk.te;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.huawei.application.BaseApp;
import com.huawei.common.CustomBroadcastConst;
import com.huawei.common.LogSDK;
import com.huawei.common.Resource;
import com.huawei.ecs.mtk.log.AndroidLogger;
import com.huawei.ecs.mtk.log.LogLevel;
import com.huawei.ecs.mtk.log.Logger;
import com.huawei.esdk.log4Android.Log4Android;
import com.huawei.esdk.te.call.CallConstants.CallStatus;
import com.huawei.esdk.te.call.CallLogic;
import com.huawei.esdk.te.data.Constants;
import com.huawei.esdk.te.util.LayoutUtil;
import com.huawei.esdk.te.util.LogUtil;
import com.huawei.esdk.te.util.OrieantationUtil;
import com.huawei.esdk.te.video.LocalHideRenderServer;
import com.huawei.manager.DataManager;
import com.huawei.module.SDKConfigParam;
import com.huawei.service.ServiceProxy;
import com.huawei.service.eSpaceService;
import com.huawei.utils.DeviceManager;
import com.huawei.utils.StringUtil;
import com.huawei.voip.CallManager.State;
import com.huawei.voip.data.LoginInfo;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TESDK
{
	private static final String TAG = TESDK.class.getSimpleName();
	/**
	 * 用于限定SDK的广播接受者域 . 目前使用"com.huawei.TEMobile" 注意 ： 此处配置字符串 要和
	 * manifest文件中IM消息注册消息通知的Activity保持一致。
	 */
	private static String SDK_BROADCAST_PERMISSION = "com.huawei.TEMobile";

	private static String SDK_LOG_DIR = "/log/";

	private static TESDK instance;

	private Application application;

	private String logPath = "";

	private boolean debugSwitch = true;

	private boolean mServiceStarted;
	private ServiceProxy mService = null;

	private final List<Message> messageQueue = new ArrayList<Message>();

	private static final byte[] SERVICE_LOCK = new byte[0];
	private final Object synLock = new Object();
	private boolean isRegisterScreenReceiver = false;

	public static void initSDK(Application app)
	{
		LogUtil.in();
		BaseApp.setApp(app);
		instance = new TESDK(app);
		System.loadLibrary("Log4Android");
		Log4Android.setContext(app);
		LogUtil.d(TAG, "Log4Android isWIFIConnect -> " + Log4Android.isWIFIConnect());

		InputStream is = app.getClass().getResourceAsStream("/com/huawei/esdk/log4Android/eSDKClientLogCfg.ini");
		String fileContents = new String(Log4Android.InputStreamToByte(is));

		LogUtil.d(TAG, "Log4Android fileContents -> " + fileContents);
		// 日志初始化
		int[] logLevel = { 0, 0, 3 };
		LogUtil.d(TAG, "logInit result -> " + Log4Android.getInstance().logInit(LogUtil.product, fileContents, logLevel, "/sdcard/TEMobile/log"));
		Log4Android.getInstance().setCallBackMethod();
		Log4Android.getInstance().setSendLogStrategy(0, 2, "172.22.9.38:9086");
		Log4Android.getInstance().initMobileLog(LogUtil.product);

		// LogUtil.log4Android("", TAG + "." + "initSDK", "", "", "", "", "",
		// "", app.toString());
		LogUtil.out("", "app -> " + app.toString());

		// LogUtil.d(TAG, "new Throwable().getStackTrace().length -> " + new
		// Throwable().getStackTrace().length);
		// new Throwable().printStackTrace();
		//
		// String clazzName0 = new
		// Throwable().getStackTrace()[0].getClassName();
		// LogUtil.d(TAG, "new Throwable().getStackTrace()[0].getClassName() -> " +
		// clazzName0);
		//
		// String funcName0 = new
		// Throwable().getStackTrace()[0].getMethodName();
		// LogUtil.d(TAG, "new Throwable().getStackTrace()[0].getMethodName() -> " +
		// funcName0);
		//
		// String clazzName1 = new
		// Throwable().getStackTrace()[1].getClassName();
		// LogUtil.d(TAG, "new Throwable().getStackTrace()[1].getClassName() -> " +
		// clazzName1);
		//
		// String funcName1 = new
		// Throwable().getStackTrace()[1].getMethodName();
		// LogUtil.d(TAG, "new Throwable().getStackTrace()[1].getMethodName() -> " +
		// funcName1);
		//
		// String clazzName2 = new
		// Throwable().getStackTrace()[2].getClassName();
		// LogUtil.d(TAG, "new Throwable().getStackTrace()[2].getClassName() -> " +
		// clazzName2);
		//
		// String funcName2 = new
		// Throwable().getStackTrace()[2].getMethodName();
		// LogUtil.d(TAG, "new Throwable().getStackTrace()[2].getMethodName() -> " +
		// funcName2);
		//
		// String clazzName00 =
		// Thread.currentThread().getStackTrace()[0].getClassName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[0].getMethodName() -> " +
		// clazzName00);
		//
		// String funcName00 =
		// Thread.currentThread().getStackTrace()[0].getMethodName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[0].getMethodName() -> " +
		// funcName00);
		//
		// String clazzName01 =
		// Thread.currentThread().getStackTrace()[1].getClassName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[1].getMethodName() -> " +
		// clazzName01);
		//
		// String funcName01 =
		// Thread.currentThread().getStackTrace()[1].getMethodName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[1].getMethodName() -> " +
		// funcName01);
		//
		// String clazzName02 =
		// Thread.currentThread().getStackTrace()[2].getClassName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[2].getMethodName() -> " +
		// clazzName02);
		//
		// String funcName02 =
		// Thread.currentThread().getStackTrace()[2].getMethodName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[2].getMethodName() -> " +
		// funcName02);
		//
		// String clazzName03 =
		// Thread.currentThread().getStackTrace()[3].getClassName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[3].getMethodName() -> " +
		// clazzName03);
		//
		// String funcName03 =
		// Thread.currentThread().getStackTrace()[3].getMethodName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[3].getMethodName() -> " +
		// funcName03);
		//
		// String clazzName04 =
		// Thread.currentThread().getStackTrace()[4].getClassName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[4].getMethodName() -> " +
		// clazzName04);
		//
		// String funcName04 =
		// Thread.currentThread().getStackTrace()[4].getMethodName();
		// LogUtil.d(TAG,
		// "Thread.currentThread().getStackTrace()[4].getMethodName() -> " +
		// funcName04);

		// String reqTime = String.format("[%s]", new
		// SimpleDateFormat(format).format(new Date()));
		// Log4Android.getInstance().logInterfaceInfo(product, "1", "",
		// "TESDK.initSDK", "", "", "", reqTime, "", "", "");

		// Log4Android.getInstance().logInterfaceError(product, "2", "HTTP+XML",
		// "TESTERROR", "", "", "", "2015-12-10", "2015-12-10", "aa", "bb");

		// // 成功
		// Log4Android.getInstance().logInterfaceInfo(product, "2", "HTTP+XML",
		// "Authentic.login", "", "", "", reqTime, RespTime, "" + loginFlag,
		// "userId:" + userId + ",userPwd:" + userPwd + ",userType:" +
		// userType);
		// // 失败
		// Log4Android.getInstance().logInterfaceError(product, "2", "HTTP+XML",
		// "Authentic.login", "", "", "", reqTime, RespTime, "" + loginFlag,
		// "userId:" + userId + ",userPwd:" + userPwd + ",userType:" +
		// userType);
		//
		// // 运行接口
		// Log4Android.getInstance().logRunDebug(product, "login excuted");
	}

	private TESDK(Application app)
	{
		LogUtil.d(TAG, "TESDK construct");
		application = app;
		debugSwitch = false;
		logPath = application.getFilesDir().getPath() + SDK_LOG_DIR;
		LogUtil.d(TAG, "init logPath -> " + logPath);

		boolean isPhone = false;
		OrieantationUtil.getIns().setDeviceReferenceAngle();
		int orientation = OrieantationUtil.getIns().getDevicereferenceangle();
		// 手机默认摄像头基准角度是 270，pad是 0 。android5.0 nexus 6基准角度为90
		// if ((0 == orientation || 180 == orientation) &&
		// LayoutUtil.getInstance().isPadScreen())
		if (LayoutUtil.getInstance().isPadScreen())
		{
			isPhone = false;
		} else
		{
			isPhone = true;
		}

		LogUtil.d(TAG, "isPhone -> " + isPhone);
		LayoutUtil.setIsPhone(isPhone);

		// 注册登录广播
		registeBroadcast(app);

		// LayoutUtil.setCustomizeVersions(application.getResources().getString(R.string.customize_versions));
		// LayoutUtil.setLoadPortLayout(getResources().getBoolean(R.bool.mobile_screen));

		// File logFileDir = new File(logPath);
		// if (!logFileDir.exists())
		// {
		// logFileDir.mkdir();
		// }
	}

	public static TESDK getInstance()
	{
		if (null == instance)
		{
			Log.e(TAG, "TESDK didn't init");
		}
		return instance;
	}

	public Application getApplication()
	{
		return application;
	}

	/**
	 * 设置记录Log文件开关及路径
	 * 
	 * @param debugSwitch
	 *            是否记录Log文件
	 * @param path
	 *            Log文件路径
	 */
	public void setLogPath(boolean debugSwitch, String path)
	{
		LogUtil.in();
		// 设置Log开关与路径
		this.debugSwitch = debugSwitch;
		this.logPath = path;
		if (null != path && !("".equals(path)))
		{
			this.logPath = path;
		}
		// 执行设置Log开关
		logSwitch();
		LogUtil.out("", "debugSwitch -> " + debugSwitch + "  path" + path);
	}

	public String getLogPath()
	{
		return this.logPath;
	}

	/**
	 * Function: 写日志总开关
	 * 
	 * @return boolean true/ false
	 */
	private boolean logSwitch()
	{
		setFastLog(debugSwitch); // FastLog是底层的Log
		saveLogcat(debugSwitch, logPath); // 保存LogCat日志
		LogUtil.setLogSwitch(debugSwitch);// SDK控制台Log开关
		return true;
	}

	/**
	 * 设置FastLog 开关
	 */
	private void setFastLog(boolean logSwitch)
	{
		LogUtil.d(TAG, "setFastLog");
		if (mService != null)
		{
			LogUtil.d(TAG, "setFastLog:" + logPath);
			mService.setLogSwitch(logPath, logSwitch);
			return;
		}
		Log.w(TAG, "setFastLog Failed -> service is null.");
	}

	/**
	 * Logcat日志写入文件开关
	 * 
	 * @param debugSwitch
	 *            是否写MAA日志
	 * @param logPath
	 *            日志路径 （打开，和关闭的路径应该一致）
	 */
	private void saveLogcat(boolean debugSwitch, String logPath)
	{
		if (StringUtil.isStringEmpty(logPath))
		{
			return;
		}
		File file = new File(logPath);
		if (!file.isDirectory())
		{
			try
			{
				if (!file.mkdirs())
				{
					return;
				}
			} catch (SecurityException e)
			{
				LogUtil.e(TAG, "logcat ecs error.");
				return;
			}
		}
		if (Logger.getLogger() == null)
		{
			Logger.setLogger(new AndroidLogger());
			Logger.setMaxLogFileSize(10485760); // 设置日志文件大小为10M
		}
		if (debugSwitch)
		{
			Logger.setLogFile(logPath + "ECS.txt");
			Logger.setLogLevel(LogLevel.DEBUG);
			LogUtil.i(TAG, "set Logcat ECS DEBUG>>>>>>>>>>>>>>>");
		} else
		{
			Logger.setLogFile(logPath + "ECS.txt");
			Logger.setLogLevel(LogLevel.INFO);
			LogUtil.i(TAG, "set Logcat ECS ERROR>>>>>>>>>>>>>>>");
		}
	}

	/**
	 * 方法名称：callWhenServiceConnected 方法描述：绑定服务后回调
	 * 
	 * @param target
	 *            输入参数
	 * @param callback
	 *            输入参数
	 * @param isAutoLogin
	 *            输入参数 返回类型：void
	 */
	private void callWhenServiceConnected(Handler target, Runnable callback, boolean isAutoLogin)
	{
		Message msg = Message.obtain(target, callback);
		if (serviceConnected())
		{
			msg.sendToTarget();
		} else
		{
			synchronized (synLock)
			{
				startImServiceIfNeed(isAutoLogin);
			}
			synchronized (messageQueue)
			{
				messageQueue.add(msg);
			}
		}
	}

	/**
	 * 得到同步锁,用于对外提供登录同步锁
	 */
	public Object getSynLock()
	{
		return synLock;
	}

	/**
	 * Function: 判断当前的Service 是否已经绑定上
	 * 
	 * @return boolean
	 */
	private boolean serviceConnected()
	{
		return mService != null;
	}

	public ServiceProxy getmService()
	{
		return mService;
	}

	private void startImServiceIfNeed(boolean autoLogin)
	{
		synchronized (SERVICE_LOCK)
		{
			LogUtil.i(TAG, "startImServiceIfNeed enter.");
			if (!mServiceStarted)
			{
				LogUtil.i(TAG, ">>>>   start  eSpaceService" + " autoLogin = " + autoLogin);
				Intent intent = new Intent(application, eSpaceService.class);
				intent.putExtra(Resource.EXTRA_CHECK_AUTO_LOGIN, autoLogin);
				application.startService(intent);
				application.bindService(intent, mImServiceConn, Context.BIND_AUTO_CREATE);
				mServiceStarted = true;
			} else
			{
				LogUtil.i(TAG, "mServiceStarted == true");
			}
			LogUtil.i(TAG, "startImServiceIfNeed leave.");
		}
	}

	/**
	 * Function: 配置SDK 参数
	 */
	private SDKConfigParam getConfigSDKParam()
	{
		SDKConfigParam param = new SDKConfigParam();
		param.setClientType(SDKConfigParam.ClientType.UC_HD);
		param.setBooadcastPermission(SDK_BROADCAST_PERMISSION);
		param.setHttpLogPath("");
		param.setMegTypeVersion(SDKConfigParam.TCP_V3);
		param.addAbility(SDKConfigParam.Ability.TEMPGROUP); // 支持临时群能力
		param.addAbility(SDKConfigParam.Ability.FIXEDGROUP);// 支持固定群
		param.addAbility(SDKConfigParam.Ability.VOIP_2833); // 支持 2833 编码能力
		param.addAbility(SDKConfigParam.Ability.CODE_OPOUS);// 支持OPOUS高清语音
		// 支持视频能力
		param.addAbility(SDKConfigParam.Ability.VOIP_VIDEO);
		return param;
	}

	/**
	 * 停止 SDK Service
	 */
	private void stopSDKService()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "stopSDKService", "", "", "", "",
		// "", "", "");
		synchronized (SERVICE_LOCK)
		{
			LogUtil.i(TAG, "stopImServiceIfInactive enter.");

			if (mServiceStarted)
			{
				if (mService != null)
				{
					LogUtil.i(TAG, "   >>>>   stop  eSpaceService");
					mService.stopService();
					application.unbindService(mImServiceConn);
					mService = null;
				} else
				{
					LogUtil.w(TAG, " stop  eSpaceService   mService  ==  null ");
				}
				mServiceStarted = false;
			} else
			{
				LogUtil.w(TAG, " mServiceStarted  == false ");
			}

			LogUtil.i(TAG, "stopImServiceIfInactive leave.");
		}
		LogUtil.out("", "");
	}

	public boolean login(final LoginParameter loginParameter)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "login", "", "", "", "", "", "",
		// loginParameter.toString());
		LogUtil.i(TAG, "login()");
		LogUtil.i(TAG, "loginParameter -> CallBandWidth:" + loginParameter.getCallBandWidth());
		LogUtil.i(TAG, "loginParameter -> CT :" + loginParameter.getCT());
		LogUtil.i(TAG, "loginParameter -> EncryptMode :" + loginParameter.getEncryptMode());
		LogUtil.i(TAG, "loginParameter -> IsILBCPri :" + loginParameter.getIsILBCPri());
		LogUtil.i(TAG, "loginParameter -> LicenseServer :" + loginParameter.getLicenseServer());
		LogUtil.i(TAG, "loginParameter -> LoginName :" + loginParameter.getLoginName());
		LogUtil.i(TAG, "loginParameter -> LoginPwd :" + loginParameter.getLoginPwd());
		LogUtil.i(TAG, "loginParameter -> MediaPort :" + loginParameter.getMediaPort());
		LogUtil.i(TAG, "loginParameter -> ProtocolType :" + loginParameter.getProtocolType());
		LogUtil.i(TAG, "loginParameter -> ServerIP :" + loginParameter.getServerIP());
		LogUtil.i(TAG, "loginParameter -> ServerPort :" + loginParameter.getServerPort());
		LogUtil.i(TAG, "loginParameter -> Sipuri :" + loginParameter.getSipuri());
		LogUtil.i(TAG, "loginParameter -> VideoMode :" + loginParameter.getVideoMode());
		LogUtil.i(TAG, "loginParameter -> AutoLogin :" + loginParameter.isAutoLogin());
		LogUtil.i(TAG, "loginParameter -> BfcpEnable :" + loginParameter.isBfcpEnable());
		// 注册锁屏广播，用以解决视频通话中锁屏的问题
		instance.registerScreenActionReceiver(application);

		final LoginInfo loginInfo = loginParameter.getLoginInfo();
		boolean isAnonymous = false;
		loginInfo.setAnonymousLogin(isAnonymous);
		loginInfo.setAutoLogin(false);
		loginInfo.setBfcpEnable(true);

		String username = loginInfo.getLoginName();
		// 初始化Datamanager
		DataManager.getIns().init(application, isAnonymous ? Constants.ANONYMOUS_ACCOUNT : username);

		// 在延迟登录线程中执行时，需要判断loop是否存在
		if (null == Looper.myLooper())
		{
			Looper.prepare();
		}

		// 如果网络问题无法登录做提示
		if (!DeviceManager.isNetworkAvailable(application))
		{
			LogUtil.e(TAG, "网络已断开");
			LogUtil.out("false", "loginParameter -> " + loginParameter);
			return false;
		}

		Constants.setAnonymousAccount(isAnonymous);
		LogSDK.setUser(isAnonymous ? Constants.ANONYMOUS_ACCOUNT : username);

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				callWhenServiceConnected(new Handler(application.getMainLooper()), new Runnable()
				{
					@Override
					public void run()
					{
						LogUtil.d(TAG, "ServiceConnected call connectToServer start.");
						connectToServer(loginInfo);
						LogUtil.d(TAG, "ServiceConnected call connectToServer end.");
					}
				}, false);
			}
		}).run();
		LogUtil.out("true", "loginParameter -> " + loginParameter);
		return true;
	}

	private ServiceConnection mImServiceConn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			synchronized (SERVICE_LOCK)
			{// 快速重复点击登陆取消后，出现返回到登陆界面，但实际是注册上的状态
				LogUtil.i(TAG, "onServiceConnected enter.");

				if (mServiceStarted)
				{
					LogUtil.i(TAG, "onServiceConnected () enter Thread:" + Thread.currentThread().getId());
					mService = ((eSpaceService.ServiceBinder) service).getService();
					mService.setSDKConfigparam(getConfigSDKParam());
					// 开启TUP日志开关
					setFastLog(true);
					new CallLogic(mService);
					synchronized (messageQueue)
					{
						Message msg = null;
						int size = messageQueue.size();
						for (int i = 0; i < size; i++)
						{
							msg = messageQueue.get(i);
							if (msg != null)
							{
								msg.sendToTarget();
							}
						}
						messageQueue.clear();
					}
				}
				// 先start，接着stop，最后才收到onServiceConnected，此时要强制stop掉
				else
				{
					LogUtil.w(TAG, "onServiceConnected service is not start, force stop Service.");
					mServiceStarted = true;
					mService = ((eSpaceService.ServiceBinder) service).getService();
					mService.setSDKConfigparam(getConfigSDKParam());
					stopSDKService();
				}

				LogUtil.i(TAG, "onServiceConnected leave.");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className)
		{
			synchronized (SERVICE_LOCK)
			{
				LogUtil.w(TAG, "onServiceDisconnected() enter Thread:" + Thread.currentThread().getId());
				mService = null;
			}
		}
	};

	/**
	 * 连接服务器 isAnonymousLogin 是否为匿名登录
	 */
	private void connectToServer(LoginInfo loginInfo)
	{
		if (mService == null)
		{
			LogUtil.w(TAG, "connect to Server error  serviceProxy is null ");
			LogUtil.i(TAG, "connectToServer leave.");
			return;
		}

		// login返回false时上报错误状态
		System.out.println("mService.login");
		if (!mService.login(loginInfo, application))
		{
			eSpaceService.getService().onLoginResult(State.UNREGISTE, Resource.NETWORK_INVALID);
		}
		LogUtil.i(TAG, "connectToServer leave.");
	}

	/**
	 * 注销
	 */
	public void logout()
	{
		LogUtil.in();
		if (getmService() != null)
		{
			new Handler().postDelayed(logoutWaitRunnable, 2000);
			getmService().logout();
			stopSDKService();
		}
		LogUtil.out("", "");
		exit();
	}

	/**
	 * 注销的定时器
	 */
	private Runnable logoutWaitRunnable = new Runnable()
	{

		@Override
		public void run()
		{
			LogUtil.i(TAG, "logout timer begin  to logout");
			synchronized (synLock)
			{
				stopSDKService();
				instance.unRegisterScreenActionReceiver(application);
			}

			// 回到登录界面时去初始化Datamanager
			DataManager.getIns().uninit();
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent != null)
			{
				LogUtil.i(TAG, "onReceive broadcast ->" + intent);

				handlerBroadcastEvent(intent);
			}
		}
	};

	/**
	 * 异步处理 广播事件
	 */
	private void handlerBroadcastEvent(final Intent intent)
	{
		if (intent != null)
		{
			LogUtil.d(TAG, "handlerBroadcastEvent ->" + intent.getAction());
			String action = intent.getAction();

			if (CustomBroadcastConst.ACTION_CONNECT_TO_SERVER.equals(action))
			{
				LogUtil.d(TAG, "connect to server");
				onConnectToServer(intent);
			}
		}
	}

	/**
	 * 注册登录广播
	 */
	private void registeBroadcast(Application app)
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(CustomBroadcastConst.ACTION_CONNECT_TO_SERVER);
		// filter.addAction(CustomBroadcastConst.ACTION_LOGIN_RESPONSE);
		// filter.addAction(Constants.BROADCAST_PATH.ACTION_HOMEACTIVITY_SHOW);
		// filter.addAction(CustomBroadcastConst.ACTION_SVN_AUTHENTICATION_RESPONSE);
		// filter.addAction(HeartBeatConfig.ACTION_RECONNECT);
		app.registerReceiver(mReceiver, filter);
	}

	/**
	 * 连接成功回调
	 */
	private void onConnectToServer(final Intent intent)
	{
		boolean flag = intent.getBooleanExtra(Resource.SERVICE_RESPONSE_DATA, false);
		if (flag)
		{
			if (getmService() != null)
			{
			} else
			{
				stopSDKService();
			}
		} else
		{
			stopSDKService();
		}
	}

	private void registerScreenActionReceiver(Context mContext)
	{
		if (!isRegisterScreenReceiver)
		{
			isRegisterScreenReceiver = true;

			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);

			mContext.registerReceiver(screenActionReceiver, filter);
		}
	}

	private void unRegisterScreenActionReceiver(Context mContext)
	{
		if (isRegisterScreenReceiver)
		{
			isRegisterScreenReceiver = false;
			mContext.unregisterReceiver(screenActionReceiver);
		}
	}

	private class ScreenActionReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_ON))
			{
				LogUtil.d(TAG, "ACTION_SCREEN_ON");
				if (null == CallLogic.getInstance())
				{
					return;
				}
				if (CallStatus.STATUS_VIDEOINIT == CallLogic.getInstance().getVoipStatus()
						| CallStatus.STATUS_VIDEOING == CallLogic.getInstance().getVoipStatus())
				{
					LogUtil.d(TAG, "ACTION_SCREEN_ON & voidoing");
					if (null != LocalHideRenderServer.getInstance())
					{
						if (!CallLogic.getInstance().isUserCloseLocalCamera())
						{
							Executors.newSingleThreadExecutor().execute(new BackFromBackground());
						} else
						{
							LocalHideRenderServer.getInstance().setBackground(false);
						}
					}
				}
			} else if (action.equals(Intent.ACTION_SCREEN_OFF))
			{
				LogUtil.d(TAG, "ACTION_SCREEN_OFF");
				if (null == CallLogic.getInstance())
				{
					return;
				}
				if (CallStatus.STATUS_VIDEOINIT == CallLogic.getInstance().getVoipStatus()
						| CallStatus.STATUS_VIDEOING == CallLogic.getInstance().getVoipStatus())
				{
					LogUtil.d(TAG, "ACTION_SCREEN_OFF & voidoing");
					if (null != LocalHideRenderServer.getInstance())
					{
						LocalHideRenderServer.getInstance().doInBackground();
					}
				}
			}
		}
	}

	/**
	 * 后台切换内部类
	 */
	private static final class BackFromBackground implements Runnable
	{
		@Override
		public void run()
		{
			LocalHideRenderServer.getInstance().doBackFromBackground();
		}
	}

	private ScreenActionReceiver screenActionReceiver = new ScreenActionReceiver();

	/**
	 * 退出eSpace 程序 清理缓存数据和销毁UI
	 */
	private void exit()
	{
		synchronized (synLock)
		{
			// 退出时去初始化Datamanager
			DataManager.getIns().uninit();
		}
		LogUtil.i(TAG, "exit app.");
	}

	public static class LoginParameter
	{

		private LoginInfo loginInfo;

		public LoginInfo getLoginInfo()
		{
			return this.loginInfo;
		}

		public LoginParameter()
		{
			this.loginInfo = new LoginInfo();
			// 设置非匿名登录
			loginInfo.setAnonymousLogin(false);
		}

		public String getLoginName()
		{
			return loginInfo.getLoginName();
		}

		public void setLoginName(String loginName)
		{
			this.loginInfo.setLoginName(loginName);
		}

		public String getLoginPwd()
		{
			return this.loginInfo.getLoginPwd();
		}

		public void setLoginPwd(String loginPwd)
		{
			this.loginInfo.setLoginPwd(loginPwd);
		}

		public String getServerIP()
		{
			return this.loginInfo.getServerIP();
		}

		public void setServerIP(String serverIP)
		{
			this.loginInfo.setServerIP(serverIP);
		}

		public String getServerPort()
		{
			return this.loginInfo.getServerPort();
		}

		public void setServerPort(String serverPort)
		{
			this.loginInfo.setServerPort(serverPort);
		}

		public boolean isAutoLogin()
		{
			return this.loginInfo.isAutoLogin();
		}

		public void setAutoLogin(boolean isAutoLogin)
		{
			this.loginInfo.setAutoLogin(isAutoLogin);
		}

		public String getProtocolType()
		{
			return this.loginInfo.getProtocolType();
		}

		public void setProtocolType(String protocolType)
		{
			this.loginInfo.setProtocolType(protocolType);
		}

		public boolean isBfcpEnable()
		{
			return this.loginInfo.isBfcpEnable();
		}

		public void setBfcpEnable(boolean isBfcpEnable)
		{
			this.loginInfo.setBfcpEnable(isBfcpEnable);
		}

		public int getEncryptMode()
		{
			return this.loginInfo.getEncryptMode();
		}

		public void setEncryptMode(int encryptMode)
		{
			this.loginInfo.setEncryptMode(encryptMode);
		}

		public int getCallBandWidth()
		{
			return this.loginInfo.getCallBandWidth();
		}

		public void setCallBandWidth(int callBandWidth)
		{
			this.loginInfo.setCallBandWidth(callBandWidth);
		}

		public int getVideoMode()
		{
			return this.loginInfo.getVideoMode();
		}

		public void setVideoMode(int videoMode)
		{
			this.loginInfo.setVideoMode(videoMode);
		}

		public int getMediaPort()
		{
			return this.loginInfo.getMediaPort();
		}

		public void setMediaPort(int mediaPort)
		{
			this.loginInfo.setMediaPort(mediaPort);
		}

		public int getSipPort()
		{
			return this.loginInfo.getSipPort();
		}

		public void setSipPort(int sipPort)
		{
			this.loginInfo.setSipPort(sipPort);
		}

		public int getIsILBCPri()
		{
			return this.loginInfo.getIsILBCPri();
		}

		public void setIsILBCPri(int ilbcAudioPri)
		{
			this.loginInfo.setIsILBCPri(ilbcAudioPri);
		}

		public int getCT()
		{
			return this.loginInfo.getCT();
		}

		public void setCT(int ct)
		{
			this.loginInfo.setCT(ct);
		}

		public String getSipuri()
		{
			return this.loginInfo.getSipuri();
		}

		public void setSipuri(String sipuri)
		{
			this.loginInfo.setSipuri(sipuri);
		}

		public boolean isSupportSipSessionTimer()
		{
			return this.loginInfo.isSupportSipSessionTimer();
		}

		public void setSupportSipSessionTimer(boolean isSupportSipSessionTimer)
		{
			this.loginInfo.setSupportSipSessionTimer(isSupportSipSessionTimer);
		}

		public String getLicenseServer()
		{
			return this.loginInfo.getLicenseServer();
		}

		public void setLicenseServer(String licenseServer)
		{
			this.loginInfo.setLicenseServer(licenseServer);
		}
	}
}
