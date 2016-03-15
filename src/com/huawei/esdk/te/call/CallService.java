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

package com.huawei.esdk.te.call;

import android.view.View;
import android.view.ViewGroup;

import com.huawei.esdk.te.util.LogUtil;
import com.huawei.esdk.te.video.VideoHandler;

import java.util.List;

import object.StreamInfo;

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
		LogUtil.in();
//		 LogUtil.log4Android("", TAG + "." + "registerNotification", "", "",
//		 "", "", "", "", listener.toString());
		LogUtil.i(TAG, "registerNotification()");
		LogUtil.i(TAG, "CallNotification listener -> " + listener);
		IpCallNotificationImpl.getInstance().registerNotification(listener);
		LogUtil.out("", "listener=" + listener);
	}

	public void unregisterNotification(CallNotification listener)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "unregisterNotification", "", "",
		// "", "", "", "", listener.toString());
		LogUtil.i(TAG, "unregisterNotification()");
		LogUtil.i(TAG, "CallNotification listener -> " + listener);
		IpCallNotificationImpl.getInstance().unRegisterNotification(listener);
		LogUtil.out("", "listener=" + listener);
	}

	/**
	 * 发起呼叫
	 *
	 * @param calleeNumber 被叫号码
	 * @param domain       域，暂时无用，可传空
	 * @param isVideoCall  是否视频通话
	 * @return CallErrorCode 成功："0" 失败：CallErrorCode.isFail(callCodeString)为true
	 */
	public String dialCall(String calleeNumber, String domain, boolean isVideoCall)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "dialCall", "", "", "", "", "",
		// "", "calleeNumber -> " + calleeNumber + "  isVideoCall -> " +
		// isVideoCall);
		LogUtil.i(TAG, "dialCall()");
		LogUtil.i(TAG, "calleeNumber -> " + calleeNumber);
		LogUtil.i(TAG, "domain -> " + domain);
		LogUtil.i(TAG, "isVideoCall -> " + isVideoCall);
		String ret = CallLogic.getInstance().dialCall(calleeNumber, domain, isVideoCall);
		LogUtil.out(ret, "calleeNumber=" + calleeNumber + "  isVideoCall=" + isVideoCall);
		return ret;
	}

	/**
	 * 接听呼叫，接听一个呼叫，包括音，视频呼叫，返回接听是否成功
	 *
	 * @param callId  接听会话的唯一标识callId
	 * @param isVideo 是否需要接入视频， 普通通话传入false
	 * @return true表示成功，false表示失败
	 */
	public boolean callAnswer(String callId, boolean isVideo)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "callAnswer", "", "", "", "", "",
		// "", "callId -> " + callId + "  isVideo -> " + isVideo);
		LogUtil.i(TAG, "callAnswer()");
		LogUtil.i(TAG, "callId -> " + callId);
		LogUtil.i(TAG, "isVideo -> " + isVideo);
		boolean ret = CallLogic.getInstance().callAnswer(callId, isVideo);
		LogUtil.out(Boolean.valueOf(ret).toString(), "callId=" + callId + "  isVideo=" + isVideo);
		return ret;
	}

	/**
	 * 挂断来电呼叫
	 *
	 * @param callId 来电的callId
	 * @return 执行拒绝呼叫结果， true 为成功
	 */
	public boolean rejectCall(String callId)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "rejectCall", "", "", "", "", "",
		// "", "callId -> " + callId);
		LogUtil.i(TAG, "rejectCall()");
		LogUtil.i(TAG, "callId -> " + callId);
		boolean ret = CallLogic.getInstance().rejectCall(callId);
		LogUtil.out(Boolean.valueOf(ret).toString(), "callId=" + callId);
		return ret;
	}

	/**
	 * 挂断呼叫
	 *
	 * @return currentCallId是否为空 - 执行是否成功
	 */
	public synchronized boolean closeCall()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "closeCall", "", "", "", "", "",
		// "", "");
		LogUtil.i(TAG, "closeCall()");
		LogUtil.out("", "");
		return CallLogic.getInstance().closeCall();
	}

	public boolean openBFCPReceive(ViewGroup localVideoView, ViewGroup remoteVideoView)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "openBFCPReceive", "", "", "",
		// "", "", "", "localVideoView -> " + localVideoView +
		// "  remoteVideoView -> "
		// + remoteVideoView);
		LogUtil.i(TAG, "openBFCPReceive()");
		LogUtil.i(TAG, "localVideoView -> " + localVideoView);
		LogUtil.i(TAG, "remoteVideoView -> " + remoteVideoView);
		boolean ret = CallLogic.getInstance().openBFCPReceive(localVideoView, remoteVideoView);
		LogUtil.out(Boolean.valueOf(ret).toString(), "localVideoView=" + localVideoView + "  remoteVideoView=" + remoteVideoView);
		return ret;
	}

	/**
	 * 把本地视频画面添加到界面布局中
	 *
	 * @param preViewContain 包含本地视频render
	 */
	public void openLocalPreview(ViewGroup preViewContain)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "openLocalPreview", "", "", "",
		// "", "", "", "localVideoView -> " + "preViewContain -> " +
		// preViewContain);
		LogUtil.i(TAG, "openLocalPreview()");
		LogUtil.i(TAG, "preViewContain -> " + preViewContain);
		if (!VideoHandler.getIns().isInit())
		{
			VideoHandler.getIns().initCallVideo();
		}
		CallLogic.getInstance().addLocalRenderToContain(preViewContain);
		LogUtil.out("", "preViewContain=" + preViewContain);
	}

	/**
	 * 把视频画面填加到界面布局中
	 *
	 * @param localViewContain  包含本地render
	 * @param remoteViewContain 包含远端render
	 * @param isLocal           true 本地最上面 false远端最上面
	 */
	public void openCallVideo(ViewGroup localViewContain, ViewGroup remoteViewContain, boolean isLocal)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "openCallVideo", "", "", "", "",
		// "", "", "localViewContain -> " + localViewContain +
		// "  remoteViewContain -> "
		// + remoteViewContain + "  isLocal -> " + isLocal);
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
		LogUtil.out("", "localViewContain=" + localViewContain + "  remoteViewContain=" + remoteViewContain + "  isLocal=" + isLocal);
	}

	/**
	 * 视频通话中本地摄像头开关接口
	 *
	 * @param isCloseAction true表示关闭本地摄像头操作，false表示打开操作
	 * @return 执行完成
	 */
	public boolean localCameraControl(boolean isCloseAction)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "localCameraControl", "", "", "",
		// "", "", "", "");
		LogUtil.i(TAG, "localCameraControl()");
		LogUtil.i(TAG, "isCloseAction -> " + isCloseAction);
		CallLogic.getInstance().setUserCloseLocalCamera(isCloseAction);
		boolean ret = CallLogic.getInstance().localCameraControl(isCloseAction);
		LogUtil.out(Boolean.valueOf(ret).toString(), "isCloseAction=" + isCloseAction);
		return ret;
	}

	/**
	 * 切换前后摄像头
	 */
	public boolean switchCamera()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "switchCamera", "", "", "", "",
		// "", "", "");
		LogUtil.i(TAG, "switchCamera()");
		boolean ret = VideoHandler.getIns().switchCamera();
		LogUtil.out(Boolean.valueOf(ret).toString(), "");
		return ret;
	}

	/**
	 * 接受视频升级
	 *
	 * @return 执行结果 true 为执行成功 false 执行失败
	 */
	public boolean agreeUpgradeVideo()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "agreeUpgradeVideo", "", "", "",
		// "", "", "", "");
		LogUtil.i(TAG, "agreeUpgradeVideo()");
		boolean ret = CallLogic.getInstance().agreeUpgradeVideo();
		LogUtil.out(Boolean.valueOf(ret).toString(), "");
		return ret;
	}

	/**
	 * 拒绝视频升级
	 *
	 * @return true / false 执行结果 true 执行成功 false 执行失败
	 */
	public boolean rejectUpgradeVideo()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "rejectUpgradeVideo", "", "", "",
		// "", "", "", "");
		LogUtil.i(TAG, "rejectUpgradeVideo()");
		boolean ret = CallLogic.getInstance().rejectUpgradeVideo();
		LogUtil.out(Boolean.valueOf(ret).toString(), "");
		return ret;
	}

	/**
	 * 音频通话转视频通话
	 *
	 * @return 执行结果 true 执行成功 false 执行失败
	 */
	public boolean upgradeVideo()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "upgradeVideo", "", "", "", "",
		// "", "", "");
		LogUtil.i(TAG, "upgradeVideo()");
		boolean ret = CallLogic.getInstance().upgradeVideo();
		LogUtil.out(Boolean.valueOf(ret).toString(), "");
		return ret;
	}

	/**
	 * 视频通话转音频通话
	 */
	public boolean closeVideo()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "closeVideo", "", "", "", "", "",
		// "", "");
		LogUtil.i(TAG, "closeVideo()");
		boolean ret = CallLogic.getInstance().closeVideo();
		LogUtil.out(Boolean.valueOf(ret).toString(), "");
		return ret;
	}

	/**
	 * 二次拨号
	 *
	 * @param code 号码
	 */
	public boolean sendDTMF(String code)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "sendDTMF", "", "", "", "", "",
		// "", "code -> " + code);
		LogUtil.i(TAG, "sendDTMF()");
		LogUtil.i(TAG, "code -> " + code);
		boolean ret = CallLogic.getInstance().reDial(code);
		LogUtil.out(Boolean.valueOf(ret).toString(), "code=" + code);
		return ret;
	}

	/**
	 * 静音本地麦克风
	 *
	 * @param isRefer 是否会议中转移 true: 会议中转移， false：非会议中转移，对设备原来的静音状态取反。
	 * @param isMute  是否静音 true: 静音， false：取消静音
	 */
	public boolean setLocalMute(boolean isRefer, boolean isMute)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "setLocalMute", "", "", "", "",
		// "", "", "isMute -> " + isMute);
		LogUtil.i(TAG, "setLocalMute()");
		LogUtil.i(TAG, "isRefer -> " + isRefer);
		LogUtil.i(TAG, "isMute -> " + isMute);
		boolean ret = CallLogic.getInstance().setLocalMute(isRefer, isMute);
		LogUtil.out(Boolean.valueOf(ret).toString(), "isMute=" + isMute);
		return ret;
	}

	/**
	 * 扬声器静音
	 */
	public boolean oratorMute(boolean isMute)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "oratorMute", "", "", "", "", "",
		// "", "isMute -> " + isMute);
		LogUtil.i(TAG, "oratorMute()");
		LogUtil.i(TAG, "isMute -> " + isMute);
		boolean ret = CallLogic.getInstance().oratorMute(isMute);
		LogUtil.out(Boolean.valueOf(ret).toString(), "isMute=" + isMute);
		return ret;
	}

	/**
	 * 听筒和扬声器切换
	 */
	public boolean changeAudioRoute()
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "changeAudioRoute", "", "", "",
		// "", "", "", "");
		LogUtil.i(TAG, "changeAudioRoute()");
		boolean ret = CallLogic.getInstance().changeAudioRoute();
		LogUtil.out(Boolean.valueOf(ret).toString(), "");
		return ret;
	}

	/**
	 * 摄像头旋转角度设置
	 *
	 * @param cameraRotation 设置摄像头采集角度（视频捕获角度）
	 * @param localRotation  设置本地图像显示角度
	 */
	public void setCameraDegree(int cameraRotation, int localRotation)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "setCameraDegree", "", "", "",
		// "", "", "", "cameraRotation -> " + cameraRotation +
		// "  localRotation -> "
		// + localRotation);
		LogUtil.i(TAG, "setCameraDegree()");
		LogUtil.i(TAG, "cameraRotation -> " + cameraRotation);
		LogUtil.i(TAG, "localRotation -> " + localRotation);
		CallLogic.getInstance().setCameraDegree(cameraRotation, localRotation);
		LogUtil.out("", "cameraRotation=" + cameraRotation + "  localRotation=" + localRotation);
	}

	/**
	 * 向sdk层设置媒体带宽参数并使其生效 带宽等于64时禁用BFCP
	 */
	public boolean setBandwidth(int bw)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "setBandwidth", "", "", "", "",
		// "", "", "cameraRotation -> " + "bw -> " + bw);
		LogUtil.i(TAG, "setBandwidth()");
		LogUtil.i(TAG, "bw -> " + bw);
		boolean ret = CallLogic.getInstance().setFastBandwidth(bw);
		LogUtil.out(Boolean.valueOf(ret).toString(), "cameraRotation=" + "bw=" + bw);
		return ret;
	}

	/**
	 * 设置视频模式 流畅优先 -> VIDEO_PROCESS_MODE = 1 画质优先 -> VIDEO_QUALITY_MODE = 0
	 */
	public boolean setVideoMode(int videoMode)
	{
		LogUtil.in();
		// LogUtil.log4Android("", TAG + "." + "setVideoMode", "", "", "", "",
		// "", "", "cameraRotation -> " + "videoMode -> " + videoMode);
		LogUtil.i(TAG, "setVideoMode()");
		LogUtil.i(TAG, "videoMode -> " + videoMode);
		boolean ret = VideoHandler.getIns().setVideoMode(videoMode);
		LogUtil.out(Boolean.valueOf(ret).toString(), "cameraRotation=" + "videoMode=" + videoMode);
		return ret;
	}

	/**
	 * 设置SRTP，安全传输协议：2：加密 3:非强制性加密(最大互通性) 1：不加密
	 */
	public boolean setEncryptMode(int encryptMode)
	{
		LogUtil.in();
		LogUtil.i(TAG, "setEncryptMode()");
		LogUtil.i(TAG, "encryptMode -> " + encryptMode);
		boolean ret = VideoHandler.getIns().setEncryptMode(encryptMode);
		LogUtil.out(Boolean.valueOf(ret).toString(), "encryptMode=" + encryptMode);
		return ret;
	}

	/**
	 * 强制关闭所有通话
	 */
	public void forceCloseCall()
	{
		LogUtil.in();
		LogUtil.i(TAG, "forceCloseCall()");
		CallLogic.getInstance().forceCloseCall();
		LogUtil.out("", "");
	}

	/**
	 * 获取当前通话媒体信息
	 */
	public StreamInfo getMediaInfo()
	{
		LogUtil.in();
		StreamInfo ret = CallLogic.getInstance().getMediaInfo();
		if (null != ret)
			LogUtil.out(ret.toString(), "");
		return ret;
	}

	/**
	 * 获取当前通话状态
	 */
	public int getVoipStatus()
	{
		LogUtil.in();
		int ret = CallLogic.getInstance().getVoipStatus();
		LogUtil.out("" + ret, "");
		return ret;
	}
	//TO test commit on Android Studio .

	/**
	 * 获取当前CallID
	 */
	public String getCurrentCallID()
	{
		LogUtil.in();
		String ret = CallLogic.getInstance().getCurrentCallID();
		LogUtil.out(ret, "");
		return ret;
	}

	public String getBfcpStatus()
	{
		LogUtil.in();
		String ret = CallLogic.getInstance().getBfcpStatus();
		LogUtil.out(ret, "");
		return ret;
	}

	public String getCallNumber()
	{
		LogUtil.in();
		String ret = CallLogic.getInstance().getCallNumber();
		LogUtil.out(ret, "");
		return ret;
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
