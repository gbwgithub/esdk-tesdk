package com.huawei.esdk.te.call;

import com.huawei.esdk.te.video.VideoHandler;

import android.view.View;
import android.view.ViewGroup;
import object.StreamInfo;

public class CallService
{

	private static CallService instance = new CallService();

	public static CallService getInstance()
	{
		return instance;
	}

	public void registerNotification(CallNotification listener)
	{
		IpCallNotificationImpl.getInstance().registerNotification(listener);
	}

	public void unregisterNotification(CallNotification listener)
	{
		IpCallNotificationImpl.getInstance().unRegisterNotification(listener);
	}

//	/**
//	 * 初始化点对点视频通话中视频数据
//	 */
//	public void initCallVideo()
//	{
//		if (!VideoHandler.getIns().isInit())
//		{
//			VideoHandler.getIns().initCallVideo();
//		}
//	}

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
		return CallLogic.getInstance().rejectCall(callId);
	}

	/**
	 * 挂断呼叫
	 * 
	 * @return currentCallId是否为空 - 执行是否成功
	 */
	public synchronized boolean closeCall()
	{
		return CallLogic.getInstance().closeCall();
	}
	
	/**
	 * 把本地视频画面添加到界面布局中
	 * 
	 * @param preViewContain
	 *            包含本地视频render
	 */
	public void openLocalPreview(ViewGroup preViewContain)
	{
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

		View remoteVV = VideoHandler.getIns().getRemoteCallView();
		if (null != remoteVV && null == remoteVV.getParent())
		{
			// 只有第一次进入视频通话的时候才去添加view，如果是视频参数更改之类的就不去做此操作
			CallLogic.getInstance().addRenderToContain(localViewContain, remoteViewContain, isLocal);
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
		return CallLogic.getInstance().localCameraControl(isCloseAction);
	}

	/**
	 * 切换前后摄像头
	 */
	public boolean switchCamera()
	{
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
		return CallLogic.getInstance().agreeUpgradeVideo();
	}

	/**
	 * 拒绝视频升级
	 * 
	 * @return true / false 执行结果 true 执行成功 false 执行失败
	 */
	public boolean rejectUpgradeVideo()
	{
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
		return CallLogic.getInstance().upgradeVideo();
	}
	
	/**
	 * 视频通话转音频通话
	 */
	public boolean closeVideo()
	{
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
		return CallLogic.getInstance().setLocalMute(isRefer, isMute);
	}

    /**
     * 扬声器静音
     */
    public boolean oratorMute(boolean isMute)
    {
        return CallLogic.getInstance().oratorMute(isMute);
    }

    
    
    
	/**
	 * 强制关闭所有通话
	 */
	public void forceCloseCall()
	{
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

//	/**
//	 * 获取视频是否初始化
//	 */
//	public boolean isVideoInit()
//	{
//		return VideoHandler.getIns().isInit();
//	}

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
