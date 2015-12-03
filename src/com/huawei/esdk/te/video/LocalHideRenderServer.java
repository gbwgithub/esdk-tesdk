package com.huawei.esdk.te.video;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.huawei.esdk.te.call.CallLogic;
import com.huawei.esdk.te.util.LogUtil;

/**
 * 用于添加本地采集点的service 操作流程 stop-->remove-->start-->add
 * remove必须在hme_videocapture_stop之后，原因没有停止采集先移除view，会导致预览报错
 * 推荐addView必须在hme_videocapture_start之后，原因设置预览参数和设置view的回调在start中完成，
 * 顺序搞反会导致addView不能触发系统surfacecreate，导致没数据出来
 */
public class LocalHideRenderServer extends Service {

	private static final String TAG = LocalHideRenderServer.class.getSimpleName();

	/**
	 * 单实例
	 */
	private static LocalHideRenderServer instance;

	/**
	 * 在remove之后是否需要add
	 */
	private boolean isNeedAdd = false;

	private boolean isDone = true;

	private final IBinder binder = new LocalHideRenderBinder();

	/**
	 * windows的服务类
	 */
	private WindowManager mWindowManager;

	/**
	 * 服务类的布局
	 */
	private WindowManager.LayoutParams wmParams;

	/**
	 * 添加到window中的view
	 */
	private ViewGroup localHideViewGroup;

	private boolean isScreenOff = false;

	private boolean isInit = false;

	/**
	 * 是否后台运行
	 */
	private boolean isBackground = false;

