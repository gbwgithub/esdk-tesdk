package com.huawei.esdk.te.util;

import com.huawei.application.BaseApp;
import com.huawei.esdk.te.TESDK;
import com.huawei.esdk.te.data.Constants;
import com.huawei.utils.PlatformInfo;
import com.huawei.voip.data.VideoCaps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 定义全局的布局参数。
 */
public final class LayoutUtil
{
	private static final String TAG = LayoutUtil.class.getSimpleName();

	/**
	 * 全局唯一的实例
	 */
	private final static LayoutUtil INSTANCE = new LayoutUtil();

	/************************************** 全局的屏幕参数 *************************************************/

	/**
	 * 标准 DPI 以 1280*800 分辨率，160DPI 为参照标准
	 * 
	 */
	public static final float DENSITYDPI_REFERENCE_STANDARD = 1f; // 默认的 1dip ==
																	// 1px
	/**
	 * 是否手机
	 */
	private static boolean isPhoneTag;

	/**
	 * 默认加载的是否竖屏布局
	 */
	private static boolean isLoadPortLayoutTag;

	/**
	 * 标准分辨率下的长边
	 */
	private static final int WIDTH_REFERENCE_STANDARD = 1280;

	/**
	 * 默认的 填充父容器布局
	 */
	private static final LinearLayout.LayoutParams DEFAULT_FILL_PARENT_LAYOUT = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
			LayoutParams.FILL_PARENT);

	/**
	 * 缩放倍率， 在计算布局的时候要考虑到 小屏幕的 适配，需要根据小屏幕的尺寸和标准屏幕（800dp）的比例来缩放
	 * 在精确计算像素的时候需要乘以这个系数
	 */
	private static float scaleMultiple = -1; // 默认值为 -1 ， 表示没有初始化。 需要从新初始化

	/**
	 * 实际的屏幕宽度 单位像素
	 */
	private int screenWidth = WIDTH_REFERENCE_STANDARD; // 默认值为 1280

	/**
	 * 屏幕高度
	 */
	private int screenHeight = 800;// 屏幕默认高度为 800

	/**
	 * decode bitmap的 参数 . 按照dip比例decode
	 */
	private BitmapFactory.Options dpiBitMapOptions = new BitmapFactory.Options();

	/**
	 * 按照 屏幕px 比例 decode
	 */
	private BitmapFactory.Options pxBitMapOptions = new BitmapFactory.Options();

	/**
	 * 真实的 BitMap DIP
	 */
	private BitmapFactory.Options realDPIBitMapOptions = new BitmapFactory.Options();

	/**
	 * 按照dip 缩放倍率
	 */
	private float dpiScale = 1f;

	/**
	 * 按照屏幕的像素尺寸缩放倍率
	 */
	private double screenPXScale = 1f;

	/**
	 * 当前的真实dpi 缩放倍率
	 */
	private float realDensity = 1f;

	/**
	 * 定制版本
	 */
	private static String customizeVersions = "default";

	/**
	 * @return 按照dip缩放的倍率
	 */
	public float getDpiScale()
	{
		return dpiScale;
	}

	/**
	 * @return 按照屏幕的像素 和 1280 像素的比例 计算出来的缩放倍率
	 */
	public float getScreenPXScale()
	{
		float tmpScreenPXScale = Double.valueOf(screenPXScale).floatValue();
		return tmpScreenPXScale;
	}

	public float getRealDensity()
	{
		return realDensity;
	}

	public BitmapFactory.Options getDpiBitMapOptions()
	{
		return dpiBitMapOptions;
	}

	public BitmapFactory.Options getPxBitMapOptions()
	{
		return pxBitMapOptions;
	}

	/**
	 * Function: 返回默认的LinearLayout 自适应父容器的 layoutParams
	 */
	public static LinearLayout.LayoutParams getDefaultFilllayout()
	{
		return DEFAULT_FILL_PARENT_LAYOUT;
	}

	/**************************************
	 * HomeActivity 布局参数
	 **********************************************************/

	/**
	 * 首页卡片 在 1280 分辨率下 的宽度像素
	 */
	public static final int HOME_CARD_WIRDH = 390;

	/**
	 * 卡片paddings
	 */
	public static final int CARD_PADDING_S = 0;

	/**
	 * 标准分辨率下的 首页卡片距离两边的边距
	 */
	public static final int HOME_SLIDE_VIEW_PADDING_STANDARD = 55;

	/**
	 * 判断是否需要分页的 滑动范围
	 */
	public static final int DEFAULT_MOVE_DINSTANCE = 30;

	/**
	 * 首页卡片的宽度。 默认为1280 分辨率下的像素宽度 390
	 */
	private int homeCardRealPxWidth = HOME_CARD_WIRDH;

	/**
	 * 触发滑屏 的最小touch move 距离
	 */
	private int needMoveDistance = DEFAULT_MOVE_DINSTANCE;

	/**
	 * 计算出来的 card 水平边距
	 */
	private int homeSlideViewPadding = HOME_SLIDE_VIEW_PADDING_STANDARD;

	/**
	 * 首页卡片的实际px宽度
	 */
	public int getHomeCardRealPxWidth()
	{
		return homeCardRealPxWidth;
	}

	/**
	 * 首页卡片的水平边距
	 */
	public int getHomeSlideViewPadding()
	{
		return homeSlideViewPadding;
	}

	/**
	 * 首页卡片的最小滑屏移动距离 大于这个距离 就会触发滑屏
	 */
	public int getNeedMoveDistance()
	{
		return needMoveDistance;
	}

	/**************************************** 聊天及二级页面的布局参数 **************************************************/

	// 标准分辨率下的默认值
	/**
	 * 聊天窗口宽度
	 */
	public static final int CHAT_SESSION_ITEM_WIDHT = 750;

	/**
	 * 数据会议或聊天（多人）时标题中显示的参与者姓名限定宽度 仅是名字组合宽度
	 */
	public static final int TITLE_NAME_WIDTH = 280;

	/**
	 * 会议列表类型栏高度
	 */
	public static final int CONFLIST_TITLE_HEIGHT = 20;

	/**
	 * 会议列表会议Item高度
	 */
	public static final int CONFLIST_ITEM_HEIGHT = 70;

	private int densityDpi;

	private LayoutUtil()
	{
	}

	/**
	 * Function: 根据获取到的屏幕参数数据，初始化和屏幕参数相关的布局参数
	 * 
	 * @param density
	 *            屏幕密度
	 * @param screenWidthPx
	 *            当前的屏幕宽度
	 */
	private void initialize(int screenWidthPx, int height, float density)
	{
		screenWidth = screenWidthPx;
		screenHeight = height;
		VideoCaps.setScreenRatio(screenWidth, screenHeight);
		LogUtil.i(TAG, "Initialize Screen info density = " + density);

		if (Math.abs(screenWidthPx * DENSITYDPI_REFERENCE_STANDARD - WIDTH_REFERENCE_STANDARD * density) < 0.0000001)
		{
			setScaleMultiple(density);
			dpiScale = density;
			LogUtil.i(TAG, " Layout for normal scale screen such as MediaPad S10");
		} else
		{
			setScaleMultiple(1);
			dpiScale = 1;
			LogUtil.i(TAG, " Layout for PX scale screen ");
		}

		// 判断是 mobile 还是 pad 根据长边的最大 dpi 来估算， 长边dip > 900 , 认为是pad
		if (screenWidthPx / density < 900)
		{
			LogUtil.i(TAG, "Layout for mobile screen");
		} else
		{
			LogUtil.i(TAG, "Layout for pad screen");
		}

		realDensity = density;
		screenPXScale = (Integer.valueOf(screenWidth > screenHeight ? screenWidth : screenHeight).doubleValue() / WIDTH_REFERENCE_STANDARD);
		homeCardRealPxWidth = doubleToInt(HOME_CARD_WIRDH * screenPXScale);
		needMoveDistance = doubleToInt(DEFAULT_MOVE_DINSTANCE * screenPXScale);
		homeSlideViewPadding = doubleToInt(HOME_SLIDE_VIEW_PADDING_STANDARD * screenPXScale);

		dpiBitMapOptions.inDensity = doubleToInt(dpiScale * DENSITYDPI_REFERENCE_STANDARD);
		pxBitMapOptions.inDensity = doubleToInt(screenPXScale * DENSITYDPI_REFERENCE_STANDARD);
		realDPIBitMapOptions.inDensity = doubleToInt(density * DENSITYDPI_REFERENCE_STANDARD);
	}

	public static int doubleToInt(double floatValue)
	{
		return Double.valueOf(floatValue).intValue();
	}

	public static void setScaleMultiple(float multiple)
	{
		LayoutUtil.scaleMultiple = multiple;
	}

	/**
	 * Function: 初始化 布局参数. 适配UI
	 * 
	 * @return void
	 */
	public void initialize()
	{
		DisplayMetrics metric = new DisplayMetrics();
		
		WindowManager wm = (WindowManager) TESDK.getInstance().getApplication().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; 	// 屏幕宽度（像素）
		int height = metric.heightPixels;   // 屏幕高度（像素）
		float density = metric.density; 	// 屏幕密度（0.75 / 1.0 / 1.5）
		this.densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）

		// 计算屏幕尺寸，如果大于7寸当成是PAD
		if (isPadScreen())
		{
			LogUtil.i(TAG, "set pad layout: true");
			// ConfigApp.getInstance().setUsePadLayout(true);
		}

		LogUtil.i(TAG, metric + ",densityDpi:" + densityDpi);
		// if (ConfigApp.getInstance().isUsePadLayout()) {
		if (height > width)
		{
			int temp = height;
			height = width;
			width = temp;
		}
		// } else {
		// if (height < width) {
		// int temp = height;
		// height = width;
		// width = temp;
		// }
		// }
		// end modify by cwx176935 reason: ANDRIOD-198
		initialize(width, height, density);
	}

	/**
	 * 是否pad屏幕，如果大于7寸当成是PAD
	 * 
	 * @return 是否pad屏幕
	 */
	public boolean isPadScreen()
	{
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager wm = (WindowManager) BaseApp.getApp().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels;
		int height = metric.heightPixels;
		float xdpi = metric.xdpi;
		float ydpi = metric.ydpi;
		float density = metric.density;
		double size = Math.sqrt(Math.pow(width / xdpi, 2) + Math.pow(height / ydpi, 2));
		LogUtil.i(TAG, "screen size: " + size);
		if (size > 6.600000 && Math.max(width, height) / density > 900)
		{
			return true;
		}
		return false;
	}

	public static LayoutUtil getInstance()
	{
		return LayoutUtil.INSTANCE;
	}

	/**
	 * Function: 该方法废弃 {@link #getDpiScale()} , {@link #getScreenPXScale()}
	 * instead.
	 */
	public static float getScaleMultiple()
	{
		boolean isbool = (scaleMultiple - (-1) < 0.0001);
		if (isbool)
		{
			LogUtil.w(TAG, "ScaleMultiple " + "is been recycled, new ScaleMultiple = " + scaleMultiple);
		}
		return scaleMultiple;
	}

	/**
	 * @return the screenWidth
	 */
	public int getScreenWidth()
	{
		return screenWidth;
	}

	/**
	 * Function: 将Bitmap 按照倍率缩放
	 */
	public static Bitmap getScaleBitMap(Bitmap bitmap, float scaleMultiple)
	{
		if (bitmap != null)
		{
			if (scaleMultiple <= 0)
			{
				scaleMultiple = 1;
			}

			scaleMultiple = scaleMultiple * LayoutUtil.getInstance().getDpiScale();
			bitmap = Bitmap.createScaledBitmap(bitmap, doubleToInt(bitmap.getWidth() * scaleMultiple), doubleToInt(bitmap.getHeight() * scaleMultiple),
					true);
		}
		return bitmap;
	}

	/**
	 * Function: 根据真实的屏幕密度取 资源图片
	 */
	public Drawable getResourceByRealDPI(int resourceId)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(BaseApp.getApp().getResources(), resourceId, realDPIBitMapOptions);
		if (bitmap == null)
		{
			return BaseApp.getApp().getResources().getDrawable(resourceId);
		} else
		{
			return new BitmapDrawable(bitmap);
		}
	}

	public int getScreenHeight()
	{
		return screenHeight;
	}

	/**
	 * 不适合listview的应用 显示一个TextView的文字显示不下以...的形式显示
	 * 说明用这个方法需要发xml中的singLine设为false且不设置ellipsize样式
	 * 
	 * @param view
	 *            需要改样式的view
	 */
	public static void setViewEndEllipse(final TextView view)
	{
		seViewEndEllipse(view, 1);
	}

	/**
	 * 不适合listview的应用 显示一个TextView的文字显示不下以...结束的形式显示
	 * 说明用这个方法需要发xml中的singLine设为false且不设置ellipsize样式
	 * 
	 * @param view
	 *            需要改样式的view
	 * @param lineCount
	 *            需要显示的行数
	 */
	public static void seViewEndEllipse(final TextView view, final int lineCount)
	{
		if (lineCount < 1)
		{
			return;
		}
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{

			@Override
			public void onGlobalLayout()
			{
				ViewTreeObserver obs = view.getViewTreeObserver();
				obs.removeGlobalOnLayoutListener(this);
				if (view.getLineCount() > lineCount)
				{
					// 硬终端会场名称为两个汉字开始+大于17位的数字或者字母的形式，呼叫安卓mobile，安卓 mobile会自动重启
					int lineEndIndex = view.getLayout().getLineEnd(lineCount - 1);
					String text = view.getText().subSequence(0, lineEndIndex) + "...";
					try
					{
						text = view.getText().subSequence(0, lineEndIndex - 2) + "...";
					} catch (IndexOutOfBoundsException e)
					{
						LogUtil.d(TAG, "text size to small.");
					}
					// end modify by cwx176935 reason: DTS2014121300053
					// 硬终端会场名称为两个汉字开始+大于17位的数字或者字母的形式，呼叫安卓mobile，安卓 mobile会自动重启
					view.setText(text);
				}
			}

		});
	}

	// 联系人列表中名称较长的联系人经滚动条滚动后，联系人名称显示方式有误
	/**
	 * 示一个TextView的文字显示不下以...的形式显示
	 * 
	 * @param textView
	 *            要显示的view
	 * @param str
	 *            要显示文字
	 * @param len
	 *            规定的长度
	 * @since 1.1
	 * @history 2013-11-1 v1.0.0 cWX176935 create
	 */
	public static void setEndEllipse(final TextView textView, final String str, int len)
	{
		if (null == str)
		{
			LogUtil.e(TAG, "str is null");
			return;
		}
		CharSequence charc = TextUtils.ellipsize(str, textView.getPaint(), len, TextUtils.TruncateAt.END);
		textView.setText(charc);
	}

	// 联系人列表中名称较长的联系人经滚动条滚动后，联系人名称显示方式有误

	// 设置是否手机屏幕
	/**
	 * 设置是否为手机
	 */
	public static void setIsPhone(boolean bPhone)
	{
		isPhoneTag = bPhone;

		LogUtil.i(TAG, "set is phone to " + isPhoneTag);
		LogUtil.i(TAG, "Build.DISPLAY : " + Build.DISPLAY);
	}

	/**
	 * 对华为X1(7D-501u)做特殊判断，X1为手机，未打开软终端时界面为横屏时(eg:设置界面)情况下会加载pad布局
	 * 
	 * @return true 是
	 */
	public static boolean isHuaweiX1()
	{
		return Build.DISPLAY.contains("7D-");
	}

	/**
	 * 创建render时是否使用glsurfaceView
	 * 
	 * @return true 是
	 */
	public static boolean isUseGLSurfaceView()
	{
		boolean isGLSurfaceViewFlag = true;
		isGLSurfaceViewFlag = PlatformInfo.getAndroidVersion() >= PlatformInfo.ANDROID_VER_3_0;
		// if (Particular.getIns().isSamsungT231())
		// {
		// isGLSurfaceViewFlag = false;
		// }
		LogUtil.i(TAG, "isGLSurfaceViewFlag : " + isGLSurfaceViewFlag);
		return isGLSurfaceViewFlag;
	}

	public static boolean isPhone()
	{
		return isPhoneTag;
	}

	/**
	 * 获取当前加载的布局类型
	 * 
	 * @return true 是
	 */
	public static boolean isLoadPortLayout()
	{
		return isLoadPortLayoutTag;
	}

	/**
	 * 设置是否加载竖屏布局
	 */
	public static void setLoadPortLayout(boolean isLoadPortLayout)
	{
		isLoadPortLayoutTag = isLoadPortLayout;
	}

	// PAD A与PAD B处于通话中，对PAD
	// A锁屏或者退到PAD桌面，
	// PAD B切换视频或辅流或音频，
	// PAD A没有任何提示
	/**
	 * 将一个Activity放到锁屏之上
	 * 
	 * @param activity
	 *            要操作的Activity
	 */
	public static void setFrontToLock(Activity activity)
	{
		// 来电界面显示在锁屏界面之上，且点亮屏幕
		Window win = activity.getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	/**
	 * 解除锁屏之上
	 * 
	 * @param activity
	 */
	public static void releaseFrontToLock(Activity activity)
	{
		if (null == activity)
		{
			return;
		}
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	// end A锁屏或者退到PAD桌面，PAD B切换视频或辅流或音频，PAD A没有任何提示

	/**
	 * 释放View里面的图片资源
	 * 
	 * @param rootView
	 *            要视频的view
	 */
	public static void releaseViewGroupRes(ViewGroup rootView)
	{

	}

	public static String getCustomizeVersions()
	{
		return customizeVersions;
	}

	/**
	 * 设置制定版本
	 * 
	 * @param customizeVersions
	 *            制定版本
	 */
	public static void setCustomizeVersions(String customizeVersionstr)
	{
		customizeVersions = customizeVersionstr;
	}
}
