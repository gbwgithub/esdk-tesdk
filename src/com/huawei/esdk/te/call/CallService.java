package com.huawei.esdk.te.call;

import java.util.List;

import object.StreamInfo;
import android.nfc.Tag;
import android.view.View;
import android.view.ViewGroup;

import com.huawei.esdk.te.util.LogUtil;
import com.huawei.esdk.te.video.VideoHandler;

public class CallService
{
	private static String TAG = CallService.class.getSimpleName();

	private static CallService instance = new CallService();

	public static CallService getInstance()
	{
		return instance;
	}

	public void registerNotification(CallNotification listener)
	{
		LogUtil.Log4Android("", TAG + "." + "registerNotification", "", "", "", "", "", "", listener.toString());
		LogUtil.i(TAG, "registerNotification()");
		LogUtil.i(TAG, "CallNotification listener -> " + listener);
		IpCallNotificationImpl.getInstance().registerNotification(listener);
	}

	public void unregisterNotification(CallNotification listener)
	{
		LogUtil.Log4Android("", TAG + "." + "registerNotification", "", "", "", "", "", "", listener.toString());
		LogUtil.i(TAG, "unregisterNotification()");
		LogUtil.i(TAG, "CallNotification listener -> " + listener);
		IpCallNotificationImpl.getInstance().unRegisterNotification(listener);
	}

	/**
	 * 发起呼叫
	 * 
	 * @param calleeNumber
	 *            被叫号码
	 * @param domain
	 *            域，暂时无用，可传空
	 * @param isVideoCall
	 *            是否视频通话
	 * @return CallErrorCode 成功："0" 失败：CallErrorCode.isFail(callCodeString)为true
	 */
	public String dialCall(String calleeNumber, String domain, boolean isVideoCall)
	{
		LogUtil.Log4Android("", TAG + "." + "dialCall", "", "", "", "", "", "", "calleeNumber -> " + calleeNumber + "  isVideoCall -> " + isVideoCall);
		LogUtil.i(TAG, "dialCall()");
		LogUtil.i(TAG, "calleeNumber -> " + calleeNumber);
		LogUtil.i(TAG, "domain -> " + domain);
		LogUtil.i(TAG, "isVideoCall -> " + isVideoCall);
		return CallLogic.getInstance().dialCall(calleeNumber, domain, isVideoCall);
	}

	/**
	 * 接听呼叫，接听一个呼叫，包括音，视频呼叫，返回接听是否成功
	 * 
	 * @param callId
	 *            接听会话的唯一标识callId
	 * @param isVideo
	 *            是否需要接入视频， 普通通话传入false
	 * @param context
	 *            上下文
	 * @return true表示成功，false表示失败
	 */
	public boolean callAnswer(String callId, boolean isVideo)
	{
		LogUtil.Log4Android("", TAG + "." + "callAnswer", "", "", "", "", "", "", "callId -> " + callId + "  isVideo -> " + isVideo);
		LogUtil.i(TAG, "callAnswer()");
		LogUtil.i(TAG, "callId -> " + callId);
		LogUtil.i(TAG, "isVideo -> " + isVideo);
		return CallLogic.getInstance().callAnswer(callId, isVideo);
	}

	/**
	 * 挂断来电呼叫
	 * 
	 * @param callId
	 *            来电的callId
	 * @return 执行拒绝呼叫结果， true 为成功
	 */
	public boolean rejectCall(String callId)
	{
		LogUtil.Log4Android("", TAG + "." + "rejectCall", "", "", "", "", "", "", "callId -> " + callId);
		LogUtil.i(TAG, "rejectCall()");
		LogUtil.i(TAG, "callId -> " + callId);
		return CallLogic.getInstance().rejectCall(callId);
	}

	/**
	 * 挂断呼叫
	 * 
	 * @return currentCallId是否为空 - 执行是否成功
	 */
	public synchronized boolean closeCall()
	{
		LogUtil.Log4Android("", TAG + "." + "closeCall", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "closeCall()");
		return CallLogic.getInstance().closeCall();
	}

	public boolean openBFCPReceive(ViewGroup localVideoView, ViewGroup remoteVideoView)
	{
		LogUtil.Log4Android("", TAG + "." + "openBFCPReceive", "", "", "", "", "", "", "localVideoView -> " + localVideoView + "  remoteVideoView -> " + remoteVideoView);
		LogUtil.i(TAG, "openBFCPReceive()");
		LogUtil.i(TAG, "localVideoView -> " + localVideoView);
		LogUtil.i(TAG, "remoteVideoView -> " + remoteVideoView);
		return CallLogic.getInstance().openBFCPReceive(localVideoView, remoteVideoView);
	}

