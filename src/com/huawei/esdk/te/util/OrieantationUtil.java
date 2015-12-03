package com.huawei.esdk.te.util;

import com.huawei.application.BaseApp;
import com.huawei.esdk.te.data.Constants;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

/**
 * 摄像头角度调整类
 */
public final class OrieantationUtil {
	private static final String TAG = OrieantationUtil.class.getSimpleName();

	/**
	 * 全局唯一的实例
	 */
	private static OrieantationUtil ins;

	/**
	 * 设备默认采集角度前置是pad 0
	 */
	private int deviceReferenceAngle = 0;

	/**
	 * 设备默认采集角度后置是pad 0
	 */
	private int deviceBackReferenceAngle = 0;

	/**
	 * 默认视频通话时的显示角度
	 */
	private int defaultVideoDisplayRotate = -1;

	private OrieantationUtil() {
	}

	/**
	 * 获取类实例
	 * 
	 * @return instance实例
	 */
	public static OrieantationUtil getIns() {
		if (null == ins) {
			ins = new OrieantationUtil();
		}
		return ins;
	}

	/**
	 * 设置默认采集角度
	 */
	public void setDeviceReferenceAngle() {
		try {
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(CameraInfo.CAMERA_FACING_FRONT, cameraInfo);
			deviceReferenceAngle = cameraInfo.orientation;
			Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, cameraInfo);
			deviceBackReferenceAngle = cameraInfo.orientation;
			LogUtil.i(TAG, "deviceReferenceAngle:" + deviceReferenceAngle);
			LogUtil.i(TAG, "deviceBackReferenceAngle:" + deviceBackReferenceAngle);
		} catch (RuntimeException e) {
			LogUtil.e(TAG, "getCameraInfo error");
		}
	}

	/**
	 * 获取默认采集角度
	 * 
	 * @return 基准角度
	 */
	public int getDevicereferenceangle() {
		return deviceReferenceAngle;
	}

	/**
	 * 默认视频通话时的显示角度
	 * 
	 * @return 基准角度
	 */
	public int getDefaultVideoDisplayRotate() {
		return defaultVideoDisplayRotate;
	}

	/**
	 * 设置默认视频通话时的显示角度
	 * 
	 * @return 基准角度
	 */
	public void setDefaultVideoDisplayRotate() {
		// 已经初始化过不需要再初始化
		if (-1 != defaultVideoDisplayRotate) {
			return;
		}
		WindowManager mWm = (WindowManager) BaseApp.getApp().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 获取设备默认的显示角度，该值会根据activity横竖屏变化而调整，pad默认横屏时是ROTATION_0，手机默认竖屏时是ROTATION_0，手机横屏时该值为ROTATION_90
		defaultVideoDisplayRotate = mWm.getDefaultDisplay().getRotation();
		// 手机竖屏界面时由于此时界面activity旋转为横屏，默认显示方向改变，旋转角度也要调整90度
		// if(!ConfigApp.getInstance().isUsePadLayout())
		// {
		defaultVideoDisplayRotate = (defaultVideoDisplayRotate + 1) % 4;
		// }
		LogUtil.i(TAG, "defaultVideoDisplayRotate is: " + defaultVideoDisplayRotate);
	}

	/**
	 * 获取默认后置采集角度
	 * 
	 * @return 基准角度
	 */
	public int getDeviceBackreferenceangle() {
		return deviceBackReferenceAngle;
	}

	/**
	 * 获取摄像头默认采集方向
	 * 
	 * @param camType
	 *            前置或后置
	 * @return 取摄像头默认采集方向
	 */
	public int getDisplayOrientation(int camType) {
		int degrees = 0;
		int rotation = defaultVideoDisplayRotate;
		// 手机竖屏时 将视频界面隐藏的话会导致远端看到视频竖屏
		// if(CallLogic.getIns().isMainView())
		// {
		// rotation +=1;
		// }
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		default:
			break;
		}
		int result = 0;
		// 前置， 获取当前设备的摄像头采集角度，设备默认采集角度+设备显示角度(通话界面始终横屏，以横屏时显示角度为准)
		if (camType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (deviceReferenceAngle + degrees) % 360;
			// // compensate the mirror
			// result = (360 - result) % 360;
		}
		// 后置
		else {
			result = (deviceBackReferenceAngle - degrees + 360) % 360;
		}
		return result;
	}

	/**
	 * 将摄像头采集方向转换为HME识别模式
	 * 
	 * @param camType
	 *            前置或后置
	 * @return 取摄像头默认采集方向
	 */
	public int calcCamOrieantation(int camType) {
		int cameraRotation = 0;
		int dispOrieantion = getDisplayOrientation(camType);
		switch (dispOrieantion) {
		case 0:
			cameraRotation = 0;
			break;
		case 90:
			cameraRotation = 1;
			break;
		case 180:
			cameraRotation = 2;
			break;
		case 270:
			cameraRotation = 3;
			break;
		default:
			break;
		}
		return cameraRotation;
	}

}
