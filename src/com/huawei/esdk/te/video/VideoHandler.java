package com.huawei.esdk.te.video;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.huawei.esdk.te.TESDK;
import com.huawei.esdk.te.call.CallConstants.CallStatus;
import com.huawei.esdk.te.call.CallLogic;
import com.huawei.esdk.te.data.Constants;
import com.huawei.esdk.te.util.OrieantationUtil;
import com.huawei.service.eSpaceService;
import com.huawei.utils.PlatformInfo;
import com.huawei.videoengine.CaptureCapabilityAndroid;
import com.huawei.videoengine.ViERenderer;
import com.huawei.videoengine.VideoCaptureDeviceInfoAndroid;
import com.huawei.voip.data.VOIPConfigParamsData;
import com.huawei.voip.data.VideoCaps;
import common.VideoWndType;

/**
 * 类名称：VideoSurfViewManager.java 类描述：点对点视频控制类
 */
final public class VideoHandler
{
	private static final String TAG = VideoHandler.class.getSimpleName();
	public static final int CAMERA_NON = -1;
	public static final int CAMERA_NORMAL = 0;
	public static final int CAMERA_SCARCE_CAPACITY = 1;

	private Context context = null;

	private Handler uiHandler = null;

	private boolean isRenderRemoveDone = true;

	/**
	 * 摄像头能力
	 */
	private Map<Integer, Integer> cameraCapacity = new HashMap<Integer, Integer>(0);

	/**
	 * 是否已经检测过摄像头能力
	 */
	private boolean hasCheckCamera = false;

	/** 后置摄像头 */
	public static final int BACK_CAMERA = 0;

	/** 前置摄像头 */
	public static final int FRONT_CAMERA = 1;

	/** 相机状态（本地视频视图句柄） */
	private int cameraIndex = FRONT_CAMERA;

	private SurfaceView localHideView;

	/** 本地视频画面，自己的图像 */
	private SurfaceView localCallView;

	/** 远程视频画面，别人的图像 */
	private SurfaceView remoteCallView;

	/** 本地辅流画面 */
	private SurfaceView localBfcpView;

	/** 远端辅流画面 */
	private SurfaceView remoteBfcpView;

	// 振铃崩溃，清除HMERender
	/**
	 * 远端视频
	 */
	private RelativeLayout remoteVideoView;
	// 振铃崩溃，清除HMERender

	// /*
	// * 本端窗口矩形
	// */
	// private VideoViewRect localViewRect = null;
	//
	// /*
	// * 远端大窗口矩形
	// */
	// private VideoViewRect remoteBigViewRect = null;

	/** 本地视频视图句柄 */
	private int localCallIndex;

	private int curLocalIndex;

	/** 远程视频视图句柄 */
	private int remoteCallIndex;

	/*
	 * 当前使用的远端窗口句柄
	 */
	private int curUseRemoteRenderIndex;

	/** 远程辅流视频视图句柄 */
	private int remoteBfcpIndex;

	/** 本地辅流视频视图句柄 */
	private int localBfcpIndex;

	/** 摄像头数量 */
	private int numberOfCameras;

	/** 单实例 */
	private static VideoHandler ins;
	// 增加视频参数
	private VideoCaps videoCaps = new VideoCaps(false, false);
	// 增加视频参数
	// 辅流的caps
	private VideoCaps dataCaps = new VideoCaps(false, true);

	private boolean isInit = false;

	private boolean isChanging = false;

	/** 是否正在视频 */
	private boolean isVideoing;

	/**
	 * -1 初始值 0竖屏 1右横屏 2倒屏 3左横屏 , 自加操作是为了与上次不同
	 */
	private int remoteTurnDirc = -1;

	/**
	 * 记录当前旋转后的角度 为规避 HME切换摄像头时调用旋转不起作用的问题
	 */
	private int curTurnDegree = 0;

	/**
	 * 描述：获取自身实例 作者：xKF49568
	 * 
	 * @return
	 */
	public static synchronized VideoHandler getIns()
	{
		if (ins == null)
		{
			ins = new VideoHandler();
		}
		return ins;
	}