	/**
	 * 把本地视频画面添加到界面布局中
	 * 
	 * @param preViewContain
	 *            包含本地视频render
	 */
	public void openLocalPreview(ViewGroup preViewContain)
	{
		LogUtil.Log4Android("", TAG + "." + "openLocalPreview", "", "", "", "", "", "", "localVideoView -> " + "preViewContain -> " + preViewContain);
		LogUtil.i(TAG, "openLocalPreview()");
		LogUtil.i(TAG, "preViewContain -> " + preViewContain);
		if (!VideoHandler.getIns().isInit())
		{
			VideoHandler.getIns().initCallVideo();
		}
		CallLogic.getInstance().addLocalRenderToContain(preViewContain);
	}

	/**
	 * 把视频画面填加到界面布局中
	 * 
	 * @param localView
	 *            包含本地render
	 * @param remoteView
	 *            包含远端render
	 * @param isLocal
	 *            true 本地最上面 false远端最上面
	 */
	public void openCallVideo(ViewGroup localViewContain, ViewGroup remoteViewContain, boolean isLocal)
	{
		LogUtil.Log4Android("", TAG + "." + "openCallVideo", "", "", "", "", "", "", "localViewContain -> " + localViewContain + "  remoteViewContain -> " + remoteViewContain + "  isLocal -> " + isLocal);
		LogUtil.i(TAG, "openCallVideo()");
		LogUtil.i(TAG, "localViewContain -> " + localViewContain);
		LogUtil.i(TAG, "remoteViewContain -> " + remoteViewContain);
		LogUtil.i(TAG, "isLocal -> " + isLocal);
		View remoteVV = VideoHandler.getIns().getRemoteCallView();
		if (null != remoteVV && null == remoteVV.getParent())
		{
			// 只有第一次进入视频通话的时候才去添加view，如果是视频参数更改之类的就不去做此操作
			VideoHandler.getIns().addRenderToContain(localViewContain, remoteViewContain, isLocal);
		}
	}

	/**
	 * 视频通话中本地摄像头开关接口
	 * 
	 * @return 执行完成
	 * @param isCloseAction
	 *            true表示关闭本地摄像头操作，false表示打开操作
	 */
	public boolean localCameraControl(boolean isCloseAction)
	{
		LogUtil.Log4Android("", TAG + "." + "localCameraControl", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "localCameraControl()");
		LogUtil.i(TAG, "isCloseAction -> " + isCloseAction);
		CallLogic.getInstance().setUserCloseLocalCamera(isCloseAction);
		return CallLogic.getInstance().localCameraControl(isCloseAction);
	}

	/**
	 * 切换前后摄像头
	 */
	public boolean switchCamera()
	{
		LogUtil.Log4Android("", TAG + "." + "switchCamera", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "switchCamera()");
		return VideoHandler.getIns().switchCamera();
	}

	/**
	 * 接受视频升级
	 * 
	 * @param caps
	 *            视频参数
	 * @return 执行结果 true 为执行成功 false 执行失败
	 */
	public boolean agreeUpgradeVideo()
	{
		LogUtil.Log4Android("", TAG + "." + "agreeUpgradeVideo", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "agreeUpgradeVideo()");
		return CallLogic.getInstance().agreeUpgradeVideo();
	}

	/**
	 * 拒绝视频升级
	 * 
	 * @return true / false 执行结果 true 执行成功 false 执行失败
	 */
	public boolean rejectUpgradeVideo()
	{
		LogUtil.Log4Android("", TAG + "." + "rejectUpgradeVideo", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "rejectUpgradeVideo()");
		return CallLogic.getInstance().rejectUpgradeVideo();
	}

	/**
	 * 音频通话转视频通话
	 * 
	 * @param caps
	 *            视频参数
	 * @return 执行结果 true 执行成功 false 执行失败
	 */
	public boolean upgradeVideo()
	{
		LogUtil.Log4Android("", TAG + "." + "upgradeVideo", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "upgradeVideo()");
		return CallLogic.getInstance().upgradeVideo();
	}

	/**
	 * 视频通话转音频通话
	 */
	public boolean closeVideo()
	{
		LogUtil.Log4Android("", TAG + "." + "closeVideo", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "closeVideo()");
		return CallLogic.getInstance().closeVideo();
	}

	/**
	 * 二次拨号
	 * 
	 * @param code
	 *            号码
	 */
	public boolean sendDTMF(String code)
	{
		LogUtil.Log4Android("", TAG + "." + "sendDTMF", "", "", "", "", "", "", "code -> " + code);
		LogUtil.i(TAG, "sendDTMF()");
		LogUtil.i(TAG, "code -> " + code);
		boolean ret = CallLogic.getInstance().reDial(code);
		return ret;
	}

	/**
	 * 静音本地麦克风
	 * 
	 * @param isRefer
	 *            是否会议中转移 true: 会议中转移， false：非会议中转移，对设备原来的静音状态取反。
	 * @param isMute
	 *            是否静音 true: 静音， false：取消静音
	 */
	public boolean setLocalMute(boolean isRefer, boolean isMute)
	{
		LogUtil.Log4Android("", TAG + "." + "setLocalMute", "", "", "", "", "", "", "isMute -> " + isMute);
		LogUtil.i(TAG, "setLocalMute()");
		LogUtil.i(TAG, "isRefer -> " + isRefer);
		LogUtil.i(TAG, "isMute -> " + isMute);
		return CallLogic.getInstance().setLocalMute(isRefer, isMute);
	}

