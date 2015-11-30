package com.huawei.esdk.te.call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import object.StreamInfo;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.huawei.common.CallErrorCode;
import com.huawei.esdk.te.call.CallConstants.BFCPStatus;
import com.huawei.esdk.te.call.CallConstants.CallStatus;
import com.huawei.esdk.te.call.CallConstants.CallType;
import com.huawei.esdk.te.data.Constants;
import com.huawei.esdk.te.util.LayoutUtil;
import com.huawei.esdk.te.util.MediaUtil;
import com.huawei.esdk.te.util.OrieantationUtil;
import com.huawei.esdk.te.video.LocalHideRenderServer;
import com.huawei.esdk.te.video.VideoHandler;
import com.huawei.service.ServiceProxy;
import com.huawei.utils.StringUtil;
import com.huawei.voip.CallManager;
import com.huawei.voip.CallSession;
import com.huawei.voip.data.CallCommandParams;
import com.huawei.voip.data.CallCommands;
import com.huawei.voip.data.CameraViewRefresh;
import com.huawei.voip.data.EarpieceMode;
import com.huawei.voip.data.EventData;
import com.huawei.voip.data.SessionBean;
import com.huawei.voip.data.VideoCaps;

public class CallLogic
{

	private static final String TAG = "CallLogic";

	/**
	 * render控制锁
	 */
	private static final Object RENDER_CHANGE_LOCK = new Object();

	/** 语音通话添加视频（被叫4112） **/
	private static final String VIDEOADD = "add";

	/** 语音通话添加视频（主叫4113） **/
	private static final String VIDEOMOD = "mod";

	private static CallLogic instance;
	/**
	 * 唯一的 CallManager对象， 和 CVOIP eSpaceService 生命周期同步
	 */
	private CallManager callManager;

	/**
	 * 是否需要清空视频数据,用于提供给上层Demo，做判断接听状态，以清除预览数据的依据
	 */
	private boolean isNeedClearVideo = false;

	/**
	 * BFCP 状态
	 */
	private String bfcpStatus = null;
	/**
	 * BFCP能力状态
	 */
	private boolean isEnableBfcp = false;

	// 添加辅流失败错误码的判断
	/**
	 * BFCP失败错误码
	 */
	private String bfcpErrorCode;

	/**
	 * 呼叫类型
	 */
	private int callType;

	/**
	 * voip语音界面的状态,默认初始状态即挂断
	 */
	private int voipStatus = CallStatus.STATUS_CLOSE;

	/**
	 * 当前呼叫号码
	 */
	private String callNumber = "";

	/**
	 * 计时的初始时间
	 */
	private long beginTime = 0;

	/**
	 * 是否是一个视频呼叫
	 */
	private boolean isVideoCall = false;

	/**
	 * 系统是否处在静音，在拨号的时候第一次初始化
	 */
	private static boolean isSlient = false;

	/**
	 * 本地麦克风是否静音
	 */
	private boolean microphoneMute = false;

	public boolean isMicrophoneMute()
	{
		return microphoneMute;
	}

	public void setMicrophoneMute(boolean microphoneMute)
	{
		this.microphoneMute = microphoneMute;
	}

	/**
	 * 扬声器静音
	 */
	private boolean oratorMute = false;

	public boolean isOratorMute()
	{
		return oratorMute;
	}

	public void setOratorMute(boolean oratorMute)
	{
		this.oratorMute = oratorMute;
	}

	/**
	 * 设置是否是一个视频呼叫
	 * 
	 * @param isVideocall
	 *            是否是一个视频呼叫
	 */
	public void setVideoCall(boolean isVideocall)
	{
		this.isVideoCall = isVideocall;
	}

	// 添加辅流失败错误码的判断
	public String getBfcpErrorCode()
	{
		return bfcpErrorCode;
	}

	public void setBfcpErrorCode(String bfcpErrorCode)
	{
		this.bfcpErrorCode = bfcpErrorCode;
	}

	/**
	 * 获取是否为一个视频呼叫
	 * 
	 * @return the isVideoCall
	 */
	public boolean isVideoCall()
	{
		return isVideoCall;
	}

	public boolean isEnableBfcp()
	{
		return isEnableBfcp;
	}

	public void setEnableBfcp(boolean enableBfcp)
	{
		this.isEnableBfcp = enableBfcp;
	}

	public static boolean isAtSlient()
	{
		return isSlient;
	}

	public static void setSlient(boolean isAtSlient)
	{
		isSlient = isAtSlient;
	}

	public String getCallNumber()
	{
		return callNumber;
	}

	public void setCallNumber(String callNumber)
	{
		this.callNumber = callNumber;
	}

	public int getVoipStatus()
	{
		return voipStatus;
	}

	public void setVoipStatus(int voipStatus)
	{
		this.voipStatus = voipStatus;
	}

	public long getBeginTime()
	{
		return beginTime;
	}

	public void setBeginTime(long beginTime)
	{
		this.beginTime = beginTime;
	}

	public int getCallType()
	{
		return callType;
	}

	public void setCallType(int callType)
	{
		this.callType = callType;
	}

	public String getBfcpStatus()
	{
		return bfcpStatus;
	}

	public void setBfcpStatus(String bfcpStatus)
	{
		this.bfcpStatus = bfcpStatus;
	}

	/**
	 * 清空临时数据
	 */
	public void reset()
	{
		callNumber = "";
		voipStatus = CallStatus.STATUS_CLOSE;
		beginTime = 0;
		isVideoCall = false;
	}

	/*
	 * 支持的音频路由列表
	 */
	private List<Integer> supportAudioRouteList = new ArrayList<Integer>(0);

	// 用于保存callid和callsession对应MAP
	private HashMap<String, SessionBean> callSessionMap = new HashMap<String, SessionBean>(0);

	/**
	 * Call ID
	 */
	private String currentCallID = null;

	public String getCurrentCallID()
	{
		return currentCallID;
	}

	/**
	 * 用于SDK对外提供currentCallID使用，由外部负责管理
	 */
	private String curSDKCallID = null;

	public String getCurSDKCallID()
	{
		return curSDKCallID;
	}

	public void setCurSDKCallID(String curSDKCallID)
	{
		this.curSDKCallID = curSDKCallID;
	}

	// 增加用于保存呼叫进来的ID
	private String comingCallID = null;

	public static CallLogic getInstance()
	{
		return instance;
	}

	// 用于保存callid和recordID对应MAP -- recordID 来自于instert数据库的操作，后期再看看这个
	// callRecordMap应该是可以删除。
	private Map<String, Integer> callRecordMap = new HashMap<String, Integer>(0);

	/**
	 * 视频升级请求是否已拒绝
	 */
	private boolean isCanceled = false;

	/**
	 * 对象同步锁
	 */
	private static final Object PREVIEWLOCK = new Object();
	/**
	 * 创建或更新窗口类型-本地窗口
	 */
	private int typeLocal = 1;

	/**
	 * 创建或更新窗口类型-远端窗口
	 */
	private int typeRemote = 0;

	/**
	 * 创建或更新窗口类型-辅流窗口
	 */
	private int typeBfcp = 3;
	/**
	 * 呼叫操作锁
	 */
	private final byte[] LOCK_CALL_OPERATION = new byte[0];
	/**
	 * 保存mService ， 保证 callManager 对象在每一次Service创建时候初始化。 不会出现对象不一致
	 */
	private ServiceProxy mService;

	/**
	 * 初始化参数
	 * 
	 * @param service
	 *            服务代理
	 */
	public CallLogic(ServiceProxy service)
	{
		instance = this;
		this.mService = service;
		boolean mServiceNotNull = (mService != null) && (mService.getCallManager() != null);
		if (mServiceNotNull)
		{
			callManager = mService.getCallManager();
			if (null != callManager)
			{
				callManager.registerNofitication(IpCallNotificationImpl.getInstance());
			}
		} else
		{
			Log.w(TAG, "mService is null !");
		}

		addDefaultAudioRoute();

	}