	/**
	 * 构造函数
	 */
	private VideoHandler()
	{

		context = TESDK.getInstance().getApplication();
		if (context == null)
		{
			throw new NullPointerException("TESDK not initialated");
		}

		uiHandler = new Handler(context.getMainLooper());

		numberOfCameras = Camera.getNumberOfCameras();
		// 安卓手机后置摄像头不可用时，视频切换前后摄像头，视频卡住不能恢复，需要退出重新登录
		// 默认设备为两个摄像头且都为好的
		cameraCapacity.put(FRONT_CAMERA, CAMERA_NORMAL);
		cameraCapacity.put(BACK_CAMERA, CAMERA_NORMAL);

	}

	/**
	 * 描述：初始化点对点视频通话中视频数据
	 */
	// 增加视频参数
	public VideoCaps initCallVideo()
	{
		if (isInit)
		{
			return videoCaps;
		}

		isInit = true;
		Log.d(TAG, "Init Call Video");
		VOIPConfigParamsData voipCfg = null;
		if (null != eSpaceService.getService() && null != eSpaceService.getService().callManager
				&& null != eSpaceService.getService().callManager.getVoipConfig()
				&& null != eSpaceService.getService().callManager.getVoipConfig().getSipPort())
		{
			voipCfg = eSpaceService.getService().callManager.getVoipConfig();
		}
		boolean isHardCodec = false;
		if (null != voipCfg)
		{
			isHardCodec = voipCfg.isHardCodec();
		}

		// 重新创建媒体参数对象，脱离上次通话相关参数
		videoCaps = new VideoCaps(isHardCodec, false);
		dataCaps = new VideoCaps(isHardCodec, true);

		localHideView = ViERenderer.CreateLocalRenderer(context);
		localHideView.setZOrderOnTop(false);

		boolean createRendererFlag = isUseGLSurfaceView();
		// 获取本地的视频视图
		localCallView = ViERenderer.CreateRenderer(context, createRendererFlag);
		// 获取远端的视频视图
		remoteCallView = ViERenderer.CreateRenderer(context, createRendererFlag);
		// 获取远端的辅流视图
		remoteBfcpView = ViERenderer.CreateRenderer(context, createRendererFlag);
		// 获取本地的辅流视图
		localBfcpView = ViERenderer.CreateRenderer(context, createRendererFlag);
		remoteCallView.setZOrderOnTop(false);
		localBfcpView.setZOrderOnTop(false);

		remoteBfcpView.setZOrderOnTop(false);
		// 默认设置为不在最上层界面显示，后续在添加到布局时动态设置
		// 这个设置是否在屏幕的最上面
		localCallView.setZOrderOnTop(false);
		// 这个是设置在媒体的最上面
		localCallView.setZOrderMediaOverlay(true);
		// 获取本地视频句柄，用于视频设备的绑定（绑定哪个设备，就显示哪个设备的视频）
		localCallIndex = ViERenderer.getIndexOfSurface(localCallView);
		curLocalIndex = localCallIndex;

		// 获取远端视频句柄，用于视频设备的绑定（绑定哪个设备，就显示哪个设备的视频）
		remoteCallIndex = ViERenderer.getIndexOfSurface(remoteCallView);
		curUseRemoteRenderIndex = remoteCallIndex;

		// 获取远端辅流视频句柄，用于视频设备的绑定（绑定哪个设备，就显示哪个设备的视频）
		remoteBfcpIndex = ViERenderer.getIndexOfSurface(remoteBfcpView);

		// remoteCallSmallIndex =
		// ViERenderer.getIndexOfSurface(remoteCallSmallView);

		// 获取本地辅流视频句柄，用于视频设备的绑定（绑定哪个设备，就显示哪个设备的视频）
		localBfcpIndex = ViERenderer.getIndexOfSurface(localBfcpView);

		// 获取本地视频句柄
		cameraIndex = numberOfCameras > 1 ? FRONT_CAMERA : BACK_CAMERA;
		// 安卓手机后置摄像头不可用时，视频切换前后摄像头，视频卡住不能恢复，需要退出重新登录
		if (cameraCapacity.get(BACK_CAMERA) == CAMERA_NON)
		{
			cameraIndex = FRONT_CAMERA;
		} else if (cameraCapacity.get(FRONT_CAMERA) == CAMERA_NON)
		{
			cameraIndex = BACK_CAMERA;
		}
		// 安卓手机后置摄像头不可用时，视频切换前后摄像头，视频卡住不能恢复，需要退出重新登录
		// 默认使用大窗口
		curUseRemoteRenderIndex = remoteCallIndex;

		// videoCaps.setVideoMode(Constants.VideoMode.VIDEO_QUALITY_MODE);

		// 初始化呼叫带宽
		int bandWidth = Constants.CallBandWidth.CALL_BANDWIDTH_512;
		Log.i(TAG, "initCallVideo bandWidth is " + bandWidth);
		// videoCaps.setBandWidth(bandWidth);
		videoCaps.setCameraIndex(cameraIndex);
		videoCaps.setPlaybackLocal(curLocalIndex);
		videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		// videoCaps.setLocalPort(10002 + 80);
		int camOrieantation = 0;
		// 手机竖屏界面时由于此时界面activity旋转为横屏，默认显示方向改变，旋转角度也要调整90度
		// if(!ConfigApp.getInstance().isUsePadLayout())
		// {
		// CamOrieantation =
		// (OrieantationUtil.getIns().calcCamOrieantation(cameraIndex) + 1)%4;
		// }
		videoCaps.setCameraRotation(camOrieantation);
		if (null != eSpaceService.getService() && null != eSpaceService.getService().callManager)
		{
			eSpaceService.getService().callManager.getTupManager().setOrientParams(videoCaps);
			eSpaceService.getService().callManager.setVideoIndex(cameraIndex);
			eSpaceService.getService().callManager.getTupManager().setVideoRenderInfo(videoCaps, VideoWndType.local);
			eSpaceService.getService().callManager.getTupManager().setVideoRenderInfo(videoCaps, VideoWndType.remote);
		}

		dataCaps.setPlaybackLocal(localBfcpIndex);
		dataCaps.setPlaybackRemote(remoteBfcpIndex);
		// dataCaps.setBandWidth(bandWidth);
		return videoCaps;
	}