	/**
	 * 扬声器静音
	 */
	public boolean oratorMute(boolean isMute)
	{
		LogUtil.Log4Android("", TAG + "." + "oratorMute", "", "", "", "", "", "", "isMute -> " + isMute);
		LogUtil.i(TAG, "oratorMute()");
		LogUtil.i(TAG, "isMute -> " + isMute);
		return CallLogic.getInstance().oratorMute(isMute);
	}

	/**
	 * 听筒和扬声器切换
	 */
	public boolean changeAudioRoute()
	{
		LogUtil.Log4Android("", TAG + "." + "changeAudioRoute", "", "", "", "", "", "", "");
		LogUtil.i(TAG, "changeAudioRoute()");
		return CallLogic.getInstance().changeAudioRoute();
	}

	/**
	 * 摄像头旋转角度设置
	 * 
	 * @param cameraRotation
	 *            设置摄像头采集角度（视频捕获角度）
	 * @param localRotation
	 *            设置本地图像显示角度
	 */
	public void setCameraDegree(int cameraRotation, int localRotation)
	{
		LogUtil.Log4Android("", TAG + "." + "setCameraDegree", "", "", "", "", "", "", "cameraRotation -> " + cameraRotation + "  localRotation -> " + localRotation);
		LogUtil.i(TAG, "setCameraDegree()");
		LogUtil.i(TAG, "cameraRotation -> " + cameraRotation);
		LogUtil.i(TAG, "localRotation -> " + localRotation);
		CallLogic.getInstance().setCameraDegree(cameraRotation, localRotation);
	}

	/**
	 * 向sdk层设置媒体带宽参数并使其生效 带宽等于64时禁用BFCP
	 */
	public boolean setBandwidth(int bw)
	{
		LogUtil.Log4Android("", TAG + "." + "setBandwidth", "", "", "", "", "", "", "cameraRotation -> " + "bw -> " + bw);
		LogUtil.i(TAG, "setBandwidth()");
		LogUtil.i(TAG, "bw -> " + bw);
		return CallLogic.getInstance().setFastBandwidth(bw);
	}

	/**
	 * 设置视频模式 流畅优先 -> VIDEO_PROCESS_MODE = 1 画质优先 -> VIDEO_QUALITY_MODE = 0
	 */
	public boolean setVideoMode(int videoMode)
	{
		LogUtil.Log4Android("", TAG + "." + "setVideoMode", "", "", "", "", "", "", "cameraRotation -> " + "videoMode -> " + videoMode);
		LogUtil.i(TAG, "setVideoMode()");
		LogUtil.i(TAG, "videoMode -> " + videoMode);
		return VideoHandler.getIns().setVideoMode(videoMode);
	}

	/**
	 * 设置SRTP，安全传输协议：2：加密 3:非强制性加密(最大互通性) 1：不加密
	 */
	public boolean setEncryptMode(int encryptMode)
	{
		LogUtil.i(TAG, "setEncryptMode()");
		LogUtil.i(TAG, "encryptMode -> " + encryptMode);
		return VideoHandler.getIns().setEncryptMode(encryptMode);
	}

	/**
	 * 强制关闭所有通话
	 */
	public void forceCloseCall()
	{
		LogUtil.i(TAG, "forceCloseCall()");
		CallLogic.getInstance().forceCloseCall();
	}

	/**
	 * 获取当前通话媒体信息
	 */
	public StreamInfo getMediaInfo()
	{
		return CallLogic.getInstance().getMediaInfo();
	}

	/**
	 * 获取当前通话状态
	 */
	public int getVoipStatus()
	{
		return CallLogic.getInstance().getVoipStatus();
	}

	/**
	 * 获取当前CallID
	 */
	public String getCurrentCallID()
	{
		return CallLogic.getInstance().getCurrentCallID();
	}

	public String getBfcpStatus()
	{
		return CallLogic.getInstance().getBfcpStatus();
	}

	public String getCallNumber()
	{
		return CallLogic.getInstance().getCallNumber();
	}

	/**
	 * 获取当前支持的音频路由 第一个为正在使用的音频路由
	 */
	public List<Integer> getAudioRouteList()
	{
		return CallLogic.getInstance().getAudioRouteList();
	}

	/**
	 * 获取当前通话类型
	 */
	public boolean isVideoCall()
	{
		return CallLogic.getInstance().isVideoCall();
	}

	public boolean isCallClosed()
	{
		return CallLogic.getInstance().isCallClosed();
	}

	public boolean isEnableBfcp()
	{
		return CallLogic.getInstance().isEnableBfcp();
	}

}
