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

import com.huawei.common.LogSDK;
import com.huawei.common.Resource;
import com.huawei.common.ThreadTimer;
import com.huawei.ecs.mtk.log.AndroidLogger;
import com.huawei.ecs.mtk.log.LogLevel;
import com.huawei.ecs.mtk.log.Logger;
import com.huawei.esdk.te.call.CallLogic;
import com.huawei.esdk.te.data.Constants;
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

	private boolean debugSwitch;

	private boolean mServiceStarted;
	private ServiceProxy mService = null;

	private final List<Message> messageQueue = new ArrayList<Message>();

	private static final byte[] SERVICE_LOCK = new byte[0];
	private final Object synLock = new Object();

	public static void initSDK(Application app)
	{
		instance = new TESDK(app);
	}

	private TESDK(Application app)
	{
		application = app;
		debugSwitch = false;
		logPath = application.getFilesDir().getPath() + SDK_LOG_DIR;
		// LayoutUtil.getInstance().initialize();
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
		this.debugSwitch = debugSwitch;
		this.logPath = path;
		if (null != path && "".equals(path))
		{
			this.logPath = path;
		}
		logToFile();
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
	private boolean logToFile()
	{
		setFastLog(debugSwitch); // FastLog是底层的Log
		saveLogcat(debugSwitch, logPath); // 保存LogCat日志
		return true;
	}

	/**
	 * 设置FastLog 开关
	 */
	private void setFastLog(boolean logSwitch)
	{
		Log.d(TAG, "setFastLog");
		if (mService != null)
		{
			Log.d(TAG, "setFastLog:" + logPath);
			mService.setLogSwitch(logPath, logSwitch);
			return;
		}
		Log.d(TAG, "setFastLog Failed -> service is null.");
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
				Log.e(TAG, "logcat ecs error.");
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
			Log.i(TAG, "set Logcat ECS DEBUG>>>>>>>>>>>>>>>");
		} else
		{
			Logger.setLogFile(logPath + "ECS.txt");
			Logger.setLogLevel(LogLevel.INFO);
			Log.i(TAG, "set Logcat ECS ERROR>>>>>>>>>>>>>>>");
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
			Log.i(TAG, "startImServiceIfNeed enter.");
			if (!mServiceStarted)
			{
				Log.i(TAG, ">>>>   start  eSpaceService" + " autoLogin = " + autoLogin);
				Intent intent = new Intent(application, eSpaceService.class);
				intent.putExtra(Resource.EXTRA_CHECK_AUTO_LOGIN, autoLogin);
				application.startService(intent);
				application.bindService(intent, mImServiceConn, Context.BIND_AUTO_CREATE);
				mServiceStarted = true;
			} else
			{
				Log.i(TAG, "mServiceStarted == true");
			}
			Log.i(TAG, "startImServiceIfNeed leave.");
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
			Log.i(TAG, "stopImServiceIfInactive enter.");

			if (mServiceStarted)
			{
				if (mService != null)
				{
					Log.i(TAG, "   >>>>   stop  eSpaceService");
					mService.stopService();
					application.unbindService(mImServiceConn);
					mService = null;
				} else
				{
					Log.w(TAG, " stop  eSpaceService   mService  ==  null ");
				}
				mServiceStarted = false;
			} else
			{
				Log.w(TAG, " mServiceStarted  == false ");
			}

			Log.i(TAG, "stopImServiceIfInactive leave.");
		}
	}

	private ServiceConnection mImServiceConn = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			synchronized (SERVICE_LOCK)
			{// 快速重复点击登陆取消后，出现返回到登陆界面，但实际是注册上的状态
				Log.i(TAG, "onServiceConnected enter.");

				if (mServiceStarted)
				{
					Log.i(TAG, "onServiceConnected () enter Thread:" + Thread.currentThread().getId());
					mService = ((eSpaceService.ServiceBinder) service).getService();
					mService.setSDKConfigparam(getConfigSDKParam());
					// 开启TUP日志开关
					setFastLog(true);
					CallLogic CallService = new CallLogic(mService);
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
					Log.w(TAG, "onServiceConnected service is not start, force stop Service.");
					mServiceStarted = true;
					mService = ((eSpaceService.ServiceBinder) service).getService();
					mService.setSDKConfigparam(getConfigSDKParam());
					stopSDKService();
				}

				Log.i(TAG, "onServiceConnected leave.");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName className)
		{
			synchronized (SERVICE_LOCK)
			{
				Log.w(TAG, "onServiceDisconnected() enter Thread:" + Thread.currentThread().getId());
				mService = null;
			}
		}
	};

	public void login(final LoginInfo loginInfo)
	{

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
		// if (!DeviceManager.isNetworkAvailable(this) && isGeneralLogin)
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
						Log.d(TAG, "ServiceConnected call connectToServer start.");
						// false - 非匿名连接服务器
						connectToServer(loginInfo);
						Log.d(TAG, "ServiceConnected call connectToServer end.");
					}
				}, false);
			}
		}).run();
	}

	/**
	 * 连接服务器 isAnonymousLogin 是否为匿名登录
	 */
	private void connectToServer(LoginInfo loginInfo)
	{
		if (mService == null)
		{
			Log.w(TAG, "connect to Server error  serviceProxy is null ");
			Log.i(TAG, "connectToServer leave.");
			return;
		}

		// login返回false时上报错误状态
		if (!mService.login(loginInfo, application))
		{
			eSpaceService.getService().onLoginResult(State.UNREGISTE, Resource.NETWORK_INVALID);
		}
		Log.i(TAG, "connectToServer leave.");
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
			Log.i(TAG, "logout timer begin  to logout");
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
		Log.i(TAG, "exit app.");
	}

}