	/**
	 * 创建
	 */
	@Override
	public void onCreate() {
		LogUtil.d(TAG, "LocalHideRenderServer onCreate()");
		isNeedAdd = false;
		synchronized (LocalHideRenderServer.class) {
			isDone = true;
		}
		localHideViewGroup = new LinearLayout(this);
		this.mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2003;
		wmParams.gravity = 51;
		wmParams.flags = 520;
		wmParams.format = 1;
		// 个别手机问题
		wmParams.width = 1;
		wmParams.height = 1;
		// 大小屏切换出现花屏
		wmParams.x = -1;
		wmParams.y = -1;

		// 大小屏切换出现花屏
		setInstance(this);
		LogUtil.i(TAG, "start local hide service");
		// 个别手机问题
		// 设置屏幕亮暗
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_BATTERY_LOW);
		registerReceiver(mScreenReceiver, filter);
	}

	/**
	 * 设置单实例
	 */
	private static void setInstance(LocalHideRenderServer server) {
		instance = server;
	}

	/**
	 * 获得单实例
	 * 
	 * @return 当前对象
	 */
	public static LocalHideRenderServer getInstance() {
		return instance;
	}

	/**
	 * 添加view
	 * 
	 * @param viewVar
	 *            目标
	 */
	public void removeView(View viewVar) {
		synchronized (LocalHideRenderServer.class) {
			if (null == viewVar || null == localHideViewGroup) {
				return;
			}
			if (null == localHideViewGroup.getParent() || null == viewVar.getParent()) {
				if (isNeedAdd) {
					startCameraStream();
				}
				LogUtil.i(TAG, "first add view");
				return;
			}
			localHideViewGroup.removeView(viewVar);
			mWindowManager.removeViewImmediate(localHideViewGroup);
			LogUtil.i(TAG, "local hide view removed");

			if (isNeedAdd) {
				startCameraStream();
			} else {
				isDone = true;
			}
		}
	}

	/**
	 * 移除view
	 * 
	 * @param viewVar
	 *            目标
	 */
	public void addView(View viewVar) {
		synchronized (LocalHideRenderServer.class) {
			if (null == viewVar) {
				LogUtil.i(TAG, "view is null");
				return;
			}
			if (null == localHideViewGroup) {
				LogUtil.i(TAG, "parent is null  new a parent");
				localHideViewGroup = new LinearLayout(this);
			}
			if (null != viewVar.getParent()) {
				stopCameraStream();
				isDone = false;
				isNeedAdd = true;
				return;
			}
			localHideViewGroup.addView(viewVar);
			if (null == localHideViewGroup.getParent()) {
				mWindowManager.addView(localHideViewGroup, wmParams);
			}
			LogUtil.i(TAG, "local hide view add");
			isDone = true;
			isNeedAdd = false;
		}
	}

	/**
	 * 关闭摄像头数据
	 */
	public void stopCameraStream() {
		synchronized (LocalHideRenderServer.class) {
			LogUtil.i(TAG, "stopCameraStream");
			isDone = false;
			CallLogic.getInstance().controlVideoCapture(false);
		}
	}

	/**
	 * 开启摄像头数据
	 */
	public void startCameraStream() {
		synchronized (LocalHideRenderServer.class) {
			LogUtil.i(TAG, "startCameraStream");
			isDone = false;
			CallLogic.getInstance().controlVideoCapture(true);
		}
	}

	/**
	 * 移除采集点
	 */
	public void removeView() {
		synchronized (LocalHideRenderServer.class) {
			if (null == localHideViewGroup) {
				return;
			}
			LogUtil.i(TAG, "remove local hide view");
			localHideViewGroup.removeAllViews();
			if (null != localHideViewGroup.getParent()) {
				mWindowManager.removeViewImmediate(localHideViewGroup);
			}
			localHideViewGroup = null;
		}
	}

	/**
	 * 是否需要添加
	 * 
	 * @return true是
	 * @since 1.1
	 * @history 2013-12-10 v1.0.0 cWX176935 create
	 */
	public boolean isNeedAdd() {
		return isNeedAdd;
	}

	/**
	 * 是否需要添加
	 * 
	 * @param isNeedAdd
	 *            true是
	 * @since 1.1
	 * @history 2013-12-10 v1.0.0 cWX176935 create
	 */
	public void setNeedAdd(boolean isNeedAdd) {
		this.isNeedAdd = isNeedAdd;
	}

	/**
	 * @return true屏幕已经暗掉
	 */
	public boolean isScreenOff() {
		return isScreenOff;
	}

	/**
	 * 设置屏幕是否已经暗掉
	 * 
	 * @param isScreenOff
	 *            true是
	 * @since 1.1
	 * @history 2015-3-12 v1.0.0 l00208218 create
	 */
	public void setScreenOff(boolean isScreenOff) {
		this.isScreenOff = isScreenOff;
	}

	/**
	 * remove add动作是否完成
	 * 
	 * @return true完成
	 * @since 1.1
	 * @history 2013-12-10 v1.0.0 cWX176935 create
	 */
	public boolean isDone() {
		synchronized (LocalHideRenderServer.class) {
			return isDone;
		}
	}

	// begin add by cwx176935 reason: 视频通话中，软终端程序最小化切换到后台；本地摄像头仍然开启着，应该关掉
	/**
	 * 是否后台运行
	 * 
	 * @param background
	 */
	public void setBackground(boolean background) {
		isBackground = background;
	}

	/**
	 * 运行后台
	 */
	public void doInBackground() {
		setBackground(true);
		LogUtil.d(TAG, "program run in background.");
		// CommonManager.getInstance().getVoip().localCameraControl(true);
		CallLogic.getInstance().localCameraControl(true);
	}

	/**
	 * 从后台中恢复
	 */
	public void doBackFromBackground() {
		if (!isBackground) {
			return;
		}

		isBackground = false;
		LogUtil.d(TAG, "program run from background.");
		// CommonManager.getInstance().getVoip().localCameraControl(false);

		CallLogic.getInstance().localCameraControl(true);
	}

	public boolean isBackground() {
		return isBackground;
	}
	// end add by cwx176935 reason: 视频通话中，软终端程序最小化切换到后台；本地摄像头仍然开启着，应该关掉

	/**
	 * 绑定binder
	 * 
	 * @param intent
	 *            intent对象
	 * @return IBinder IBinder对象
	 * @since 1.1
	 * @history 2013-9-25 v1.0.0 cWX176935 create
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * 销毁service 时操作
	 */
	@Override
	public void onDestroy() 
	{
		LogUtil.e(TAG, "LocalHideRenderServer onDestroy()");
		// 只有在杀进程的时候才进强制关闭
		// if (TESDK.getInstance().isKillPro()) {
		// // 安卓pad发送辅流后，应用程序中的”正在运行“项停止eSpace for
		// // TP进程，软终端自动重启登录，登录失败，需要关机后登录成功
		if(!CallLogic.getInstance().isCallClosed())
		{
			LogUtil.d(TAG, "currentCallID is not NULL ");
			CallLogic.getInstance().forceCloseCall();
		}

		// TESDK.getInstance().setKillPro(false);
		// // 安卓pad发送辅流后，应用程序中的”正在运行“项停止eSpace for
		// // TP进程，软终端自动重启登录，登录失败，需要关机后登录成功
		// }

		// 退出安卓客户端，然后在设置中把语言改为英文，客户端会自动登录
		if (null != instance && !instance.isInit) {
			return;
		}
		unregisterReceiver(mScreenReceiver);
		this.stopSelf();
		LogUtil.i(TAG, "local hide service destroy");
		if (null != instance) {
			instance.isInit = false;
		}
		if (null == localHideViewGroup) {
			localHideViewGroup = null;
			mWindowManager = null;
			return;
		}
		localHideViewGroup.removeAllViews();
		if (null != localHideViewGroup.getParent()) {
			mWindowManager.removeViewImmediate(localHideViewGroup);
		}
		localHideViewGroup = null;
		mWindowManager = null;
	}

	/**
	 * service的binder
	 */
	private class LocalHideRenderBinder extends Binder 
	{
		LocalHideRenderServer getService() 
		{
			return LocalHideRenderServer.this;
		}
	}

	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(action)) {
				isScreenOff = false;
				LogUtil.i(TAG, "screen has on");
			} else if (Intent.ACTION_BATTERY_LOW.equalsIgnoreCase(action)) {
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				LogUtil.i(TAG, "battery warning: battery level:" + level);
			}
		}
	};
}