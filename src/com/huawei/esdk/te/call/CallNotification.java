package com.huawei.esdk.te.call;



public interface CallNotification {

	//收到来电通知
	public abstract void onCallComing(Call currentCall);

	//通话接通通知
	public abstract void onCallConnect(Call currentCall);

	//已经执行完挂断操作，用于在这里执行数据清理的工
	public abstract void onCallDestroy(Call currentCall);

	//接收到对方挂断消息，但还没有执行挂断操作，用于获取挂断原因
	public abstract void onCallend(Call currentCall);

	//协商结果处理 (用来接收是否有辅流能力)
	public abstract void onDataReady(int callId, int bfcpRet);
	
	//对方音视频转换的响应通知
	public abstract void onCallViedoResult(Call currentCall);
	
	//音视频转换的结果通知
	public abstract void onSessionModified(Call currentCall);
	
	//收到语音转视频请求
	public abstract void onCallAddVideo(Call currentCall);

	//收到视频转语音请求
	public abstract void onCallDelViedo(Call currentCall);

	//响铃通知
	public abstract void onRingBack(Call currentCall);
	
	
	// BFCP辅流（共享）接收开始通知
	public abstract void onDataReceiving(String callId);
	
	// BFCP辅流 (共享) 结束通知
	public abstract void onDataStopped(String callId);
}
