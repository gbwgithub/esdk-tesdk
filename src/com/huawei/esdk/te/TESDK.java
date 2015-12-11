package com.huawei.esdk.te;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.huawei.application.BaseApp;
import com.huawei.common.LogSDK;
import com.huawei.common.Resource;
import com.huawei.ecs.mtk.log.AndroidLogger;
import com.huawei.ecs.mtk.log.LogLevel;
import com.huawei.ecs.mtk.log.Logger;
import com.huawei.esdk.te.call.CallLogic;
import com.huawei.esdk.te.data.Constants;
import com.huawei.esdk.te.util.LayoutUtil;
import com.huawei.esdk.te.util.LogUtil;
import com.huawei.esdk.te.util.OrieantationUtil;
import com.huawei.manager.DataManager;
import com.huawei.module.SDKConfigParam;
import com.huawei.service.ServiceProxy;
import com.huawei.service.eSpaceService;
import com.huawei.utils.DeviceManager;
import com.huawei.utils.StringUtil;
import com.huawei.voip.CallManager.State;
import com.huawei.voip.data.LoginInfo;

public class TESDK
{

	private static final String TAG = TESDK.class.getSimpleName();
	/**
	 * 用于限定SDK的广播接受者域 . 目前使用"com.huawei.TEMobile" 注意 ： 此处配置字符串 要和
	 * manifest文件中IM消息注册消息通知的Activity保持一致。
	 */
	private static String SDK_BROADCAST_PERMISSION = "com.huawei.TEMobile";

	private static String SDK_LOG_DIR = "/TESDKLog";

	private static TESDK instance;

	private Application application;

	private String logPath = "";

	private boolean debugSwitch = true;

	private boolean mServiceStarted;
	private ServiceProxy mService = null;

	private final List<Message> messageQueue = new ArrayList<Message>();

	private static final byte[] SERVICE_LOCK = new byte[0];
	private final Object synLock = new Object();

	public static void initSDK(Application app)
	{
		BaseApp.setApp(app);
		instance = new TESDK(app);
	}

	private TESDK(Application app)
	{
		Log.d(TAG, "TESDK construct");
		application = app;
		debugSwitch = false;
		logPath = application.getFilesDir().getPath() + SDK_LOG_DIR;
		Log.d(TAG, "init logPath -> " + logPath);

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

		Log.d(TAG, "isPhone -> " + isPhone);
		LayoutUtil.setIsPhone(isPhone);
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
			LogUtil.e(TAG, "TESDK didn't init");
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
		// 设置Log开关与路径
		this.debugSwitch = debugSwitch;
		this.logPath = path;
		if (null != path && "".equals(path))
		{
			this.logPath = path;
		}
		// 执行设置Log开关
		logSwitch();
	}

	public String getLogPath()
	{
		return this.logPath;
	}

	/**
	 * Function: 写日志总开关
	 * 
	 * @param logSwitch
	 *            写SIP日志开关
	 * @param log
	 *            日志开关
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
		LogUtil.d(TAG, "setFastLog Failed -> service is null.");
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
	public void stopSDKService()
	{
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
	}

	public void login(final LoginParameter loginParameter)
	{
		final LoginInfo loginInfo = loginParameter.getLoginInfo();

		boolean isAnonymous = loginInfo.isAnonymousLogin();
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
			Toast.makeText(application, "网络已断开", Toast.LENGTH_SHORT).show();
			return;
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
		if (getmService() != null)
		{
			new Handler().postDelayed(logoutWaitRunnable, 2000);
			getmService().logout();
			stopSDKService();
		}
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
			}

			// 回到登录界面时去初始化Datamanager
			DataManager.getIns().uninit();
		}
	};

	/**
	 * 退出eSpace 程序 清理缓存数据和销毁UI
	 */
	private void exit()
	{
		synchronized (synLock)
		{
			// 退出时去初始化Datamanager
			DataManager.getIns().uninit();
			stopSDKService();
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