	// public synchronized void registerNotification(CallNotification listener)
	// {
	// if (listener != null && !mCallNotificationListeners.contains(listener))
	// mCallNotificationListeners.add(listener);
	// }
	//
	// public synchronized void unRegisterNofitication(CallNotification
	// listener)
	// {
	// if (null == mCallNotificationListeners)
	// {
	// return;
	// }
	// if (listener == null)
	// {
	// Log.d(TAG, "CallManager remove all CallBack");
	// mCallNotificationListeners.clear();
	// } else
	// {
	// mCallNotificationListeners.remove(listener);
	// }
	// if (!mCallNotificationListeners.isEmpty())
	// return;
	// }

	/**
	 * 发起呼叫
	 * 
	 * @param fromPhone
	 *            呼叫号码
	 * @param domain
	 *            域，暂时无用，可传空
	 * @param isVideoCall
	 *            是否视频通话
	 * @return CallErrorCode 成功："0" 失败：CallErrorCode.isFail(callCodeString)为true
	 */
	public synchronized String dialCall(String fromPhone, String domain, final boolean isVideoCall)
	{

		// 设置VideoCaps
		VideoCaps vcaps = null;
		VideoCaps dataCaps = null;
		if (isVideoCall)
		{
			// 初始化视频参数
			// VideoHandler.getIns().initCallVideo();
			// vcaps = VideoHandler.getIns().getCaps();
			// To invoke
			vcaps = VideoHandler.getIns().initCallVideo();
			dataCaps = VideoHandler.getIns().getDataCaps();
		}

		final String callRet = dialCall(fromPhone, domain, isVideoCall, vcaps, dataCaps);

		if (!CallErrorCode.isFail(callRet))
		{
			return callRet;
		}
		Log.i(TAG, "call ret is error");
		if (isVideoCall)
		{
			VideoHandler.getIns().clearCallVideo();
		}
		// ARMv6不支持任何呼叫
		if (CallErrorCode.CALL_ERROR_AMRV6.equals(callRet))
		{
			Log.e(TAG, "Your device CPU uses the ARMv6 architecture and does not support calling currently");
			return callRet;
		}
		Log.e(TAG, "Call failed");

		return callRet;
	}

	/**
	 * 发起呼叫
	 * 
	 * @param fromPhone
	 *            呼叫号码
	 * @param domain
	 *            域，暂时无用，可传空
	 * @param isVideoCall
	 *            是否视频通话
	 * @param vcaps
	 *            视频通话时，需设置的视频参数，
	 * @param dataCaps
	 *            bfcp参数
	 * @return CallErrorCode 成功："0" 失败：CallErrorCode.isFail(callCodeString)为true
	 */
	private synchronized String dialCall(String fromPhone, String domain, boolean isVideoCall, final VideoCaps vcaps, VideoCaps dataCaps)
	{

		if (CallStatus.STATUS_CLOSE != getVoipStatus())
		{
			Log.e(TAG, "dialCall() failed --- getVoipStatus not in close status");
			return CallErrorCode.CALL_ERROR_FAILURE;
		}

		setVideoCall(isVideoCall);
		setVoipStatus(CallStatus.STATUS_CALLING);

		if (isVideoCall)
		{
			setVoipStatus(CallStatus.STATUS_VIDEOINIT);
			callManager.operateVideoWindow(typeLocal, vcaps.getPlaybackLocal(), null, VideoCaps.DISPLAY_TYPE.DISPLAY_TYPE_CLIPPING);
			callManager.operateVideoWindow(typeRemote, vcaps.getPlaybackRemote(), null, VideoCaps.DISPLAY_TYPE.DISPLAY_TYPE_BORDER);
		}

		// 保存呼叫类型
		setCallType(CallType.CALL_OUT);

		// 视频预览会产生泄漏
		synchronized (PREVIEWLOCK)
		{
			Log.d(TAG, "in synchronized");
		}

		// 呼叫修改执行呼叫业务管理统一方法
		Log.d(TAG, "tophone:" + fromPhone + ",domain:" + domain);
		CallCommandParams params = new CallCommandParams();
		params.setCallNumber(fromPhone);
		params.setDomain(domain);
		params.setVideo(isVideoCall);
		params.setCaps(vcaps);
		params.setDataCaps(dataCaps);

		// 设置加密模式
		callManager.getVoipConfig().setEncryptMode(Constants.SecureEncryptMode.SECURE_ENCRYPT_AUTO_MODE);
		CallSession callsession = callManager.makeCall(params);
		String callid = null;
		if (null != callsession)
		{
			callid = callsession.getCallID();
		}

		// 判断callid为空或为NULL
		boolean isNullofCallID = (StringUtil.isStringEmpty(callid) || CallErrorCode.isFail(callid));
		if (isNullofCallID)
		{
			Log.i(TAG, "diallcall excute callcommand fail!callid=" + callid);
			if (CallErrorCode.CALL_ERROR_IP_CHANGE.equals(callid))
			{
				Log.e(TAG, "CallErrorCode.CALL_ERROR_IP_CHANGE");
			}
			// 由于数据库需要来电号码作为联系人匹配，防止IP更改后的呼叫没有直接挂断的通话记录更新后没有联系人
			setCallNumber(fromPhone);
			// 呼叫失败 重置呼叫状态 调整清空数据位置
			reset();

			return callid;
		} else
		{

			// 重置路由
			resetAudioRoute(isVideoCall);

			// 设置呼出成功的callid为当前操作的会话
			this.currentCallID = callid;
			curSDKCallID = currentCallID;

			// callRecordMap.put(callid, recordid);
			Log.i(TAG, "diallcall: callID=" + currentCallID + ",isVideoCall=" + isVideoCall);
			return CallErrorCode.CALL_SUCCESS;
		}
	}

	public boolean isCallClosed()
	{
		return (currentCallID == null);
	}

	/**
	 * 挂断呼叫
	 * 
	 * @return currentCallId是否为空 - 执行是否成功
	 */
	public synchronized boolean closeCall()

	{
		Log.i(TAG, "closeCall exec ");
		// 解决主叫呼出快速挂断回到主界面，还有呼通响铃
		synchronized (LOCK_CALL_OPERATION)
		{
			Log.i(TAG, "closeCall enter.");
			// 当前没有会话，不执行此操作
			Log.d(TAG, "callId->" + currentCallID);
			if (StringUtil.isStringEmpty(currentCallID))
			{
				Log.e(TAG, "currentCallID is null, notify call end.");
				Log.i(TAG, "closeCall leave.");
				return false;
			}
			MediaUtil.getIns().stopPlayer();

			CallCommandParams closeParam = new CallCommandParams();
			closeParam.setCallID(currentCallID);

			// 释放视频数据
			clearVideoSurface();

			delRecordMapBycallID(currentCallID);

			// 将呼叫状态重置
			reset();

			// 数据清除
			resetData();
			VideoHandler.getIns().resetTurnDirc();
			// 结束后清除render.这个由界面执行完挂断后直接完成
			String strRet = callManager.executeCallCommand(CallCommands.CALL_CMD_ENDCALL, closeParam);
			Log.i(TAG, "hangup the call " + strRet);
			boolean bRet = parseRet(strRet);

			Log.d(TAG, "closeCall 底层是否执行完成:" + bRet);
			Log.i(TAG, "closeCall leave.");
			return bRet;
		}
	}

	/**
	 * 接听呼叫，接听一个呼叫，包括音，视频呼叫，返回接听是否成功
	 * 
	 * @param callid
	 *            接听会话的唯一标识callid
	 * @param isVideo
	 *            是否需要接入视频， 普通通话传入false
	 * @param context
	 *            上下文
	 * @return true表示成功，false表示失败
	 */
	public boolean callAnswer(String callid, boolean isVideo)
	{

		VideoCaps caps = null;
		VideoCaps dataCaps = null;
		// 判断是否接入视频
		if (isVideo)
		{
			caps = VideoHandler.getIns().initCallVideo();
			dataCaps = VideoHandler.getIns().getDataCaps();
		}

		return callAnswer(callid, isVideo, caps, dataCaps);
	}