	/**
	 * 创建render时是否使用glsurfaceView
	 */
	public static boolean isUseGLSurfaceView()
	{
		boolean isGLSurfaceViewFlag = true;
		isGLSurfaceViewFlag = PlatformInfo.getAndroidVersion() >= PlatformInfo.ANDROID_VER_3_0;
		Log.i(TAG, "isGLSurfaceViewFlag : " + isGLSurfaceViewFlag);
		return isGLSurfaceViewFlag;
	}

	// 安卓手机后置摄像头不可用时，视频切换前后摄像头，视频卡住不能恢复，需要退出重新登录
	public void checkCameraBeforeCall()
	{
		Executors.newSingleThreadExecutor().execute(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if (!hasCheckCamera)
				{
					calcCameraAbility();
					hasCheckCamera = true;
				}
			}
		});
	}

	// 安卓手机后置摄像头不可用时，视频切换前后摄像头，视频卡住不能恢复，需要退出重新登录
	// 与PC视频通话过程中，将摄像头前后切换后，远端视频卡住
	/**
	 * 获得前置摄像头的能力
	 */
	public int[] getFrontCameraInfo()
	{
		return getCameraInfo(1);
	}

	/**
	 * 获取后置摄像头能力
	 * 
	 * @param context
	 * @return CameraInfo 数组
	 */
	public int[] getBackCameraInfo(Context context)
	{
		return getCameraInfo(0);
	}

	private int[] getCameraInfo(int index)
	{
		VideoCaptureDeviceInfoAndroid info = VideoCaptureDeviceInfoAndroid.CreateVideoCaptureDeviceInfoAndroid(index, context);
		if (null == info)
		{
			Log.w(TAG, "get deviceInfo android is null");
			return new int[] {};
		}
		CaptureCapabilityAndroid[] caps = info.GetCapabilityArray(info.GetDeviceUniqueName(index));
		if (null == caps)
		{
			Log.w(TAG, "capability is null");
			return new int[] {};
		}
		Log.i(TAG, "device info:" + info.GetDeviceUniqueName(index));
		int[] result = new int[caps.length];
		for (int i = 0; i < caps.length; i++)
		{
			result[i] = caps[i].width * caps[i].height;
			Log.i(TAG, "cameraIndex:" + index + "VRawType:" + caps[i].VRawType + ' ' + caps[i].width + '*' + caps[i].height + " MaxFps:" + caps[i].maxFPS);
		}
		return result;
	}

	/**
	 * 计算摄像头是否可以切换的能力
	 */
	private void calcCameraAbility()
	{
		int[] frontParams = getFrontCameraInfo();
		if (frontParams.length == 0)
		{
			cameraCapacity.put(FRONT_CAMERA, CAMERA_NON);
			Log.w(TAG, "can not found front camera capability");
		}

		int[] backParams = getBackCameraInfo(context);

		if (backParams.length == 0)
		{
			cameraCapacity.put(BACK_CAMERA, CAMERA_NON);
			Log.w(TAG, "can not found back camera capability , set the camera switch ability false");
			return;
		}

		int minParam = VideoCaps.FRAME_SIZE_WIDTH_HEIGHT[4][0] * VideoCaps.FRAME_SIZE_WIDTH_HEIGHT[4][1];// QVGA
		for (int i = 0; i < backParams.length; i++)
		{
			if (backParams[i] <= minParam)
			{
				cameraCapacity.put(BACK_CAMERA, CAMERA_NORMAL);
				Log.i(TAG, "found min than QVGA camera can switch");
				return;
			}
		}
		Log.i(TAG, "the phone core:" + PlatformInfo.getNumCores());
		if (PlatformInfo.getNumCores() == 1)
		{
			cameraCapacity.put(BACK_CAMERA, CAMERA_SCARCE_CAPACITY);
			Log.w(TAG, "the min framesize more than QVGA and core is 1 so camera cannot switch");
		}

	}

	/*************************************** 会议中视频相关 ***************************************/

	/*************************************** 会议中视频相关 ***************************************/

	/**
	 * 切换摄像头
	 */
	public boolean switchCamera()
	{
		// 切换摄像头时加入判断如果当前已经关闭了本地摄像头直接返回;
		boolean isCloseLocalCamera = videoCaps.isCloseLocalCamera();
		if (isCloseLocalCamera)
		{
			Log.e(TAG, "local Cameras is closed");
			return false;
		}

		if (numberOfCameras <= 1)
		{
			Log.e(TAG, "No More Cameras");
			return false;
		}

		CallLogic callPresenter = CallLogic.getInstance();
		// CVoip voip = CommonManager.getInstance().getVoip();
		if (callPresenter == null)
		{
			Log.e(TAG, "callPresenter Is Null");
			return false;
		}
		cameraIndex = (cameraIndex + 1) % numberOfCameras;
		// 设置后放入caps;
		videoCaps.setCameraIndex(cameraIndex);
		if (cameraIndex == BACK_CAMERA)
		{
			videoCaps.setCameraRotation(OrieantationUtil.getIns().calcCamOrieantation(cameraIndex));
			videoCaps.setLocalRoate(0);
		} else if (CallLogic.getInstance().getVoipStatus() == CallStatus.STATUS_VIDEOINIT)
		{
			videoCaps.setCameraRotation(OrieantationUtil.getIns().calcCamOrieantation(cameraIndex));
		}
		// 切换摄像头
		if (callPresenter.switchCamera(getCaps()))
		{
			Log.d(TAG, "Switch Success");
			// 当摄像头切换时要改变这个值 不然图像首次不会校准
			remoteTurnDirc += 1;
			if (cameraIndex == FRONT_CAMERA)
			{
				orientationChange(curTurnDegree);
			}
			return true;
		}
		Log.d(TAG, "Switch Fail");
		return false;
	}

	/**
	 * 清除数据
	 */
	public void clearCallVideo()
	{
		Log.d(TAG, "clearCallVideo() enter");

		uiHandler.post(new Runnable()
		{

			@Override
			public void run()
			{
				isInit = false;
				// 振铃崩溃，清除HMERender
				clearHMERendr();

				// 重置关闭摄像头标志;
				videoCaps.setIsCloseLocalCamera(false);
				cameraIndex = FRONT_CAMERA;
				// 重置摄像头为前置;
				videoCaps.setCameraIndex(cameraIndex);

				// 释放本地视频数据
				ViERenderer.FreeLocalRenderResource();
				ViERenderer.setSurfaceNullFromIndex(remoteCallIndex);
				ViERenderer.setSurfaceNullFromIndex(localCallIndex);
				ViERenderer.setSurfaceNullFromIndex(remoteBfcpIndex);
				ViERenderer.setSurfaceNullFromIndex(localBfcpIndex);
				if (localCallView != null)
				{
					ViERenderer.setSurfaceNull(localCallView);
					localCallView.setVisibility(View.GONE);
					localCallView = null;
				}
				if (remoteCallView != null)
				{
					// 释放远端视频数据
					ViERenderer.setSurfaceNull(remoteCallView);
					remoteCallView.setVisibility(View.GONE);
					remoteCallView = null;
				}
				if (localHideView != null)
				{
					localHideView.setVisibility(View.GONE);
					localHideView = null;
				}
				if (localBfcpView != null)
				{
					ViERenderer.setSurfaceNull(localBfcpView);
					localBfcpView.setVisibility(View.GONE);
					localBfcpView = null;
				}
				if (remoteBfcpView != null)
				{
					ViERenderer.setSurfaceNull(remoteBfcpView);
					remoteBfcpView.setVisibility(View.GONE);
					remoteBfcpView = null;
				}
				isVideoing = false;
			}

			/**
			 * 振铃崩溃，清除HMERender
			 */
			private void clearHMERendr()
			{
				// if
				// (ConfigAccount.getIns().getLoginAccount().isSupportVideo()) {
				addViewToContain(localCallView, remoteVideoView);
				if (null != remoteVideoView)
				{
					remoteVideoView.removeAllViews();
				}
				addViewToContain(remoteCallView, remoteVideoView);
				if (null != remoteVideoView)
				{
					remoteVideoView.removeAllViews();
				}
				addViewToContain(localBfcpView, remoteVideoView);
				if (null != remoteVideoView)
				{
					remoteVideoView.removeAllViews();
				}
				addViewToContain(remoteBfcpView, remoteVideoView);
				if (null != remoteVideoView)
				{
					remoteVideoView.removeAllViews();
				}
			}
		});
	}

	public boolean isVideoing()
	{
		return isVideoing;
	}

	public void setVideoing(boolean isVideo)
	{
		this.isVideoing = isVideo;
	}

	public SurfaceView getLocalHideView()
	{
		return localHideView;
	}

	public void setLocalHideView(SurfaceView localHideView)
	{
		this.localHideView = localHideView;
	}

	public SurfaceView getLocalCallView()
	{
		return localCallView;
	}

	public void setLocalCallView(SurfaceView lv)
	{
		this.localCallView = lv;
	}

	/**
	 * 得到大窗口的render 数据不一定是remote
	 */
	public SurfaceView getRemoteCallView()
	{
		return remoteCallView;
	}

	public void setRemoteCallView(SurfaceView rv)
	{
		this.remoteCallView = rv;
	}

	public int getCameraType()
	{
		return cameraIndex;
	}

	public void setCameraType(int type)
	{
		this.cameraIndex = type;
	}

	// 增加视频参数
	public VideoCaps getCaps()
	{
		return videoCaps;
	}

	public void setCaps(VideoCaps caps)
	{
		this.videoCaps = caps;
	}

	/**
	 * 能否切换摄像头 多个的时候
	 * 
	 * @return true 可以切换摄像头 false 不能切换摄像头
	 */
	public boolean isSwitchCameraAble()
	{
		return numberOfCameras > 1;
	}

	/**
	 * @return the dataCaps
	 */
	public VideoCaps getDataCaps()
	{
		return dataCaps;
	}

	/**
	 * @param dataCaps
	 *            the dataCaps to set
	 */
	public void setDataCaps(VideoCaps dataCaps)
	{
		this.dataCaps = dataCaps;
	}

	/**
	 * 是否支持视频通话
	 * 
	 * @return 至少存在一个摄像头的时候才支持视频
	 */
	public boolean isSupportVideo()
	{
		return numberOfCameras > 0;
	}

	/**
	 * 手机旋转
	 * 
	 * @param degree
	 */
	public void orientationChange(int degree)
	{
		// int orientation = getVideoChangeOrientation(degree,
		// VideoHandler.getIns().getCameraType() == 1);
		// if ((orientation == remoteTurnDirc) || orientation == -1 ||
		// VideoHandler.getIns().getCameraType() == VideoHandler.BACK_CAMERA) {
		// // 如果与上次一样不翻转
		// return;
		// }
		// curTurnDegree = degree;
		// remoteTurnDirc = orientation;
		// // 记录当前的角度 ，为规避 HME切换摄像头时调用旋转不起作用的问题
		// if (CallLogic.getIns().getVoipStatus() == CallLogic.STATUS_VIDEOING)
		// {
		// CommonManager.getInstance().getVoip().setCameraEx(degree,
		// cameraIndex);
		// }
	}

	/**
	 * 当设备检测不到旋转方向但是需要做旋转动作的时候调用
	 */
	public void orientationPreChange()
	{
		// 当音频转视频时要改变这个值 不然图像首次不会校准
		remoteTurnDirc += 1;
		orientationChange(curTurnDegree);
	}

	/**
	 * 通话结束还原标志
	 */
	public void resetTurnDirc()
	{
		remoteTurnDirc = -1;
		curTurnDegree = 0;
	}

	/**
	 * @param degree
	 * @param isfront
	 * @return
	 */
	private int getVideoChangeOrientation(int degree, boolean isfront)
	{
		// int currentCameraOrientation =
		// CommonManager.getInstance().getVoip().getCameraOrientation(VideoHandler.getIns().getCameraType());
		int resultDirc = -1;

		// 注意: 魅族手机与寻常手机不一致.需要分别做判断.正常手机采用下面方法即可
		// 以30度为基准
		// 0
		if (0 <= degree && degree < 60 || (330 <= degree && degree <= 360))
		{
			resultDirc = 0;
		}
		// 90
		else if (60 <= degree && degree <= 120)
		{
			resultDirc = 1;
		}
		// 180
		else if (150 <= degree && degree <= 210)
		{
			resultDirc = 2;
		}
		// 270
		else if (240 <= degree && degree <= 300)
		{
			resultDirc = 3;
		}
		return resultDirc;
	}

	// /**
	// * 设置各个视频窗口大小
	// *
	// * @param localRect
	// * @param remoteBigRect
	// * @param remoteSmallRect
	// */
	// public void setVideoViewRect(VideoViewRect localRect, VideoViewRect
	// remoteBigRect, VideoViewRect remoteSmallRect) {
	// localViewRect = localRect;
	// remoteBigViewRect = remoteBigRect;
	//
	// videoCaps.setLocalRenderRect(localViewRect);
	// }

	/**
	 * 设置目前使用的窗口
	 * 
	 * @param usingRemoteView
	 *            目前使用的窗口
	 * @since 1.1
	 * @history 2013-9-1 v1.0.0 z00199735 create
	 */
	public void setUseRemoteView(SurfaceView usingRemoteView)
	{
		// VideoViewRect remoteRect = null;
		if (remoteCallView.equals(usingRemoteView))
		{
			curUseRemoteRenderIndex = remoteCallIndex;
			// remoteRect = remoteBigViewRect;
		} else
		{
			Log.d(TAG, "setUseRemoteView Fail");
			return;
		}

		videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		// videoCaps.setRemoteRenderRect(remoteRect);
	}

	/**
	 * 获取当前角度
	 */
	public int getCurTurnDegree()
	{
		return curTurnDegree;
	}

	/**
	 * 底下事件刷新
	 * 
	 * @param isAdd
	 *            true添加
	 */
	public void refreshLocalHide(boolean isAdd1)
	{

		final boolean isAdd = isAdd1;

		uiHandler.post(new Runnable()
		{

			@Override
			public void run()
			{

				View localHI = VideoHandler.getIns().getLocalHideView();
				if (localHI == null)
				{
					Log.i(TAG, "localHI is null");
					return;
				}
				if (null == LocalHideRenderServer.getInstance())
				{
					Log.i(TAG, "localHideRenderServer is null");
					return;
				}
				if (!isAdd)
				{
					LocalHideRenderServer.getInstance().removeView(localHI);
				} else
				{
					LocalHideRenderServer.getInstance().addView(localHI);
					isRenderRemoveDone = true;

					// 刷新下view
					// reason：新增程序后台运行的时候关闭摄像头，回来的时候要打开，在开打的时候偶现本远端画面出现黑屏，只有在本远端切换的时候才能恢复
					if (null != VideoHandler.getIns().getRemoteCallView() && null != VideoHandler.getIns().getLocalCallView())
					{
						VideoHandler.getIns().getRemoteCallView().postInvalidate();
						VideoHandler.getIns().getLocalCallView().postInvalidate();
					}
				}

			}
		});
	}

	/**
	 * 本远端视频切换
	 */
	public void changeRender()
	{
		// if (isChanging) {// 如果已经在改变则返回,防止出现anr
		// Log.d(TAG, "change render not done");
		// return;
		// }
		// isChanging = true;
		// if (curLocalIndex == remoteCallIndex) {
		// curLocalIndex = localCallIndex;
		// } else {
		// curLocalIndex = remoteCallIndex;
		// }
		//
		// if (curUseRemoteRenderIndex == localCallIndex) {
		// curUseRemoteRenderIndex = remoteCallIndex;
		// } else {
		// curUseRemoteRenderIndex = localCallIndex;
		// }
		// // begin modify by cwx176935 reason: DTS2013103000918
		// // 软终端视频通话时，本地与远端视频切换时，软终端概率性异常退出
		// Executors.newSingleThreadExecutor().execute(new Runnable() {
		// @Override
		// public void run() {
		// videoCaps.setPlaybackLocal(curLocalIndex);
		// videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		// CommonManager.getInstance().getVoip().modifyRender(false);
		// Log.d(TAG, "change render");
		// isChanging = false;
		// }
		// });
		// // end modify by cwx176935 reason: DTS2013103000918
		// // 软终端视频通话时，本地与远端视频切换时，软终端概率性异常退出
	}

	/**
	 * 
	 * 还原本远端 render
	 * 
	 * @since 1.1
	 * @history 2013-9-23 v1.0.0 cWX176935 create
	 * @deprecated
	 */
	public void resetRender()
	{
		// if (curLocalIndex == remoteCallIndex) {
		// curLocalIndex = localCallIndex;
		// }
		//
		// if (curUseRemoteRenderIndex == localCallIndex) {
		// curUseRemoteRenderIndex = remoteCallIndex;
		// }
		// videoCaps.setPlaybackLocal(curLocalIndex);
		// videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		// CommonManager.getInstance().getVoip().modifyRender(false);
	}

	/**
	 * 预览辅流 是小窗口为远端
	 * 
	 * @since 1.1
	 * @history 2013-9-23 v1.0.0 cWX176935 create
	 * @deprecated
	 */
	public void changeLocaltoRemote()
	{
		// if (curLocalIndex == localCallIndex) {
		// curLocalIndex = remoteCallIndex;
		// } else {
		// curLocalIndex = localCallIndex;
		// }
		//
		// if (curUseRemoteRenderIndex == remoteCallIndex) {
		// curUseRemoteRenderIndex = localCallIndex;
		// } else {
		// curUseRemoteRenderIndex = remoteCallIndex;
		// }
		//
		// videoCaps.setPlaybackLocal(curLocalIndex);
		// videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		// CommonManager.getInstance().getVoip().modifyRender(false);
	}

	/**
	 * 
	 * 接收共享
	 * 
	 * @since 1.1
	 * @history 2013-9-30 v1.0.0 wWX183960 create
	 */
	public void remoteToBfcp()
	{
		// curUseRemoteRenderIndex = localCallIndex;
		// curLocalIndex = remoteCallIndex;
		//
		// videoCaps.setPlaybackLocal(curLocalIndex);
		// videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		//
		// dataCaps.setPlaybackLocal(localBfcpIndex);
		// dataCaps.setPlaybackRemote(remoteBfcpIndex);
		// CommonManager.getInstance().getVoip().modifyRender(true);
	}

	/**
	 * 是否初始化
	 * 
	 * @return true 已经初始化
	 * @since 1.1
	 * @history 2013-9-26 v1.0.0 cWX176935 create
	 */
	public boolean isInit()
	{
		return isInit;
	}

	/**
	 * 获取BFCPrender
	 * 
	 * @return
	 * @since 1.1
	 * @history 2013-9-30 v1.0.0 wWX183960 create
	 */
	public SurfaceView getRemoteBfcpView()
	{
		return remoteBfcpView;
	}

	public void setRemoteBfcpView(SurfaceView remoteBfcpView)
	{
		this.remoteBfcpView = remoteBfcpView;
	}

	// 视频通话中，TE30关闭本地视频，软终端显示对端残留的最后一帧图像
	/**
	 * 重新创建render 重新创建的render 需要重新设置ZMOrder 且需要重新添加，暂时不用
	 * 
	 * @param context
	 * @deprecated
	 */
	public void reCreateRemoteView(Context context)
	{
		if (null == remoteCallView)
		{
			Log.i(TAG, "remotecallview is null");
			return;
		}
		int oldRemoteIndex = curUseRemoteRenderIndex;
		SurfaceView newSufaceView = null;
		// 获取远端的视频视图
		newSufaceView = ViERenderer.CreateRenderer(context, PlatformInfo.getAndroidVersion() >= PlatformInfo.ANDROID_VER_3_0);
		newSufaceView.setZOrderOnTop(false);
		remoteCallIndex = ViERenderer.getIndexOfSurface(newSufaceView);
		curUseRemoteRenderIndex = remoteCallIndex;
		remoteCallView = newSufaceView;
		ViERenderer.setSurfaceNullFromIndex(oldRemoteIndex);
		videoCaps.setPlaybackRemote(curUseRemoteRenderIndex);
		Log.i(TAG, "now remote index:" + curUseRemoteRenderIndex + " clear remote index:" + oldRemoteIndex);
	}

	// 视频通话中，TE30关闭本地视频，软终端显示对端残留的最后一帧图像
	// 振铃崩溃，清除HMERender
	public void setRemoteVideoView(RelativeLayout remoteVideoView)
	{
		this.remoteVideoView = remoteVideoView;
	}

	private void addViewToContain(View videoView, ViewGroup videoContain)
	{
		if (null == videoView || null == videoContain)
		{
			Log.i(TAG, "Some Is Null");
			return;
		}
		ViewGroup container = (ViewGroup) videoView.getParent();
		videoView.setVisibility(View.GONE);
		videoContain.removeAllViews();
		if (null == container)
		{
			Log.i(TAG, "No Parent");
			videoContain.addView(videoView);
		} else if (!container.equals(videoContain))
		{
			container.removeView(videoView);
			Log.i(TAG, "Diferent Parent");
			videoContain.addView(videoView);
		} else
		{
			Log.i(TAG, "Same Parent");
		}
		videoContain.setVisibility(View.VISIBLE);
		videoView.setVisibility(View.VISIBLE);
	}

	/**
	 * 获取摄像头能力
	 * 
	 * @param cameraIndex
	 *            0后置，1前置
	 * @return -1无摄像头或坏的 0正常 1能力不够
	 */
	public int getCameraCapacty(int cameraIndex)
	{
		return cameraCapacity.get(cameraIndex);
	}

}
