package com.huawei.esdk.te.call;


public interface CallNotification {

	public abstract void onCallComing(Call currentCall);

	public abstract void onCallConnect(Call currentCall);

	public abstract void onCallDestroy(Call currentCall);

	public abstract void onCallend(Call currentCall);

	public abstract void onDataReady(int callId, int bfcpRet);
	
	public abstract void onCallViedoResult(Call currentCall);
	
	public abstract void onCallAddVideo(Call currentCall);

	public abstract void onCallDelViedo(Call currentCall);
	
	public abstract void onSessionModified(Call currentCall);
	
	public abstract void onRingBack(Call currentCall);
}