	/**
	 * 接听呼叫，接听一个呼叫，包括音，视频呼叫，返回接听是否成功
	 * 
	 * @param callid
	 *            接听会话的唯一标识callid
	 * @param isNeedAnswerVideo
	 *            是否需要接入视频， 普通通话传入false
	 * @param caps
	 *            如果是视频呼叫需要传入视频render参数，如果是普通回呼，此值可以为null
	 * @return true表示成功，false表示失败
	 */
	private boolean callAnswer(String callid, boolean isNeedAnswerVideo, VideoCaps caps, VideoCaps dataCaps)
	{
		boolean ret = false;
		if (callid.isEmpty())
		{
			return ret;
		}
		// 在主动呼叫和被动应答时都应重置音频路由
		resetAudioRoute(isNeedAnswerVideo);
		CallCommandParams param = new CallCommandParams();
		param.setCallID(callid);
		param.setVideo(isNeedAnswerVideo);
		if (isNeedAnswerVideo && null != caps)
		{
			/* 将caps参数中视频render参数放入param中 */
			param.setCaps(caps);
			param.setDataCaps(dataCaps);
			callManager.operateVideoWindow(typeLocal, caps.getPlaybackLocal(), callid, VideoCaps.DISPLAY_TYPE.DISPLAY_TYPE_CLIPPING);
			callManager.operateVideoWindow(typeRemote, caps.getPlaybackRemote(), callid, VideoCaps.DISPLAY_TYPE.DISPLAY_TYPE_BORDER);
		}
		Log.i(TAG, "callid:" + callid + ";callAnswer:" + ret);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_ANSWER, param);

		this.currentCallID = callid;
		curSDKCallID = this.currentCallID;
		SessionBean session = callSessionMap.get(callid);
		// 接听来电后将来电标志位重置
		// isHadComingCall = false;
		comingCallID = null;
		/* 解析执行结果 赋值给ret */
		ret = parseRet(sRet);
		// 如果是语音接听，只显示语音
		if (!ret || null == session)
		{
			// 主要将id置空不然这个时候去注销会出现空指针异常
			resetData();
			Log.e(TAG, "callAnswer failed! [callid = " + callid + "[sessionBean=" + session + ']');
			return ret;
		}
		// 将状态设置改到执行完成后
		Log.i(TAG, "session.getCallerNumber() : " + session.getCallerNumber());
		setCallNumber(session.getCallerNumber());
		setVideoCall(isNeedAnswerVideo);
		if (isNeedAnswerVideo)
		{
			setVoipStatus(CallStatus.STATUS_VIDEOACEPT);
		} else
		{
			setVoipStatus(CallStatus.STATUS_TALKING);
		}
		Log.i(TAG, "callAnswer:isvideo:" + isNeedAnswerVideo + ",callid:" + callid);

		return ret;
	}

	/**
	 * 挂断来电呼叫
	 * 
	 * @param callid
	 *            来电的callid
	 * @return 执行拒绝呼叫结果， true 为成功
	 */
	public boolean rejectCall(String callid)
	{
		Log.d(TAG, "rejectCall()");
		boolean bRet = false;
		if (null == callid || callid.isEmpty())
		{
			return bRet;
		}
		/* 执行 调用呼叫管理系统挂断呼叫,将执行结果赋值给ret */
		CallCommandParams param = new CallCommandParams();
		param.setCallID(callid);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_ENDCALL, param);
		bRet = parseRet(sRet);
		if (!bRet)
		{
			Log.e(TAG, "rejectCall failed! callid:" + callid);
		}
		// 来电呼叫被挂断，将来电呼叫标志位恢复
		// isHadComingCall = false;
		comingCallID = null;
		delCallSessionMapByCallID(callid);
		return bRet;
	}

	/**
	 * 视频通话中关闭视频 对方响应 4113 Param.E_CALL_ID.FAST_CALL_NTF_SESSION_MODIFIED
	 */
	public boolean closeVideo()
	{
		boolean ret = false;
		if (StringUtil.isStringEmpty(currentCallID))
		{
			return ret;
		}

		CallCommandParams params = new CallCommandParams();
		params.setCallID(this.currentCallID);
		// 释放视频数据
		// clearVideoSurface();
		/* 将会话的视频关闭,并赋值给ret */
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_CLOSEVIDEO, params);
		ret = parseRet(sRet);
		Log.i(TAG, "close Video ret-> " + ret);

		return ret;
	}

	/**
	 * 视频应答
	 * 
	 * @param caps
	 *            视频参数
	 * @return 执行结果 true 为执行成功 false 执行失败
	 */
	public boolean agreeUpgradeVideo()
	{
		VideoCaps caps = (VideoCaps) VideoHandler.getIns().initCallVideo();
		VideoCaps dataCaps = VideoHandler.getIns().getDataCaps();

		boolean ret = false;
		if (StringUtil.isStringEmpty(currentCallID))
		{
			return ret;
		}
		/* 将会话升级为视频,执行结果赋值给ret */
		CallCommandParams callCommandParams = new CallCommandParams();
		callCommandParams.setCaps(caps);
		callCommandParams.setDataCaps(dataCaps);
		callCommandParams.setCallID(currentCallID);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_AGREEUPDATEVIDEO, callCommandParams);

		ret = parseRet(sRet);
		if (ret)
		{
			// 接听后未收到会话成功为此状态
			setVoipStatus(CallStatus.STATUS_VIDEOING);
			// CallActionNotifyActivty.getIns().notifyCallModify(CallLogic.ModifyNoticeType.defaultType);
			resetAudioRoute(true);
			// setCameraEx(VideoHandler.getIns().getCurTurnDegree(),
			// VideoHandler.getIns().getCameraType());
			Log.d(TAG, "agreeUpgradeVideo-->checkForAudioReNegotiate");
		}
		Log.i(TAG, "agreeUpgradeVideo:");
		return ret;
	}

	/**
	 * 拒绝视频升级
	 * 
	 * @return true / false 执行结果 true 执行成功 false 执行失败
	 */
	public boolean rejectUpgradeVideo()
	{
		if (isCanceled)
		{
			Log.i(TAG, "disAgreeUpgradeVideo: isCanceled");
			return false;
		}

		boolean ret = false;
		if (StringUtil.isStringEmpty(currentCallID)) // / *当前操作的会话不存在*/)
		{
			return ret;
		}
		/* 拒绝会话升级为视频,将执行结果赋给ret */
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_REJECTUPDATEVIDEO, param);
		ret = parseRet(sRet);
		isCanceled = true;
		Log.i(TAG, "disAgreeUpgradeVideo:");
		return ret;
	}

	/**
	 * 通话过程中升级到视频通话
	 * 
	 * @param caps
	 *            视频参数
	 * @return 执行结果 true 执行成功 false 执行失败
	 */
	public boolean upgradeVideo()
	{
		boolean ret = false;

		VideoCaps caps = VideoHandler.getIns().initCallVideo();
		VideoCaps dataCaps = VideoHandler.getIns().getDataCaps();
		if (!VideoHandler.getIns().isInit())
		{
			caps = VideoHandler.getIns().initCallVideo();
			dataCaps = VideoHandler.getIns().getDataCaps();
		}

		if (null == caps || StringUtil.isStringEmpty(currentCallID))
		{
			return ret;
		}

		CallCommandParams params = new CallCommandParams();
		params.setCallID(currentCallID);
		params.setCaps(caps);
		params.setDataCaps(dataCaps);

		/* 将会话升级为视频,并赋值给ret */
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_PDATEVIDEO, params);
		ret = parseRet(sRet);
		Log.i(TAG, "upgrade Video Success " + ret);
		if (ret)
		{
			setVoipStatus(CallStatus.STATUS_VIDEOINIT);
			// CallActionNotifyActivty.getIns().notifyCallViewUpdate();
		}

		// 低带宽升级失败
		else if (CallErrorCode.UPDATE_FAIL_LOW_BW.equals(sRet))
		{
			setVoipStatus(CallStatus.STATUS_TALKING);
			// CallActionNotifyActivty.getIns().notifyLowBWUpdateFail();
		}
		return ret;
	}

	/**
	 * 获取当前通话媒体信息
	 */
	public StreamInfo getMediaInfo()
	{
		Log.i(TAG, "getMediaInfo()");

		if (StringUtil.isStringEmpty(currentCallID))
		{
			Log.i(TAG, "getMediaInfo() callid is null");
			return null;
		}

		return callManager.getMediaInfo(currentCallID);
	}

	/**
	 * 二次拨号
	 * 
	 * @param code
	 *            号码
	 * @return 组件调用返回码
	 */
	public boolean reDial(String code)
	{
		boolean isEmpty = (StringUtil.isStringEmpty(currentCallID) || StringUtil.isStringEmpty(code));
		if (isEmpty)
		{
			return false;
		}

		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		param.setDialCode(code);
		String exeRetString = callManager.executeCallCommand(CallCommands.CALL_CMD_REDAL, param);
		/* 解析返回值，返回是否成功 */

		Log.i(TAG, "reDial:" + code);

		String SUCCESS = "0";
		return SUCCESS.equals(exeRetString);
	}

	/**
	 * 本地麦克风静音
	 * 
	 * @param isRefer
	 *            是否会议中转移 true: 会议中转移， false：非会议中转移，对设备原来的静音状态取反。
	 * @param isMute
	 *            是否静音 true: 静音， false：取消静音
	 */
	public boolean setLocalMute(boolean isRefer, boolean isMute)
	{
		boolean result = false;
		if (isRefer)
		{
			result = mute(isMute, 0);
		} else
		{
			result = mute(!isMicrophoneMute(), 0);
		}
		if (result)
		{
			setMicrophoneMute(isMute);
			return true;
		}
		return false;
	}

	/**
	 * 扬声器静音
	 */
	public boolean oratorMute(boolean isMute)
	{
		// 底层返回值可直接判断
		boolean result = mute(isMute, 1);

		if (result)
		{
			setOratorMute(isMute);
			return true;
		}
		// 底层返回值可直接判断
		return false;
	}

	/**
	 * 旋转摄像头
	 * 
	 * @param rotation
	 *            0-0度 1-90度 2-180度 3-270度
	 */
	public void setRotationCamera(int orientation)
	{
		VideoCaps caps = VideoHandler.getIns().getCaps();
		CallCommandParams params = new CallCommandParams();
		caps.setRemoteRoate(orientation == 1 ? 2 : 0);
		params.setCaps(caps);
		params.setCallID(currentCallID);
		callManager.executeCallCommand(CallCommands.CALL_CMD_CAMERA_ROTATION, params);
	}

	/**
	 * 旋转摄像头, 产品使用了这个接口，没有使用setRotationCamera() 设置视频画面的旋转角度(当设备旋转，自动感应倒置界面时)
	 */
	public void setCameraEx(int degree, int cameraType)
	{
		if (!LayoutUtil.isPhone())
		{
			// 如果是pad 加上90度， 当超过360度得时候去余
			degree += 90;
			degree %= 360;

		}
		boolean isLand = true;
		if (0 <= degree && degree < 60 || (330 <= degree && degree <= 360))
		{
			isLand = false;
		}
		// 90
		else if (60 <= degree && degree <= 120)
		{
			isLand = true;
		}
		// 180
		else if (150 <= degree && degree <= 210)
		{
			isLand = false;
		}
		// 270
		else if (240 <= degree && degree <= 300)
		{
			isLand = true;
		}
		VideoCaps caps = VideoHandler.getIns().getCaps();
		CallCommandParams params = new CallCommandParams();
		// 摄像头问题统一接口适配，兼容手机、pad及ep820设备
		int camOrieantation = OrieantationUtil.getIns().calcCamOrieantation(cameraType);
		// 竖屏时需将采集角度翻转180度，否则对端看到图像出现倒置
		if (!isLand)
		{
			caps.setCameraRotation((camOrieantation + 2) % 4);
			caps.setLocalRoate(2);
		} else
		{
			caps.setCameraRotation(camOrieantation);
			caps.setLocalRoate(0);
		}
		// end modified by l00208218 2015/03/19
		params.setCaps(caps);
		params.setCallID(currentCallID);

		callManager.executeCallCommand(CallCommands.CALL_CMD_CAMERA_ROTATION, params);
	}

	/**
	 * 呼叫保持
	 * 
	 * @return 组件调用结果 返回 0成功 非0失败
	 */
	public boolean holdcall()
	{
		Log.i(TAG, "holdCall() enter");
		if (StringUtil.isStringEmpty(currentCallID))
		{
			Log.e(TAG, "holdCall() callID is null");
			return false;
		}
		// 之后增加条件判断是否能保持
		boolean canNotKeepholding = (getVoipStatus() != CallStatus.STATUS_CALLING || getVoipStatus() != CallStatus.STATUS_VIDEOINIT);
		if (canNotKeepholding)
		{
			return false;
		}
		CallCommandParams params = new CallCommandParams();
		params.setCallID(currentCallID);
		String strRet = callManager.executeCallCommand(CallCommands.CALL_CMD_HOLD, params);
		Log.i(TAG, "holdCall() strRet:" + strRet + ", callid=" + currentCallID);
		return true;
	}

	/**
	 * 方法名称：resume 方法描述：恢复通话
	 * 
	 * @return 组件调用结果
	 */
	public boolean resume()
	{
		Log.i(TAG, "resume() enter");
		if (StringUtil.isStringEmpty(currentCallID))
		{
			Log.i(TAG, "holding() callid is null");
			return false;
		}
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		callManager.executeCallCommand(CallCommands.CALL_CMD_RESUME, param);
		return true;
	}

	/**
	 * 辅流媒体控制
	 * 
	 * @param mediaModule
	 *            控制模块
	 * @param mediaSwitch
	 *            控制开关
	 */
	public boolean dataControl(int mediaModule, int mediaSwitch)
	{
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		param.setVideoControlModule(mediaModule);
		param.setVideoControlSwitch(mediaSwitch);
		String strRet = callManager.executeCallCommand(CallCommands.CALL_CMD_DATA_CONTROL, param);
		return StringUtil.findElemBool(strRet, "ret", false);
	}

	/**
	 * 获取Framesize信息
	 */
	public String getDataFramesize()
	{
		String strResult = "";

		if (StringUtil.isStringEmpty(currentCallID))
		{
			Log.e(TAG, "getDataFramesize() callid is null");
			return "1280*720";
		}

		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		strResult = callManager.executeCallCommand(CallCommands.CALL_CMD_GET_DATA_FRAMESIZE, param);

		return strResult;
	}

	/**
	 * 获取网络媒体加密信息
	 */
	public int getMediaSEncryptState()
	{
		Log.i(TAG, "getMediaSEncryptState()");
		String strResult = "";

		if (StringUtil.isStringEmpty(currentCallID))
		{
			Log.e(TAG, "getMediaSEncryptState() callid is null");
			return 0;
		}

		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		strResult = callManager.executeCallCommand(CallCommands.CALL_CMD_GET_MEDIA_ENCRYPT_STATE, param);

		return StringUtil.stringToInt(strResult);
	}

	/**
	 * 静音/关闭静音
	 * 
	 * @param mute
	 *            true:开启静音 false:关闭静音
	 * @param type
	 *            麦克风静音为0 扬声器为1
	 * @return 静音是否成功
	 */
	public boolean mute(boolean mute, int type)
	{
		if (StringUtil.isStringEmpty(currentCallID))
		{
			return false;
		}
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);

		param.setMuteType(type);
		param.setNeedMute(mute);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_MUTE, param);
		/* 解析返回值返回赋值给sRet */
		boolean bRet = parseRet(sRet);
		Log.i(TAG, "mute ismuteAction:" + mute);
		return bRet;
	}

	/**
	 * 切换窗口
	 * 
	 * @return 成功或者失败
	 */
	public boolean modifyRender(boolean isModifyBfcpRender)
	{
		VideoCaps caps = VideoHandler.getIns().getCaps();
		VideoCaps dataCaps = VideoHandler.getIns().getDataCaps();
		CallCommandParams params = new CallCommandParams();

		// 将当前呼叫ID，CAPS 写入参数类
		params.setCallID(currentCallID);
		params.setCaps(caps);
		params.setDataCaps(dataCaps);
		params.setModifyBfcpRender(isModifyBfcpRender);
		// 调用切换窗口方法
		callManager.executeCallCommand(CallCommands.CALL_CMD_MODFIY_RENDER, params);

		return true;
	}

	/**
	 * render控制
	 * 
	 * @param witch
	 *            本远端
	 * @param isOpen
	 *            true开启
	 */
	public boolean controlRenderVideo(int renderModule, boolean isStart)
	{
		int mediaSwitch = isStart ? CallCommandParams.MMV_CONTROL_START : CallCommandParams.MMV_CONTROL_STOP;
		return videoControl(renderModule, mediaSwitch);
	}

	/**
	 * 主流媒体控制
	 * 
	 * @param mediaModule
	 *            控制模块
	 * @param mediaSwitch
	 *            控制开关
	 */
	public boolean videoControl(int mediaModule, int mediaSwitch)
	{
		int iRet = callManager.vedioControl(StringUtil.stringToInt(currentCallID), mediaSwitch, mediaModule);

		Log.i(TAG, "videoControl render:" + mediaModule + ", mediaSwitch:" + mediaSwitch + ", ret:" + iRet);

		return 0 == iRet;
	}

	/**
	 * 关闭本地摄像头
	 * 
	 * @return 执行完成
	 * @param isCloseAction
	 *            true表示关闭本地摄像头操作，false表示打开操作
	 */
	public boolean localCameraControl(boolean isCloseAction)
	{
		// 打开关闭本地摄像头
		// 获取当前通话的视频Caps
		VideoCaps caps = VideoHandler.getIns().getCaps();

		// 设置是否关闭本地摄像头标志位
		caps.setIsCloseLocalCamera(isCloseAction);

		CallCommandParams params = new CallCommandParams();

		// 将当前呼叫ID，CAPS 写入参数类
		params.setCallID(currentCallID);
		params.setCaps(caps);

		// 调用底层方法关闭本地摄像头
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_LOCAL_CAMERA_CONTROL, params);

		boolean bRet = parseRet(sRet);
		Log.i(TAG, "close local camera Success" + bRet);
		return bRet;
	}

	/**
	 * 开启或者关闭主流采集
	 * 
	 * @param isStart
	 *            开启或者关闭
	 */
	public boolean controlVideoCapture(boolean isStart)
	{
		int mediaSwitch = isStart ? CallCommandParams.MMV_CONTROL_START : CallCommandParams.MMV_CONTROL_STOP;
		return videoControl(CallCommandParams.MMV_SWITCH_CAPTURE, mediaSwitch);
	}

	/**
	 * 描述：本地切换摄像头（前后摄像头切换）
	 * 
	 * @param caps
	 *            本远端参数
	 * @return 切换成功，失败
	 */
	public boolean switchCamera(VideoCaps caps)
	{
		if (null == caps)
		{
			Log.e(TAG, "caps is null!");
			return false;
		}
		boolean callManagerAndIDStatus = (callManager == null || StringUtil.isStringEmpty(currentCallID));
		if (callManagerAndIDStatus)
		{
			Log.e(TAG, "callManager or  currentCallID is null!");
			return false;
		}
		CallCommandParams params = new CallCommandParams();
		/* 将当前呼叫ID，CAPS 写入参数类 */
		params.setCallID(currentCallID);
		params.setCaps(caps);
		/* 调用callManagerq切换摄像头 */
		callManager.executeCallCommand(CallCommands.CALL_CMD_SWITCHCAMERA, params);
		return true;
	}

	/**
	 * 启动音频媒体通道
	 * 
	 * @return 是否成功
	 */
	public boolean startAudioChannel()
	{
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		param.setMediaOperSwitch(CallCommandParams.MMV_CONTROL_START);
		param.setMediaOperType(CallCommandParams.MEDIA_TYPE_AUDIO);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_OPERATE_MEDIA, param);

		boolean bRet = parseRet(sRet);
		Log.i(TAG, "startAudioChannel:" + bRet);

		return bRet;
	}

	/**
	 * 停止音频媒体通道
	 * 
	 * @return 是否成功
	 */
	public boolean stopAudioChannel()
	{
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		param.setMediaOperSwitch(CallCommandParams.MMV_CONTROL_STOP);
		param.setMediaOperType(CallCommandParams.MEDIA_TYPE_AUDIO);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_OPERATE_MEDIA, param);

		boolean bRet = parseRet(sRet);
		Log.i(TAG, "stopAudioChannel:" + bRet);

		return bRet;
	}

	/**
	 * 请求 开始BFCP
	 * 
	 * @return 是否执行完成
	 */
	public boolean startBFCP()
	{
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		callManager.operateVideoWindow(typeBfcp, VideoHandler.getIns().getDataCaps().getPlaybackRemote(), currentCallID + "",
				VideoCaps.DISPLAY_TYPE.DISPLAY_TYPE_BORDER);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_STARTBFCP, param);
		boolean ret = parseRet(sRet);
		Log.i(TAG, "send aux " + ret);
		return ret;
	}

	/**
	 * 请求停止BFCP
	 * 
	 * @return 执行是否完成
	 */
	public boolean stopBFCP()
	{
		CallCommandParams param = new CallCommandParams();
		param.setCallID(currentCallID);
		String sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_STOPBFCP, param);
		setBfcpStatus(BFCPStatus.BFCP_END);
		boolean ret = parseRet(sRet);

		Log.i(TAG, "stop aux " + ret);
		return ret;
	}

	/*******************************************************************
	 * 处理由SDK层上报的事件
	 *******************************************************************/

	/**
	 * 来电通知 4102
	 * 
	 * @param callsession
	 *            会话对象
	 */
	public void processCallNtfComing(SessionBean session)
	{
		Log.d(TAG, "callsession:" + session);
		if (session == null)
		{
			Log.d(TAG, "seesion is null");
			return;
		}
		comingCallID = session.getCallID();
		CallCommandParams callCommandParams = new CallCommandParams();
		callCommandParams.setCallID(comingCallID);

		refreshAudioRoute();

		callSessionMap.put(session.getCallID(), session);

		callManager.executeCallCommand(CallCommands.CALL_CMD_ALERTING_CALL, callCommandParams);
	}

	/**
	 * 接听通知 & 会开始通知 4104(接听后，对方接听后)
	 */
	public void processCallNtfTalk(SessionBean session)
	{
		// 快速挂断拉起界面
		MediaUtil.getIns().stopPlayer();
		setBfcpStatus(BFCPStatus.BFCP_END);
		if (session == null)
		{
			Log.e(TAG, "processCallNtfTalk:session data is null");
			return;
		}
		String callid = session.getCallID();
		if (StringUtil.isStringEmpty(callid))
		{
			return;
		}
		// 非同路通话，挂断来电 -- 快速挂断拉起界面
		boolean notSameidAndlogicIsClose = (!callid.equals(this.currentCallID) || CallStatus.STATUS_CLOSE == getVoipStatus());
		if (notSameidAndlogicIsClose)
		{
			CallCommandParams param = new CallCommandParams();
			param.setCallID(callid);
			callManager.executeCallCommand(CallCommands.CALL_CMD_ENDCALL, param);
			return;
		}
		// 增加session如有，则会被覆盖 防止接听与呼叫的视频状态不一致
		if (callSessionMap.containsKey(callid))
		{
			SessionBean sessionOriginal = callSessionMap.get(callid);
			sessionOriginal.setVideoCall(session.isVideoCall());
			callSessionMap.put(session.getCallID(), sessionOriginal);
		} else
		// 此时的可能是主动呼出，对方接听的
		{
			callSessionMap.put(callid, session);
		}
		// 由会话开始决定是什么会话

		if (session.isVideoCall())
		{
			setVoipStatus(CallStatus.STATUS_VIDEOING);
			setEnableBfcp(session.isBFCPSuccess());
			/* 获得BFCP能力结果保存在CallLogic中 */
		} else
		{
			// 本方视频呼叫，对方音频接听时，释放视频数据标志位设为true
			int voipStatus = getVoipStatus();
			if (CallStatus.STATUS_VIDEOINIT == voipStatus)
			{
				VideoHandler.getIns().clearCallVideo();
			}

			setVoipStatus(CallStatus.STATUS_TALKING);
		}
		// 接听时刷新菜单栏扬声器和听筒显示
		resetAudioRoute(session.isVideoCall());

		// 保存语音状态
		setCallType(CallStatus.STATUS_TALKING);
	}

	/**
	 * 振铃通知
	 * 
	 * @param callSession
	 *            会话对象
	 */
	public void processCallNtfRinging(SessionBean session)
	{

		if (session == null)
		{
			Log.e(TAG, "processCallNtfRinging:session data is null");
			return;
		}

		if (!isCurrentCall(session.getCallID()))
		{
			Log.e(TAG, "processCallNtfRinging fail, is not current call. curCallID: " + currentCallID + " ntfCallID: " + session.getCallID());
			return;
		}
	}

	/**
	 * 收到通话结束，上报的消息，最终结果在oncalldestroy中处理，此处只用于获取挂断原因
	 */
	public void processCallNtfEnded(SessionBean session)
	{
		Log.i(TAG, "processCallNtfEnded enter.");
		if (session == null)
		{
			Log.e(TAG, "session is null.");
			Log.i(TAG, "processCallNtfEnded leave.");
			return;
		}
		String callid = session.getCallID();
		if (StringUtil.isStringEmpty(callid))
		{
			Log.e(TAG, "callid is null.");
			Log.i(TAG, "processCallNtfEnded leave.");
			return;
		}
		Log.i(TAG, "processCallNtfEnded callid:" + callid);
		// bye原因
		String reason = session.getReleaseReason();
		Log.e(TAG, "onCallEnd  processCallNtfEnded()  hangup reason:" + reason);
		Log.i(TAG, "processCallNtfEnded leave.");
	}

	/**
	 * 通话挂断通知
	 * 
	 * @param callsession
	 *            会话对象
	 */
	public void processCallNtfClosed(SessionBean session)
	{
		Log.i(TAG, "processCallNtfClosed enter.");
		if (session == null)
		{
			Log.e(TAG, "session is null.");
			Log.i(TAG, "processCallNtfClosed leave.");
			return;
		}

		String callid = session.getCallID();
		if (StringUtil.isStringEmpty(callid))
		{
			Log.e(TAG, "callid is null.");
			Log.i(TAG, "processCallNtfClosed leave.");
			return;
		}
		Log.i(TAG, "processCallNtfClosed callid:" + callid);

		// 如果是当前电话 不是当前会话的在后面
		if (isCurrentCall(callid))
		{
			// 停止振铃
			MediaUtil.getIns().stopPlayer();
			// 清理数据缓存
			delRecordMapBycallID(callid);
			// 还原通话结束标志
			VideoHandler.getIns().resetTurnDirc();
			resetData();

			// 释放视频数据
			clearVideoSurface();
			// 接到挂断通知重新设置数据
			reset();
		} else
		{
			// 如果有插入记录说明已经接听的会话(解决多路会话导致更新，插入记录无法判断)
			if (null != comingCallID && callid.equals(comingCallID))
			{
				// // 未接来电必须找到来电缓存的sessionbean
				if (callSessionMap.containsKey(callid))
				{
					delCallSessionMapByCallID(callid);
				}
				Log.i(TAG, "ComingCall is closed by other! callid:" + callid);

				// 标志位后移增加挂断时间准备，防止界面未完全挂断，多个来电界面销毁问题
				// isHadComingCall = false;
				comingCallID = null;
			} else
			{
				delRecordMapBycallID(callid);
			}
		}
		Log.i(TAG, "processCallNtfClosed leave.");
	}

	/**
	 * 会话变更通知 4112
	 * 
	 * @param sessionBean
	 *            会话数据
	 */
	public void processCallNtfModifyAlert(SessionBean session)
	{
		Log.d(TAG, "processCallNtfModifyAlert()");
		boolean prepertyOfSessionBean = (null == session) || (!isCurrentCall(session.getCallID()));
		if (prepertyOfSessionBean)
		{
			return;
		}
		String oper = session.getOperation();
		int voipStatus = getVoipStatus();
		if ("add".equals(oper))
		{
			isCanceled = false;
			boolean noCameraOrUntalking = (!VideoHandler.getIns().isSupportVideo() || CallStatus.STATUS_TALKING != voipStatus);
			if (noCameraOrUntalking)
			{
				// 语音升级视频Alerting 设置BFCP能力
				setEnableBfcp(false);
				CallCommandParams param = new CallCommandParams();
				param.setCallID(session.getCallID());
				callManager.executeCallCommand(CallCommands.CALL_CMD_REJECTUPDATEVIDEO, param);
				return;
			}
			// 设置BFCP能力
			setEnableBfcp(session.isBFCPSuccess());
		}
		Log.d(TAG, "4112 mCallSession upgrade  video call alert");
	}

	/**
	 * 处理视频升级结果通知
	 */
	public void processCallAddVideoResult(final SessionBean currentCall)
	{

	}

	/**
	 * 会话变更 4113
	 * 
	 * @param mCallSession
	 *            会话
	 */
	public void processCallNtfModified(final SessionBean currentCall)
	{
		Log.d(TAG, "processCallNtfModified()");
		if (null == currentCall)
		{
			Log.d(TAG, "session is null!");
			return;
		} else
		{
			if (!isCurrentCall(currentCall.getCallID()))
			{
				Log.d(TAG, "[session=" + currentCall + "] [callID=" + currentCall.getCallID() + ']');
				return;
			}
		}
		int voipStatus = getVoipStatus();
		Log.d(TAG, "voipStatus = " + voipStatus);

		String oper = currentCall.getOperation();
		int videoModifyState = currentCall.getVideoModifyState();
		Log.d(TAG, "videoModifyState = " + videoModifyState);

		// 关闭视频成功 || 对端请求关闭视频
		boolean isVideoClose = (0 == videoModifyState && CallStatus.STATUS_VIDEOING == voipStatus);
		// 主动升级视频成功
		boolean isActiveUpdateVideo = (1 == videoModifyState && CallStatus.STATUS_VIDEOINIT == voipStatus);
		// 主动升级视频失败
		boolean isVideoUpFailed = (0 == videoModifyState && CallStatus.STATUS_VIDEOINIT == voipStatus);
		// 升级取消
		boolean isVideoUpCanceled = (0 == videoModifyState && CallStatus.STATUS_TALKING == voipStatus);

		if (isVideoClose)
		{
			setVideoCall(false);
			clearVideoSurface();
			setVoipStatus(CallStatus.STATUS_TALKING);
			// 需设置BfcpStatus,否则与VCT对接辅流时音视频变换，界面异常
			setBfcpStatus(BFCPStatus.BFCP_END);
		}
		// 主动升级，会话重协商。
		else if (isActiveUpdateVideo)
		{
			Log.d(TAG, "Upgrade To Video Call");
			setVoipStatus(CallStatus.STATUS_VIDEOING);
			// resetAudioRoute(true);
			setEnableBfcp(currentCall.isBFCPSuccess());
		} else if (isVideoUpFailed)
		{
			setVoipStatus(CallStatus.STATUS_TALKING);
		} else if (VIDEOMOD.equals(oper))
		{
			setEnableBfcp(currentCall.isBFCPSuccess());
		}
	}

	/**
	 * 刷新view
	 */
	public void processCallNtfRefreshView(EventData data)
	{
		Log.i(TAG, "refresh view()");
		boolean cameraDataStatus = (null != data && data instanceof CameraViewRefresh);
		if (cameraDataStatus)
		{
			// 目前只有本地采集点， 后续有做render事件时 再添加
			CameraViewRefresh viewData = (CameraViewRefresh) data;

			if (viewData.getMediaType() == CameraViewRefresh.MEDIA_TYPE_VIDEO || viewData.getMediaType() == CameraViewRefresh.MEDIA_TYPE_PREVIEW)
			{
				if (viewData.getViewType() == CameraViewRefresh.VIEW_TYPE_LOCAL_ADD)
				{
					refreshView(true);
				} else if (viewData.getViewType() == CameraViewRefresh.VIEW_TYPE_LOCAL_REMOVE)
				{
					refreshView(false);
				}
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					Log.e(TAG, "Progress get an Exception.");
				}
			}
		}
	}

	/**
	 * 刷新LocalRender视频界面
	 */
	private void refreshView(boolean isAdd)
	{
		synchronized (LocalHideRenderServer.class)
		{
			Log.i(TAG, "refresh_view");
			VideoHandler.getIns().refreshLocalHide(isAdd);
		}
	}

	/**
	 * 协商结果处理 将BFCP重协商结果上报界面层
	 * 
	 * @param callid
	 *            呼叫唯一标识
	 * @param ConsultRet
	 *            协商结果/重协商结果
	 */
	public void processBFCPConsultRet(String callId, boolean consultRet)
	{
		/* 判断是否是当前会话，不是则返回 */
		if (!isCurrentCall(callId))
		{
			return;
		}
		/* 根据协商结果，设置BFCP是否可用（共享按钮） */
		this.setEnableBfcp(consultRet);
		callManager.operateVideoWindow(typeBfcp, VideoHandler.getIns().getDataCaps().getPlaybackRemote(), callId + "",
				VideoCaps.DISPLAY_TYPE.DISPLAY_TYPE_BORDER);
		if (consultRet)
		{
			CallLogic.getInstance().setEnableBfcp(true);
		} else
		{
			CallLogic.getInstance().setEnableBfcp(false);
		}
	}

	/*******************************************************************
	 * 处理由SDK层上报的事件finish
	 *******************************************************************/

	/**
	 * 清除视频数据
	 */
	public void clearVideoSurface()
	{
		int voipStatus = getVoipStatus();
		Log.d(TAG, "clearVideoSurface() - voipStatus ->" + voipStatus);
		boolean voipStatusIsTrue = (voipStatus == CallStatus.STATUS_VIDEOING || voipStatus == CallStatus.STATUS_VIDEOACEPT || voipStatus == CallStatus.STATUS_VIDEOINIT);
		if (voipStatusIsTrue)
		{
			// 释放视频数据
			Log.d(TAG, "clearVideoSurface() - 释放视频数据");
			VideoHandler.getIns().clearCallVideo();
		}
	}

	/**
	 * 用于清除缓存中的多路Callrecord（主被动挂断后）
	 * 
	 * @param callid
	 *            会话ID
	 */
	private void delRecordMapBycallID(String callid)
	{
		delCallRecordMapByCallID(callid);
		delCallSessionMapByCallID(callid);
	}

	/**
	 * 删除缓存的与Callid对应的数据库记录ID
	 * 
	 * @param callid
	 *            会话ID
	 */
	private void delCallRecordMapByCallID(String callid)
	{
		if (callRecordMap.containsKey(callid))
		{
			callRecordMap.remove(callid);
		}
	}

	/**
	 * 删除缓存的CallSessionMap中的会话
	 * 
	 * @param callid
	 *            会话ID
	 */
	private void delCallSessionMapByCallID(String callid)
	{
		if (callSessionMap.containsKey(callid))
		{
			callSessionMap.remove(callid);
		}
	}

	public boolean isCurrentCall(String callid)
	{
		boolean ret = false;
		if (StringUtil.isNotEmpty(callid) && StringUtil.isNotEmpty(currentCallID) && callid.equals(this.currentCallID))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * 重置音频路由
	 */
	public void resetAudioRoute(boolean isVideo)
	{
		Log.d(TAG, "resetAudioRoute");
		// 存在多个路由，则恢复听筒模式,此Demo不需要默认听筒模式，直接开启扬声器。。
		// if (1 >= supportAudioRouteList.size())
		// {
		// Log.d(TAG, "only one route");
		// return;
		// }

		if (null != callManager)
		{

			// 是否有外部设备(有线耳机和蓝牙耳机)
			// boolean hasEarphone =
			// (supportAudioRouteList.contains(EarpieceMode.TYPE_EARPHONE))
			// || (supportAudioRouteList.contains(EarpieceMode.TYPE_BLUETOOTH));
			// // 在没有外接设备的情况下视频默认使用扬声器
			// // 是视频并且无外接耳机时，默认使用扬声器
			// int resetRoute = ((ConfigApp.getInstance().isUsePadLayout() ||
			// isVideo) && !hasEarphone) ? EarpieceMode.TYPE_LOUD_SPEAKER
			// : EarpieceMode.TYPE_AUTO;
			if (callManager.setAudioRoute(EarpieceMode.TYPE_LOUD_SPEAKER))
			{

				// 刷新当前使用的路由
				refreshAudioRoute();

				// if (null != routeCallBack) {
				// routeCallBack.onAudioRouteSwitch(supportAudioRouteList.get(0));
				// }

				Log.i(TAG, "changed audioRoute: " + supportAudioRouteList.get(0));

			}
		}
	}

	private void refreshAudioRoute()
	{
		// 蓝牙耳机判断调整，保持和链接状态时的判断结果一致
		if (callManager != null)
		{
			int route = callManager.getAudioRoute();
			Log.d(TAG, "refreshAudioRoute route: " + route);

			// 获取音频路由只能获取到是听筒模式还是扬声器模式
			switch (route) {
			// 听筒模式（包含蓝牙>有线耳机>听筒）
			case EarpieceMode.TYPE_AUTO:
				// 获取到当前为听筒
				break;

			// 听筒
			case EarpieceMode.TYPE_TELRECEIVER:
			// 手机下，有听筒
			// if (LayoutUtil.isPhone())
			// {
			// // 把听筒放到第一位，表示当前使用听筒
			// modSupportAudioRouteList(EarpieceMode.TYPE_TELRECEIVER);
			// }
			// Pad下听筒为扬声器
			// else
			{
				// 把扬声器放到第一位，表示当前使用扬声器
				modSupportAudioRouteList(EarpieceMode.TYPE_LOUD_SPEAKER);
			}
				break;
			// 扬声器模式
			case EarpieceMode.TYPE_LOUD_SPEAKER:
				// 把扬声器放到第一位，表示当前使用扬声器
				modSupportAudioRouteList(EarpieceMode.TYPE_LOUD_SPEAKER);
				break;
			// 蓝牙
			case EarpieceMode.TYPE_BLUETOOTH:
				// 有线耳机
			case EarpieceMode.TYPE_EARPHONE:
				modSupportAudioRouteList(route);
				break;
			default:
				break;
			}

			// 如果非扬声器模式下， 即听筒模式下，按蓝牙>有线耳机>听筒的顺序排序
			if (EarpieceMode.TYPE_LOUD_SPEAKER != supportAudioRouteList.get(0))
			{
				// 如果有听筒则把听筒放第一位
				if (supportAudioRouteList.contains(EarpieceMode.TYPE_TELRECEIVER))
				{
					supportAudioRouteList.remove((Integer) EarpieceMode.TYPE_TELRECEIVER);
					supportAudioRouteList.add(0, EarpieceMode.TYPE_TELRECEIVER);
				}

				// 如果有线耳机则把有线耳机放第一位
				// if
				// (supportAudioRouteList.contains(EarpieceMode.TYPE_EARPHONE))
				else if (supportAudioRouteList.contains(EarpieceMode.TYPE_EARPHONE))
				{
					supportAudioRouteList.remove((Integer) EarpieceMode.TYPE_EARPHONE);
					supportAudioRouteList.add(0, EarpieceMode.TYPE_EARPHONE);
				}

				// 如果有蓝牙则把蓝牙放第一位
				// if
				// (supportAudioRouteList.contains(EarpieceMode.TYPE_BLUETOOTH))
				else if (supportAudioRouteList.contains(EarpieceMode.TYPE_BLUETOOTH))
				{
					supportAudioRouteList.remove((Integer) EarpieceMode.TYPE_BLUETOOTH);
					supportAudioRouteList.add(0, EarpieceMode.TYPE_BLUETOOTH);
				}
			}

			// 刷新界面,音频路由改变（这个界面暂时没添加）
			// CallActionNotifyActivty.getIns().notifyUpdateAudioRoute();

			// end modify 蓝牙耳机判断调整，保持和链接状态时的判断结果一致
			Log.i(TAG, "getAudioRoute:" + route);
		}
	}

	/**
	 * 增加默认的音频路由 Pad：扬声器 Phone：听筒、扬声器
	 */
	private void addDefaultAudioRoute()
	{
		supportAudioRouteList.clear();

		// 默认都支持扬声器
		supportAudioRouteList.add(EarpieceMode.TYPE_LOUD_SPEAKER);
		if (null == callManager)
		{
			Log.e(TAG, "addDefaultAudioRoute fail. callManager is null.");
			return;
		}

		// 判断是手机，则把听筒加到第一位，即默认使用听筒模式
		// if (LayoutUtil.isPhone())
		// {
		// supportAudioRouteList.add(0, EarpieceMode.TYPE_TELRECEIVER);
		// }
	}

	/**
	 * 公共处理逻辑，提取出来，降低函数复杂度和代码冗余
	 */
	private void modSupportAudioRouteList(int route)
	{
		if (supportAudioRouteList.contains(route))
		{
			supportAudioRouteList.remove((Integer) route);
		}
		supportAudioRouteList.add(0, route);
	}

	/**
	 * 解析底层返回值（只用于判断底层是否执行完成）
	 * 
	 * @param retStr
	 *            执行底层命令返回string值，
	 * @return 是否为真
	 */
	private boolean parseRet(String retStr)
	{
		boolean bRet = false;
		if (null != retStr && "0".equals(retStr))
		{
			bRet = true;
		}
		return bRet;
	}

	/**
	 * 强制关闭所有呼叫，用于注销
	 */
	public void forceCloseCall()
	{
		Log.i(TAG, "forceCloseCall exec ");

		// 补充，当存在主动呼出中，callSessionMap未保存记录，所以当前呼叫需要单独挂断
		if (StringUtil.isNotEmpty(currentCallID))
		{
			Log.i(TAG, "forceCloseCall currentCallID");
			closeCall();
		}
		if (callSessionMap.size() <= 0)
		{
			return;
		}
		Set<String> keySet = callSessionMap.keySet();
		HashSet<String> setContain = new HashSet<String>(0);
		setContain.addAll(keySet);
		Iterator<String> itor = setContain.iterator();
		String key = null;
		boolean hasNext = itor.hasNext();
		while (hasNext)
		{
			key = itor.next();
			if (key.equalsIgnoreCase(comingCallID))
			{
				Log.d(TAG, "comingCallID" + comingCallID);
				// 软终端建立视频通话，振铃时，软终端弹出异常退出提示，软终端一直振铃
				MediaUtil.getIns().cancelVibrate();
				MediaUtil.getIns().stopPlayer();
				rejectCall(comingCallID);
				// 在断网主动挂断时，需要主动关闭铃音并关闭来电界面
				// 这部分以后可以提供接口给Demo，但现在只是登出操作用到此函数，暂时不用提供接口
				// Log.d(TAG, "sendHandlerMessage to close call;callid:" +
				// comingCallID);
				// HomeActivity.sendHandlerMessage(MSG_FOR_HOMEACTIVITY.MSG_NOTIFY_CALLCLOSE,
				// comingCallID);
			} else
			{
				closeInnerCall(key);
			}
			hasNext = itor.hasNext();
		}
		setContain.clear();
		// setContain = null;
		// 电话部分全部重置
		callSessionMap.clear();
		callRecordMap.clear();
	}

	/**
	 * 删除保持中的呼叫
	 * 
	 * @param callid
	 *            会话标识
	 */
	private boolean closeInnerCall(String callid)
	{
		// 不再保存callsession，只有callid ,所有命令调用通过CALLManager去做
		String sRet = "";
		CallCommandParams parm = new CallCommandParams();
		parm.setCallID(callid);
		sRet = callManager.executeCallCommand(CallCommands.CALL_CMD_ENDCALL, parm);
		Log.i(TAG, "closeCall:" + sRet);
		delCallRecordMapByCallID(callid);
		boolean bRet = parseRet(sRet);
		return bRet;
	}

	/**
	 * 重置数据
	 */
	private void resetData()
	{
		currentCallID = null;
		microphoneMute = false;
		// fromNumber = null;
		// routeCallBack = null;
	}

	/**
	 * 添加视频到布局
	 */
	private void addViewToContain(View videoView, ViewGroup videoContain)
	{
		if (null == videoView || null == videoContain)
		{
			Log.i(TAG, "addViewToContain()->Some view is Null");
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
	 * 把远端视频画面添加到界面布局中
	 */
	public void addLocalRenderToContain(ViewGroup previewLayout)
	{
		View localVV = VideoHandler.getIns().getLocalCallView();
		if (null != localVV)
		{
			addViewToContain(localVV, previewLayout);
		}
	}

	/**
	 * 把本地和远端视频画面填加到界面布局中
	 * 
	 * @param localView
	 *            包含本地render
	 * @param remoteView
	 *            包含远端render
	 * @param isLocal
	 *            true 本地最上面 false远端最上面
	 */
	public void addRenderToContain(ViewGroup localViewContain, ViewGroup remoteViewContain, boolean isLocal)
	{
		Log.d(TAG, "addRenderToContain()");

		synchronized (RENDER_CHANGE_LOCK)
		{
			VideoHandler.getIns().setRemoteVideoView((RelativeLayout) remoteViewContain);
			controlRenderVideo(CallCommandParams.MMV_SWITCH_LCLRENDER | CallCommandParams.MMV_SWITCH_RMTRENDER, false);
			localViewContain.removeAllViews();
			remoteViewContain.removeAllViews();
			SurfaceView localVV = VideoHandler.getIns().getLocalCallView();
			SurfaceView remoteVV = VideoHandler.getIns().getRemoteCallView();

			if (null == localVV || null == remoteVV)
			{
				return;
			}

			if (isLocal)
			{
				remoteVV.setZOrderMediaOverlay(false);
				localVV.setZOrderMediaOverlay(true);

				addViewToContain(remoteVV, remoteViewContain);
				addViewToContain(localVV, localViewContain);
			} 
			else
			{
				localVV.setZOrderMediaOverlay(false);
				remoteVV.setZOrderMediaOverlay(true);
				addViewToContain(localVV, localViewContain);
				addViewToContain(remoteVV, remoteViewContain);
			}
			controlRenderVideo(CallCommandParams.MMV_SWITCH_LCLRENDER | CallCommandParams.MMV_SWITCH_RMTRENDER, true);
			controlRenderVideo(CallCommandParams.MMV_SWITCH_CAPTURE, true);
		}
	}

}
